package com.careercompass.service.impl;

import com.careercompass.entity.*;
import com.careercompass.exception.InvalidCSVException;
import com.careercompass.exception.ResourceNotFoundException;
import com.careercompass.repository.*;
import com.careercompass.service.CSVImportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * Implementation of CSVImportService for batch cutoff data upload
 *
 * Expected CSV Format:
 * exam_name,college_name,branch_name,category,gender,quota,round,opening_rank,closing_rank,year
 */
@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class CSVImportServiceImpl implements CSVImportService {

    private final CutoffRepository cutoffRepository;
    private final ExamRepository examRepository;
    private final CollegeRepository collegeRepository;
    private final BranchRepository branchRepository;

    private int processedRecords = 0;
    private int skippedRecords = 0;
    private StringBuilder importSummary = new StringBuilder();

    @Override
    public int importCutoffData(InputStream inputStream) {
        processedRecords = 0;
        skippedRecords = 0;
        importSummary = new StringBuilder();

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(inputStream, StandardCharsets.UTF_8));
             CSVParser csvParser = new CSVParser(reader,
                     CSVFormat.DEFAULT.builder()
                             .setHeader()
                             .setSkipHeaderRecord(true)
                             .setIgnoreEmptyLines(true)
                             .setTrim(true)
                             .build())) {

            validateHeaderMap(csvParser.getHeaderMap());

            for (CSVRecord record : csvParser) {
                try {
                    if (importCutoffRecord(record)) {
                        processedRecords++;
                    } else {
                        skippedRecords++;
                    }
                } catch (Exception e) {
                    log.warn("Error processing record: {}", record, e);
                    skippedRecords++;
                    importSummary.append("\nSkipped record due to: ").append(e.getMessage());
                }
            }
        } catch (IOException e) {
            log.error("Error reading CSV file", e);
            throw new InvalidCSVException("Error reading CSV file: " + e.getMessage());
        }

        importSummary.append("\n=== Import Summary ===\n")
                .append("Processed: ").append(processedRecords).append("\n")
                .append("Skipped: ").append(skippedRecords);

        log.info("CSV import completed. Processed: {}, Skipped: {}", processedRecords, skippedRecords);
        return processedRecords;
    }

    @Override
    public int importCutoffDataFromFile(String filePath) {
        try {
            InputStream inputStream = new FileInputStream(filePath);
            return importCutoffData(inputStream);
        } catch (FileNotFoundException e) {
            throw new InvalidCSVException("File not found: " + filePath);
        }
    }

    @Override
    public void validateCSVStructure(InputStream inputStream) {
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(inputStream, StandardCharsets.UTF_8));
            CSVParser csvParser = new CSVParser(reader,
                     CSVFormat.DEFAULT.builder()
                             .setHeader()
                             .setSkipHeaderRecord(true)
                             .setTrim(true)
                             .build())) {

            validateHeaderMap(csvParser.getHeaderMap());
        } catch (IOException e) {
            throw new InvalidCSVException("Error validating CSV: " + e.getMessage());
        }
    }

    @Override
    public boolean validateCutoffRecord(String[] record) {
        if (record.length < 10) {
            return false;
        }

        try {
            Integer.parseInt(record[7]); // opening_rank
            Integer.parseInt(record[8]); // closing_rank
            Integer.parseInt(record[9]); // year
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    @Override
    public int getProcessedRecords() {
        return processedRecords;
    }

    @Override
    public int getSkippedRecords() {
        return skippedRecords;
    }

    @Override
    public String getLastImportSummary() {
        return importSummary.toString();
    }

    /**
     * Import a single cutoff record from CSV
     */
    private boolean importCutoffRecord(CSVRecord record) {
        String examName = record.get("exam_name");
        String collegeName = record.get("college_name");
        String branchName = record.get("branch_name");
        String category = record.get("category");
        String gender = record.get("gender");
        String quota = record.get("quota");
        String round = record.get("round");
        String openingRank = record.get("opening_rank");
        String closingRank = record.get("closing_rank");
        String year = record.get("year");

        try {
            // Validate fields
            if (examName.isBlank() || collegeName.isBlank() || branchName.isBlank()) {
                importSummary.append("\nSkipped: Missing required fields");
                return false;
            }

            int roundNumber = Integer.parseInt(round);
            int opening = Integer.parseInt(openingRank);
            int closing = Integer.parseInt(closingRank);
            int cutoffYear = Integer.parseInt(year);

            if (roundNumber <= 0 || opening <= 0 || closing <= 0 || cutoffYear <= 0) {
                importSummary.append("\nSkipped: ranks, round, and year must be positive");
                return false;
            }

            if (opening > closing) {
                importSummary.append("\nSkipped: opening rank cannot be greater than closing rank");
                return false;
            }

            // Find or throw exception for related entities
            Exam exam = examRepository.findByExamType(parseExamType(examName))
                    .orElseThrow(() -> new ResourceNotFoundException("Exam not found: " + examName));

            College college = collegeRepository.findByNameContainingIgnoreCase(collegeName).stream()
                    .findFirst()
                    .orElseThrow(() -> new ResourceNotFoundException("College not found: " + collegeName));

            Branch branch = branchRepository.findByBranchCode(branchName)
                    .or(() -> branchRepository.findByNameContainingIgnoreCase(branchName).stream().findFirst())
                    .orElseThrow(() -> new ResourceNotFoundException("Branch not found: " + branchName));

            Category parsedCategory = parseCategory(category);
            Gender parsedGender = parseGender(gender);
            Quota parsedQuota = parseQuota(quota);

            boolean duplicate = cutoffRepository.existsByExamIdAndCollegeIdAndBranchIdAndCategoryAndGenderAndQuotaAndRoundNumberAndYear(
                    exam.getId(), college.getId(), branch.getId(), parsedCategory, parsedGender, parsedQuota, roundNumber, cutoffYear);
            if (duplicate) {
                importSummary.append("\nSkipped duplicate cutoff for ")
                        .append(examName).append(" / ").append(collegeName).append(" / ").append(branchName);
                return false;
            }

            // Create cutoff record
            Cutoff cutoff = Cutoff.builder()
                    .exam(exam)
                    .college(college)
                    .branch(branch)
                    .category(parsedCategory)
                    .gender(parsedGender)
                    .quota(parsedQuota)
                    .roundNumber(roundNumber)
                    .openingRank(opening)
                    .closingRank(closing)
                    .year(cutoffYear)
                    .build();

            cutoffRepository.save(cutoff);
            return true;

        } catch (Exception e) {
            log.debug("Error importing record: {}", e.getMessage());
            return false;
        }
    }

    private void validateHeaderMap(Map<String, Integer> headerMap) {
        String[] headers = {"exam_name", "college_name", "branch_name", "category",
                "gender", "quota", "round", "opening_rank", "closing_rank", "year"};

        if (headerMap.isEmpty()) {
            throw new InvalidCSVException("CSV file has no header row");
        }

        for (String header : headers) {
            if (!headerMap.containsKey(header)) {
                throw new InvalidCSVException("Missing required column: " + header);
            }
        }
    }

    private ExamType parseExamType(String rawExamName) {
        String normalized = rawExamName.trim().replace('-', '_').replace(' ', '_').toUpperCase();
        for (ExamType examType : ExamType.values()) {
            if (examType.name().equals(normalized)
                    || examType.getDisplayName().equalsIgnoreCase(rawExamName.trim())) {
                return examType;
            }
        }
        throw new ResourceNotFoundException("Unsupported exam type: " + rawExamName);
    }

    private Category parseCategory(String rawCategory) {
        String normalized = rawCategory.trim().replace('-', '_').replace(' ', '_').toUpperCase();
        return switch (normalized) {
            case "OPEN", "GENERAL", "GEN" -> Category.GENERAL;
            case "OBC_NCL", "OBC" -> Category.OBC;
            case "GEN_EWS", "EWS" -> Category.EWS;
            case "SC" -> Category.SC;
            case "ST" -> Category.ST;
            default -> Category.valueOf(normalized);
        };
    }

    private Gender parseGender(String rawGender) {
        String normalized = rawGender.trim().replace('-', '_').replace(' ', '_').toUpperCase();
        return switch (normalized) {
            case "GENDER_NEUTRAL", "GENDER_NEUTRAL_ONLY", "NEUTRAL", "ANY" -> Gender.GENDER_NEUTRAL;
            case "FEMALE_ONLY", "FEMALE" -> Gender.FEMALE;
            case "MALE" -> Gender.MALE;
            default -> Gender.valueOf(normalized);
        };
    }

    private Quota parseQuota(String rawQuota) {
        String normalized = rawQuota.trim().replace('-', '_').replace(' ', '_').toUpperCase();
        return switch (normalized) {
            case "AI", "OS", "OPEN_SEAT_QUOTA", "ALL_INDIA", "ALL_INDIA_QUOTA" -> Quota.ALL_INDIA;
            case "HS", "HOME_STATE", "STATE_QUOTA", "STATE" -> Quota.HOME_STATE;
            case "FOREIGN", "NRI" -> Quota.FOREIGN;
            default -> Quota.valueOf(normalized);
        };
    }
}
