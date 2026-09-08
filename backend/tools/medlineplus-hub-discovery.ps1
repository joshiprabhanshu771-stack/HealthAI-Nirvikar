[CmdletBinding()]
param(
    [string]$OutputDirectory = "reports/medlineplus-hub-discovery",
    [int]$DelayMilliseconds = 300,
    [int]$TimeoutSeconds = 30,
    [string]$MysqlDatabase = "healthai_db",
    [string]$MysqlUser = "root",
    [string]$MysqlPassword = "root"
)

Set-StrictMode -Version Latest
$ErrorActionPreference = "Stop"

# This utility only executes a SELECT query and HTTP GET requests.
$root = Resolve-Path (Join-Path $PSScriptRoot "..\..")
$outputPath = Join-Path $root $OutputDirectory
New-Item -ItemType Directory -Force -Path $outputPath | Out-Null
$cachePath = Join-Path $outputPath "cache"
New-Item -ItemType Directory -Force -Path $cachePath | Out-Null

$query = @"
SELECT DISTINCT d.id, d.name, ms.url
FROM diseases d
JOIN disease_sources ds ON ds.disease_id = d.id
JOIN medical_sources ms ON ms.id = ds.source_id
WHERE ms.url LIKE 'https://medlineplus.gov/%.html'
  AND ms.url NOT LIKE 'https://medlineplus.gov/ency/article/%'
ORDER BY d.id
"@

$mysqlArgs = @("--batch", "--raw", "--skip-column-names", "-u$MysqlUser", "-p$MysqlPassword", $MysqlDatabase, "-e", $query)
$rows = & mysql @mysqlArgs
if ($LASTEXITCODE -ne 0) {
    throw "MySQL SELECT failed with exit code $LASTEXITCODE."
}

# Only explicitly listed equivalences may produce APPROVED_SYNONYM.
$approvedSynonyms = @{
    "high blood pressure" = @("hypertension")
    "hypertension" = @("high blood pressure")
    "irregular heartbeat" = @("arrhythmia")
    "arrhythmia" = @("irregular heartbeat")
}

$nonDiseaseTerms = @(
    "medicare", "choosing a doctor", "health care service", "advanced directives",
    "assisted living", "assistive devices", "artificial limbs"
)

function Normalize-Name([string]$value) {
    if ([string]::IsNullOrWhiteSpace($value)) { return "" }
    $decoded = [System.Net.WebUtility]::HtmlDecode($value)
    $decoded = $decoded -replace "\|\s*MedlinePlus.*$", ""
    $decoded = $decoded -replace ":\s*MedlinePlus.*$", ""
    $decoded = $decoded -replace "-\s*MedlinePlus.*$", ""
    $decoded = $decoded -replace "\s+MedlinePlus.*$", ""
    $decoded = $decoded -replace "\([^)]*\)", ""
    $decoded = $decoded.ToLowerInvariant() -replace "[^a-z0-9 ]", " "
    return (($decoded -replace "\s+", " ").Trim())
}

function Get-Title([string]$html) {
    $match = [regex]::Match($html, '<title[^>]*>(.*?)</title>', [Text.RegularExpressions.RegexOptions]::IgnoreCase -bor [Text.RegularExpressions.RegexOptions]::Singleline)
    if (-not $match.Success) { return "" }
    return ([System.Net.WebUtility]::HtmlDecode(($match.Groups[1].Value -replace '<[^>]+>', ' ')) -replace "\s+", " ").Trim()
}

function Get-PageType([string]$html, [string]$title) {
    $headingTexts = [regex]::Matches($html, '<h[1-4][^>]*>(.*?)</h[1-4]>', [Text.RegularExpressions.RegexOptions]::IgnoreCase -bor [Text.RegularExpressions.RegexOptions]::Singleline) |
        ForEach-Object { (($_.Groups[1].Value -replace '<[^>]+>', ' ') -replace "\s+", " ").Trim().ToLowerInvariant() }
    $medicalHeading = $headingTexts | Where-Object { $_ -match 'what is|what are|what causes|who is at risk|symptom|diagnos|treatment|therap|prevent|risk|emergency|when to|seek medical' }
    if ($medicalHeading) { return "INLINE_TOPIC" }
    if ($title -match 'medicare|doctor|health care service|directive|living|device') { return "GENERAL_NON_DISEASE" }
    return "HUB_OR_GENERAL"
}

function Get-OfficialArticleLinks([string]$html, [string]$baseUrl) {
    $links = New-Object System.Collections.Generic.List[string]
    $matches = [regex]::Matches($html, '<a\b[^>]*href=["'']([^"'']+)["''][^>]*>', [Text.RegularExpressions.RegexOptions]::IgnoreCase)
    foreach ($match in $matches) {
        $href = [System.Net.WebUtility]::HtmlDecode($match.Groups[1].Value).Trim()
        if ($href.StartsWith("//")) { $href = "https:" + $href }
        elseif ($href.StartsWith("/")) { $href = "https://medlineplus.gov" + $href }
        try { $uri = [Uri]$href } catch { continue }
        if ($uri.Host -ne "medlineplus.gov") { continue }
        if ($uri.AbsolutePath -notmatch '^/ency/article/[^/]+\.htm$') { continue }
        if (-not $links.Contains($uri.AbsoluteUri)) { [void]$links.Add($uri.AbsoluteUri) }
    }
    return @($links)
}

function Get-CachedPage([string]$url) {
    $key = [Convert]::ToBase64String([Text.Encoding]::UTF8.GetBytes($url)).TrimEnd('=').Replace('/','_').Replace('+','-')
    $file = Join-Path $cachePath ($key + ".html")
    if (Test-Path $file) { return [IO.File]::ReadAllText($file) }
    try {
        $response = Invoke-WebRequest -Uri $url -UseBasicParsing -TimeoutSec $TimeoutSeconds -Headers @{ 'User-Agent' = 'HealthAI-Nirvikar-read-only-discovery' }
        [IO.File]::WriteAllText($file, $response.Content)
        Start-Sleep -Milliseconds $DelayMilliseconds
        return $response.Content
    } catch {
        throw $_
    }
}

$candidateTitleCache = @{}

function Resolve-Match([string]$diseaseName, [array]$candidates) {
    $diseaseKey = Normalize-Name $diseaseName
    $matches = @()
    foreach ($candidate in $candidates) {
        $candidateKey = Normalize-Name $candidate.Title
        $matchType = ""
        if ($candidateKey -eq $diseaseKey) { $matchType = "EXACT" }
        elseif ($approvedSynonyms.ContainsKey($diseaseKey) -and ($approvedSynonyms[$diseaseKey] -contains $candidateKey)) { $matchType = "APPROVED_SYNONYM" }
        if ($matchType) { $matches += [pscustomobject]@{ Candidate = $candidate; MatchType = $matchType } }
    }
    if ($matches.Count -eq 1) { return $matches[0] }
    if ($matches.Count -gt 1) { return [pscustomobject]@{ Candidate = $matches[0].Candidate; MatchType = "AMBIGUOUS" } }
    return $null
}

$results = New-Object System.Collections.Generic.List[object]
$index = 0
foreach ($row in $rows) {
    if ([string]::IsNullOrWhiteSpace($row)) { continue }
    $parts = $row -split "`t", 3
    if ($parts.Count -ne 3) { continue }
    $index++
    $id = $parts[0]
    $diseaseName = $parts[1]
    $currentUrl = $parts[2]
    Write-Progress -Activity "MedlinePlus read-only discovery" -Status "$index / $($rows.Count): $diseaseName" -PercentComplete (($index / [Math]::Max(1, $rows.Count)) * 100)
    $base = [ordered]@{ diseaseId = $id; diseaseName = $diseaseName; currentUrl = $currentUrl; httpStatus = $null; pageTitle = ""; pageType = ""; candidateArticleUrl = ""; candidateArticleTitle = ""; matchType = ""; confidenceReason = ""; fetchFailure = $null }
    try {
        $html = Get-CachedPage $currentUrl
        $base.httpStatus = 200
        $base.pageTitle = Get-Title $html
        $base.pageType = Get-PageType $html $base.pageTitle
        $links = Get-OfficialArticleLinks $html $currentUrl
        $candidates = New-Object System.Collections.Generic.List[object]
        # Inline topic pages are already direct content; their encyclopedia links
        # are related subtopics, not replacement candidates. Fetch candidates only
        # for hub/general pages where discovery is actually required.
        $candidateLinks = if ($base.pageType -eq "INLINE_TOPIC") { @() } else { $links }
        foreach ($link in $candidateLinks) {
            if ($candidateTitleCache.ContainsKey($link)) {
                [void]$candidates.Add($candidateTitleCache[$link])
                continue
            }
            try {
                $articleHtml = Get-CachedPage $link
                $candidate = [pscustomobject]@{ Url = $link; Title = (Get-Title $articleHtml) }
                $candidateTitleCache[$link] = $candidate
                [void]$candidates.Add($candidate)
            } catch { }
        }
        $resolved = $null
        if ($candidates.Count -gt 0) {
            $resolved = Resolve-Match -diseaseName $diseaseName -candidates @($candidates.ToArray())
        }
        if ($resolved) {
            $base.candidateArticleUrl = $resolved.Candidate.Url
            $base.candidateArticleTitle = $resolved.Candidate.Title
            $base.matchType = $resolved.MatchType
            $base.confidenceReason = if ($resolved.MatchType -eq "EXACT") { "Candidate title exactly matches normalized disease name." } elseif ($resolved.MatchType -eq "APPROVED_SYNONYM") { "Candidate title matches an explicitly configured synonym." } else { "More than one exact or approved candidate exists." }
        } elseif ($base.pageType -eq "INLINE_TOPIC") {
            $base.matchType = "NO_MATCH"
            $base.confidenceReason = "Stored URL is already an inline detailed MedlinePlus topic; no replacement article is needed."
        } elseif ($base.pageType -eq "GENERAL_NON_DISEASE" -or ($nonDiseaseTerms | Where-Object { (Normalize-Name $diseaseName) -like "*$_*" })) {
            $base.matchType = "NON_DISEASE"
            $base.confidenceReason = "Topic title/name identifies general service or non-disease content."
        } elseif ($candidates.Count -gt 0) {
            $base.matchType = "NO_MATCH"
            $base.confidenceReason = "Official encyclopedia links exist, but none exactly or synonym-match the disease name."
        } else {
            $base.matchType = "NO_MATCH"
            $base.confidenceReason = "No official MedlinePlus encyclopedia article links were found."
        }
    } catch {
        $base.matchType = "FETCH_FAILURE"
        $base.confidenceReason = $_.Exception.Message
        $base.fetchFailure = $_.Exception.Message
    }
    [void]$results.Add([pscustomobject]$base)
}
Write-Progress -Activity "MedlinePlus read-only discovery" -Completed

$results | ConvertTo-Json -Depth 5 | Set-Content (Join-Path $outputPath "report.json") -Encoding UTF8
$results | Export-Csv (Join-Path $outputPath "report.csv") -NoTypeInformation -Encoding UTF8
$summary = [ordered]@{
    totalRecordsAnalyzed = $results.Count
    exactMatches = @($results | Where-Object matchType -eq "EXACT").Count
    approvedSynonymMatches = @($results | Where-Object matchType -eq "APPROVED_SYNONYM").Count
    noMatch = @($results | Where-Object matchType -eq "NO_MATCH").Count
    ambiguous = @($results | Where-Object matchType -eq "AMBIGUOUS").Count
    nonDiseaseGeneralTopics = @($results | Where-Object matchType -eq "NON_DISEASE").Count
    fetchFailures = @($results | Where-Object matchType -eq "FETCH_FAILURE").Count
    generatedAtUtc = [DateTime]::UtcNow.ToString("o")
    readOnly = $true
    candidatePolicy = "Exact normalized title or explicitly configured synonym only"
}
$summary | ConvertTo-Json | Set-Content (Join-Path $outputPath "summary.json") -Encoding UTF8
$summary | Format-Table | Out-String | Set-Content (Join-Path $outputPath "summary.txt") -Encoding UTF8
Write-Output ($summary | ConvertTo-Json -Compress)
Write-Output "Reports written to $outputPath"
