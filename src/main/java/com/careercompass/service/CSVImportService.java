package com.careercompass.service;

import java.io.InputStream;

/**
 * CSV import service interface for batch cutoff data upload
 */
public interface CSVImportService {

    int importCutoffData(InputStream inputStream);

    int importCutoffDataFromFile(String filePath);

    void validateCSVStructure(InputStream inputStream);

    boolean validateCutoffRecord(String[] record);

    int getProcessedRecords();

    int getSkippedRecords();

    String getLastImportSummary();
}
