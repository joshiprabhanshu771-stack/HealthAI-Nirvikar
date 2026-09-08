package com.healthai.integration.medlineplus;

import com.healthai.dto.DiseaseImportData;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Parses MedlinePlus pages to extract structured disease information.
 *
 * <p>MedlinePlus has two distinct page types:</p>
 * <ul>
 *   <li><b>NIH Health Topic pages</b> – contain inline "What is...?" / "What are the symptoms...?"
 *       H2/H3 sections with descriptive paragraph content (e.g. /asthma.html, /diabetes.html).</li>
 *   <li><b>Hub / Aggregator pages</b> – contain navigation links to external articles, no inline
 *       medical content (e.g. /acne.html, /malaria.html). These link to MedlinePlus Encyclopedia
 *       articles at /ency/article/ which have direct Causes / Symptoms / Treatment sections.</li>
 * </ul>
 *
 * <p>The parser detects the page type and, for hub pages, identifies the primary
 * encyclopedia article URL so the caller can fetch and re-parse it.</p>
 */
@Component
public class MedlinePlusParser {

    private static final Logger log = LoggerFactory.getLogger(MedlinePlusParser.class);

    // Allowed official domains for article links
    private static final String MEDLINEPLUS_HOST = "medlineplus.gov";
    private static final String NIH_HOST         = "nih.gov";

    // -------------------------------------------------------------------------
    // Public API
    // -------------------------------------------------------------------------

    /**
     * Parse the MedlinePlus search response XML and find the disease page URL.
     * (Used by the single-disease import path.)
     */
    public String extractDiseaseUrl(String xml, String diseaseName) {

        Document document =
                Jsoup.parse(xml, "", org.jsoup.parser.Parser.xmlParser());

        String searchName = diseaseName
                .trim()
                .replaceAll("\\s+", " ")
                .toLowerCase();

        Elements documents = document.select("document");
        log.debug("extractDiseaseUrl: found {} document elements via Jsoup XML for '{}'",
                documents.size(), diseaseName);

        for (Element doc : documents) {

            String url = doc.attr("url").trim();

            Element titleElement = doc.selectFirst("content[name=title]");

            String title = "";

            if (titleElement != null) {
                // Use .html() (same pattern as MedlinePlusTopicDiscoveryService)
                // so that escaped HTML highlight tags (<span class="qt0">) are treated
                // as HTML and stripped cleanly by the second Jsoup.parse() call.
                String rawTitle = titleElement.html();
                title = Jsoup.parse(rawTitle).text()
                        .replaceAll("\\s+", " ")
                        .trim()
                        .toLowerCase();
            }

            if (!url.isEmpty()) {

                if (title.equals(searchName)
                        || title.contains(searchName)
                        || searchName.contains(title)) {

                    log.debug("extractDiseaseUrl: title match '{}' -> {}", title, url);
                    return url;
                }
            }
        }

        // Fallback 1: first document URL (Jsoup found elements but none matched by title)
        if (!documents.isEmpty()) {
            String url = documents.first().attr("url").trim();
            if (!url.isEmpty()) {
                log.debug("extractDiseaseUrl: using first-document fallback -> {}", url);
                return url;
            }
        }

        // Fallback 2: regex on raw XML — handles cases where Jsoup XML parser
        // finds no <document> elements (e.g. encoding / DOCTYPE quirks).
        log.warn("extractDiseaseUrl: Jsoup found 0 documents; trying regex fallback for '{}'", diseaseName);
        Pattern urlPattern = Pattern.compile("url=\"(https://medlineplus\\.gov/[^\"]+)\"");
        Matcher urlMatcher = urlPattern.matcher(xml);
        String firstUrl = null;
        while (urlMatcher.find()) {
            String url = urlMatcher.group(1);
            if (firstUrl == null) firstUrl = url;
            // Slug match: "abdominalpain" vs "abdominal pain"
            String slug = url.replaceAll(".*medlineplus\\.gov/([^./]+).*", "$1").toLowerCase();
            String slugSpaced = slug.replace("-", " ");
            if (slugSpaced.equals(searchName)
                    || searchName.replace(" ", "").equals(slug)
                    || slugSpaced.contains(searchName)
                    || searchName.contains(slugSpaced)) {
                log.debug("extractDiseaseUrl: regex slug match '{}' -> {}", slug, url);
                return url;
            }
        }
        // Absolute last resort: first URL found in the XML
        if (firstUrl != null) {
            log.debug("extractDiseaseUrl: regex first-URL fallback -> {}", firstUrl);
            return firstUrl;
        }

        return null;
    }

    /**
     * Returns {@code true} if the given HTML is a MedlinePlus hub/aggregator page
     * (i.e. does NOT contain inline detailed medical content).
     *
     * <p>Detection heuristic: a Health Topic page always has at least one heading
     * whose text starts with "what is", "what are", "what causes", or "how is",
     * or contains explicit medical section keywords at h2/h3 level that are
     * followed by real paragraph content.</p>
     */
    public boolean isHubPage(String html) {

        Document doc = Jsoup.parse(html);

        // Check for inline "What is / What are / What causes / How is" headings
        Elements headings = doc.select("h1, h2, h3, h4");
        for (Element h : headings) {
            String t = h.text().trim().toLowerCase();
            if (t.startsWith("what is")
                    || t.startsWith("what are")
                    || t.startsWith("what causes")
                    || t.startsWith("how is")
                    || t.startsWith("how are")) {
                // Additionally verify it is followed by paragraph content
                Element next = h.nextElementSibling();
                while (next != null && !next.tagName().matches("h1|h2|h3|h4")) {
                    if (next.tagName().equals("p") && !next.text().isBlank()) {
                        return false; // Definitely a detailed topic page
                    }
                    next = next.nextElementSibling();
                }
            }
        }

        // Check if structured medical sections are directly present with content
        String[] medicalKeywords = {"symptoms", "causes", "treatment", "diagnosis", "prevention"};
        for (Element h : headings) {
            String t = h.text().trim().toLowerCase();
            for (String kw : medicalKeywords) {
                if (t.equals(kw) || t.startsWith(kw + " ") || t.endsWith(" " + kw)) {
                    // Check if followed by real paragraph content (not just nav links)
                    Element next = h.nextElementSibling();
                    while (next != null && !next.tagName().matches("h1|h2|h3|h4")) {
                        if (next.tagName().equals("p") && !next.text().isBlank()) {
                            return false; // Has real content → not a hub
                        }
                        next = next.nextElementSibling();
                    }
                }
            }
        }

        return true; // No inline detailed content found → hub page
    }

    /**
     * Finds the primary detailed MedlinePlus / NIH article URL from a hub page.
     *
     * <p>Strategy (in priority order):</p>
     * <ol>
     *   <li>First {@code /ency/article/} link in the document (MedlinePlus Encyclopedia)</li>
     *   <li>First {@code /ency/} link (patient instructions etc.)</li>
     *   <li>First link in the "Start Here" section that points to medlineplus.gov or nih.gov</li>
     * </ol>
     *
     * <p>Returns {@code null} if no official detailed article is found.</p>
     */
    public String findDetailArticleUrl(String html) {

        Document doc = Jsoup.parse(html);

        // Priority 1: /ency/article/ links (most structured)
        Elements encyArticleLinks = doc.select("a[href~=(?i)medlineplus\\.gov/ency/article/]");
        if (!encyArticleLinks.isEmpty()) {
            return encyArticleLinks.first().absUrl("href");
        }

        // Priority 2: any /ency/ link
        Elements encyLinks = doc.select("a[href~=(?i)medlineplus\\.gov/ency/]");
        if (!encyLinks.isEmpty()) {
            return encyLinks.first().absUrl("href");
        }

        // Priority 3: first official link in Start Here section
        Elements allHeadings = doc.select("h2, h3");
        for (Element heading : allHeadings) {
            String t = heading.text().trim().toLowerCase();
            if (t.contains("start here") || t.contains("basics")) {
                Element sibling = heading.nextElementSibling();
                while (sibling != null && !sibling.tagName().matches("h2|h3")) {
                    for (Element a : sibling.select("a[href]")) {
                        String href = a.absUrl("href");
                        if (isOfficialUrl(href)) {
                            return href;
                        }
                    }
                    sibling = sibling.nextElementSibling();
                }
            }
        }

        return null;
    }

    /**
     * Parse a MedlinePlus disease page (either a Health Topic page or an Encyclopedia
     * article page) and extract all available structured sections.
     *
     * @param html        the raw HTML of the page
     * @param diseaseName the disease name
     * @param sourceUrl   the URL of this page (stored as the medical source)
     */
    public DiseaseImportData parseDiseasePage(
            String html,
            String diseaseName,
            String sourceUrl) {

        Document document = Jsoup.parse(html == null ? "" : html);
        document.select("script, style, noscript, template").remove();

        DiseaseImportData data = new DiseaseImportData();
        data.setName(diseaseName);

        if (sourceUrl != null && !sourceUrl.isBlank()) {
            data.getSources().add(sourceUrl);
        }

        // Overview
        data.setOverview(extractOverview(document));

        // Structured sections — try both Health-Topic and Encyclopedia heading patterns
        data.setSymptoms(extractSection(document, SectionType.SYMPTOMS));
        data.setCauses(extractSection(document, SectionType.CAUSES));
        data.setRiskFactors(extractSection(document, SectionType.RISK_FACTORS));
        data.setDiagnosis(extractSection(document, SectionType.DIAGNOSIS));
        data.setTreatments(extractSection(document, SectionType.TREATMENT));
        data.setPrevention(extractSection(document, SectionType.PREVENTION));
        data.setEmergencySigns(extractSection(document, SectionType.EMERGENCY));

        // Log section results
        log.info("[MedlinePlusImport] Sections extracted for '{}': symptoms={}, causes={}, risks={}, diagnosis={}, treatment={}, prevention={}, emergency={}",
                diseaseName,
                data.getSymptoms().size(),
                data.getCauses().size(),
                data.getRiskFactors().size(),
                data.getDiagnosis().size(),
                data.getTreatments().size(),
                data.getPrevention().size(),
                data.getEmergencySigns().size());

        return data;
    }

    // -------------------------------------------------------------------------
    // Section type enum
    // -------------------------------------------------------------------------

    private enum SectionType {
        SYMPTOMS {
            @Override boolean matches(String heading) {
                return heading.contains("symptom")
                        || heading.contains("signs and symptom")
                        || heading.contains("sign and symptom")
                        || heading.contains("clinical feature");
            }
        },
        CAUSES {
            @Override boolean matches(String heading) {
                return heading.contains("what causes")
                        || (heading.contains("cause") && !heading.contains("because"))
                        || heading.contains("etiology");
            }
        },
        RISK_FACTORS {
            @Override boolean matches(String heading) {
                return heading.contains("at risk")
                        || heading.contains("risk factor")
                        || heading.equals("risks")
                        || heading.contains("who gets")
                        || heading.contains("who is more likely");
            }
        },
        DIAGNOSIS {
            @Override boolean matches(String heading) {
                return heading.contains("diagnos")
                        || heading.contains("exams and test")
                        || heading.contains("exam and test")
                        || heading.contains("tests and diagnosis");
            }
        },
        TREATMENT {
            @Override boolean matches(String heading) {
                return heading.contains("treatment")
                        || heading.contains("therapy")
                        || heading.contains("therapies")
                        || heading.contains("management")
                        || (heading.contains("how is") && heading.contains("treated"));
            }
        },
        PREVENTION {
            @Override boolean matches(String heading) {
                return heading.contains("prevent")
                        || heading.contains("how can i avoid")
                        || heading.contains("reduce your risk");
            }
        },
        EMERGENCY {
            @Override boolean matches(String heading) {
                return heading.contains("emergency")
                        || heading.contains("when to call")
                        || heading.contains("when to contact")
                        || heading.contains("seek medical");
            }
        };

        abstract boolean matches(String lowerHeading);
    }

    // -------------------------------------------------------------------------
    // Private extraction helpers
    // -------------------------------------------------------------------------

    private String extractOverview(Document document) {

        // 1) MedlinePlus Health Topic summary container
        Element topicSummary = document.selectFirst("#topic-summary p, .topic-summary p");
        if (topicSummary != null) {
            String text = cleanText(topicSummary.text());
            if (!text.isEmpty()) return text;
        }

        // 2) Encyclopedia article summary (id="ency_summary", class="ency-box", or #mplus-content)
        Element encySummary = document.selectFirst("#ency_summary p, .ency_summary p, .ency-box p, #mplus-content > p");
        if (encySummary != null) {
            String text = cleanText(encySummary.text());
            if (!text.isEmpty()) return text;
        }

        // 3) "What is / What are / Summary / Overview" style headings
        Elements headings = contentHeadings(document);
        List<String> bestResults = new ArrayList<>();
        for (Element heading : headings) {
            String t = heading.text().trim().toLowerCase();
            if (t.startsWith("what is") || t.startsWith("what are")
                    || t.contains("summary") || t.contains("overview")) {

                Element current = heading.nextElementSibling();
                while (current != null) {
                    if (current.tagName().matches("h1|h2|h3|h4")) break;
                    if (current.tagName().equalsIgnoreCase("p")) {
                        String text = cleanText(current.text());
                        if (!text.isEmpty()) return text;
                    }
                    current = current.nextElementSibling();
                }
            }
        }

        return null;
    }

    /**
     * Extracts content for a given section type by scanning all h1-h4 headings
     * and collecting subsequent paragraph and list-item text until the next heading.
     *
     * <p>Supports both Health Topic ("What are the symptoms of X?") and Encyclopedia
     * ("Symptoms", "Causes", "Treatment") heading styles via the {@link SectionType}
     * keyword matchers, including nested Encyclopedia section structures
     * (e.g. {@code <section><div class="section"><div class="section-title"><h2>...</h2></div><div class="section-body">...</div></div></section>}).</p>
     */
    private List<String> extractSection(Document document, SectionType section) {

        List<String> results = new ArrayList<>();
        List<String> bestResults = new ArrayList<>();

        // First, check Encyclopedia-style sections. Current pages use <div class="section">,
        // while older/alternate pages may use <section> containers.
        Elements encySections = document.select(".section, section");
        for (Element sec : encySections) {
            Element titleEl = sec.selectFirst(".section-title h2, .section-title h3, h2, h3");
            if (titleEl != null) {
                String headingText = titleEl.text().trim().toLowerCase();
                if (section.matches(headingText)) {
                    Element bodyEl = sec.selectFirst(".section-body");
                    if (bodyEl != null) {
                        for (Element p : bodyEl.select("> p, p")) {
                            String text = cleanText(p.text());
                            if (!text.isEmpty()) {
                                results.add(text);
                            }
                        }
                        for (Element item : bodyEl.select("li")) {
                            String text = cleanText(item.text());
                            if (!text.isEmpty()) {
                                results.add(text);
                            }
                        }
                        if (results.size() > bestResults.size()
                                || totalLength(results) > totalLength(bestResults)) {
                            bestResults = new ArrayList<>(results);
                        }
                    }
                }
            }
        }

        // Second, fallback to flat heading + sibling structure (Health Topic style)
        Elements headings = contentHeadings(document);

        for (Element heading : headings) {
            String headingText = heading.text().trim().toLowerCase();

            if (!section.matches(headingText)) {
                continue;
            }

            results = new ArrayList<>();
            collectFollowingContent(heading, results);
            if (results.size() > bestResults.size()
                    || totalLength(results) > totalLength(bestResults)) {
                bestResults = new ArrayList<>(results);
            }

            // Stop at first matching section (avoid duplicate sections from nav)
        }

        return bestResults;
    }

    private int totalLength(List<String> values) {
        return values.stream().mapToInt(String::length).sum();
    }

    private void collectFollowingContent(Element heading, List<String> results) {
        Element current = heading.nextElementSibling();
        while (current != null && !isHeading(current)) {
            collectTextElements(current, results);
            current = current.nextElementSibling();
        }
    }

    private Elements contentHeadings(Document document) {
        Element content = document.selectFirst("#mplus-content, #main-content, main");
        Elements headings = (content != null ? content : document).select("h1, h2, h3, h4");
        headings.removeIf(this::isNavigationHeading);
        return headings;
    }

    private boolean isNavigationHeading(Element heading) {
        return heading.parents().stream().anyMatch(parent ->
                parent.tagName().matches("nav|header|footer|aside")
                        || parent.id().matches(".*(nav|menu|sidebar).*"));
    }

    private void collectTextElements(Element root, List<String> results) {
        if (root.tagName().equalsIgnoreCase("p")
                || root.tagName().equalsIgnoreCase("li")) {
            addCleanText(results, root.text());
            return;
        }

        for (Element child : root.children()) {
            if (isHeading(child)) {
                break;
            }
            collectTextElements(child, results);
        }
    }

    private boolean isHeading(Element element) {
        return element.tagName().matches("h1|h2|h3|h4");
    }

    private void addCleanText(List<String> results, String rawText) {
        String text = cleanText(rawText);
        if (!text.isEmpty() && !results.contains(text)) {
            results.add(text);
        }
    }

    private String cleanText(String rawText) {
        if (rawText == null) {
            return "";
        }
        return Jsoup.parse(rawText)
                .text()
                .replaceAll("\\s+", " ")
                .trim();
    }

    /**
     * Returns {@code true} if the URL belongs to an official MedlinePlus or NIH domain.
     */
    private boolean isOfficialUrl(String url) {
        if (url == null || url.isBlank()) return false;
        return url.contains(MEDLINEPLUS_HOST) || url.contains(NIH_HOST);
    }
}