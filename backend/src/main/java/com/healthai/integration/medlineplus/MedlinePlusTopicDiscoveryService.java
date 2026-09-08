package com.healthai.integration.medlineplus;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Discovers all available MedlinePlus health topics by querying
 * letter-based search terms (a* to z*) and properly paginating
 * through all pages using retstart and retmax.
 *
 * Respects rate limits with a polite delay between API calls.
 * Does NOT import diseases — it discovers clean topic names, canonical URLs,
 * and group names for category mapping.
 */
@Service
public class MedlinePlusTopicDiscoveryService {

    private static final Logger log =
            LoggerFactory.getLogger(MedlinePlusTopicDiscoveryService.class);

    private static final int RETMAX = 50;
    private static final long DELAY_MS = 300;

    private final MedlinePlusClient client;

    public MedlinePlusTopicDiscoveryService(MedlinePlusClient client) {
        this.client = client;
    }

    /**
     * Discovers all MedlinePlus health topics across letters a through z.
     * Fully paginates through each letter until all topics reported by MedlinePlus are retrieved.
     */
    public List<TopicInfo> discoverAllTopics() {

        List<TopicInfo> allTopics = new ArrayList<>();

        for (char letter = 'a'; letter <= 'z'; letter++) {

            String term = letter + "*";
            int retstart = 0;
            int letterTotalDiscovered = 0;

            while (true) {

                log.info("[BulkDiscovery] term={} retstart={} retmax={}", term, retstart, RETMAX);

                try {
                    String xml = client.discoverTopics(term, retstart, RETMAX);

                    int totalAvailableForTerm = parseTotalCount(xml);
                    List<TopicInfo> batch = parseTopicsFromXml(xml);

                    if (batch.isEmpty()) {
                        break;
                    }

                    allTopics.addAll(batch);
                    letterTotalDiscovered += batch.size();
                    retstart += RETMAX;

                    // Stop if we have retrieved all available documents for this term
                    if (totalAvailableForTerm > 0 && retstart >= totalAvailableForTerm) {
                        break;
                    }

                    // Respect rate limits between pagination requests
                    Thread.sleep(DELAY_MS);

                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    log.warn("[BulkDiscovery] Thread interrupted during discovery.");
                    return allTopics;
                } catch (Exception e) {
                    log.error("[BulkDiscovery] Error fetching term={} retstart={}: {}",
                            term, retstart, e.getMessage());
                    break; // Move to next letter if this page fails
                }
            }

            log.info("[BulkDiscovery] Total for {}: {}", term, letterTotalDiscovered);
        }

        log.info("[BulkDiscovery] Total topics discovered across all terms: {}", allTopics.size());
        return allTopics;
    }

    /**
     * Extracts the total number of matching documents from the <list num="..."> attribute.
     */
    private int parseTotalCount(String xml) {
        if (xml == null || xml.isBlank()) {
            return 0;
        }
        try {
            Document document = Jsoup.parse(xml, "", org.jsoup.parser.Parser.xmlParser());
            Element listEl = document.selectFirst("list");
            if (listEl != null && listEl.hasAttr("num")) {
                return Integer.parseInt(listEl.attr("num").trim());
            }
        } catch (Exception e) {
            log.debug("[BulkDiscovery] Could not parse total count attribute: {}", e.getMessage());
        }
        return 0;
    }

    /**
     * Parses a MedlinePlus XML search response and extracts topic info.
     * Cleans search highlighting tags (e.g. <span class="qt0">...</span>) from titles.
     */
    private List<TopicInfo> parseTopicsFromXml(String xml) {

        List<TopicInfo> topics = new ArrayList<>();

        if (xml == null || xml.isBlank()) {
            return topics;
        }

        Document document = Jsoup.parse(
                xml, "", org.jsoup.parser.Parser.xmlParser());

        Elements documents = document.select("document");

        for (Element doc : documents) {

            String url = doc.attr("url").trim();

            if (url.isEmpty()) {
                continue;
            }

            // Extract title and clean any HTML tags (e.g. <span class="qt0">)
            Element titleEl = doc.selectFirst("content[name=title]");
            if (titleEl == null) {
                continue;
            }

            // titleEl.html() contains inner tags like <span class="qt0">Asthma</span>
            // Jsoup.parse().text() cleanly strips all HTML tags to pure text
            String cleanName = Jsoup.parse(titleEl.html()).text().trim();
            if (cleanName.isEmpty()) {
                cleanName = titleEl.text().trim();
            }

            if (cleanName.isEmpty()) {
                continue;
            }

            // Extract all groupName elements
            Elements groupEls = doc.select("content[name=groupName]");
            List<String> groupNames = new ArrayList<>();
            for (Element groupEl : groupEls) {
                String gn = groupEl.text().trim();
                if (!gn.isEmpty()) {
                    groupNames.add(gn);
                }
            }

            topics.add(new TopicInfo(cleanName, url, groupNames));
        }

        return topics;
    }
}
