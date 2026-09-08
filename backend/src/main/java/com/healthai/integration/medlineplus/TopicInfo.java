package com.healthai.integration.medlineplus;

import java.util.List;

/**
 * Represents a discovered MedlinePlus health topic,
 * obtained from the search API XML response.
 */
public class TopicInfo {

    private String name;
    private String url;
    private List<String> groupNames;

    public TopicInfo(String name, String url, List<String> groupNames) {
        this.name = name;
        this.url = url;
        this.groupNames = groupNames;
    }

    public String getName() {
        return name;
    }

    public String getUrl() {
        return url;
    }

    public List<String> getGroupNames() {
        return groupNames;
    }
}
