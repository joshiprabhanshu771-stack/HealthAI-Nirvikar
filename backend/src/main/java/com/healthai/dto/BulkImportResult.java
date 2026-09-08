package com.healthai.dto;

import java.util.ArrayList;
import java.util.List;

/**
 * Summary result returned after a bulk disease import operation.
 */
public class BulkImportResult {

    private int totalDiscovered;
    private int alreadyExists;
    private int imported;
    private int updated;
    private int failed;
    private String message;
    private List<String> failedDiseases = new ArrayList<>();

    public int getTotalDiscovered() {
        return totalDiscovered;
    }

    public void setTotalDiscovered(int totalDiscovered) {
        this.totalDiscovered = totalDiscovered;
    }

    public int getAlreadyExists() {
        return alreadyExists;
    }

    public void setAlreadyExists(int alreadyExists) {
        this.alreadyExists = alreadyExists;
    }

    public int getImported() {
        return imported;
    }

    public void setImported(int imported) {
        this.imported = imported;
    }

    public int getUpdated() {
        return updated;
    }

    public void setUpdated(int updated) {
        this.updated = updated;
    }

    public int getFailed() {
        return failed;
    }

    public void setFailed(int failed) {
        this.failed = failed;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<String> getFailedDiseases() {
        return failedDiseases;
    }

    public void setFailedDiseases(List<String> failedDiseases) {
        this.failedDiseases = failedDiseases;
    }
}
