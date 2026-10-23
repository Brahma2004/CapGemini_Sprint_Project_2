package CRM_Vtiger_BITM_GenericUtility;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Row.MissingCellPolicy;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

public class CRM_Vtiger_BITM_ExcelUtility implements AutoCloseable {

    private Workbook workbook;
    private final DataFormatter formatter = new DataFormatter();

    private static final String DEFAULT_FILE_NAME = "VTiger_Manual_Test_Cases.xlsx";
    private static final String DEFAULT_FILE_PATH = "src/test/resources/" + DEFAULT_FILE_NAME;

    public void openWorkbook() throws IOException {
        openWorkbook(DEFAULT_FILE_NAME);
    }

    public void openWorkbook(String pathOrResourceName) throws IOException {
        if (pathOrResourceName == null || pathOrResourceName.trim().isEmpty()) {
            throw new IllegalArgumentException("Excel file path/resource name cannot be empty.");
        }

        closeWorkbook();

        String cleanName = pathOrResourceName.trim();
        if (cleanName.startsWith("/")) {
            cleanName = cleanName.substring(1);
        }

        String resourcePath = cleanName;
        if (resourcePath.startsWith("src/test/resources/")) {
            resourcePath = resourcePath.replace("src/test/resources/", "");
        }

        InputStream input = getClass().getClassLoader().getResourceAsStream(resourcePath);

        if (input == null) {
            File file = new File(cleanName);
            if (!file.exists()) {
                file = new File(DEFAULT_FILE_PATH);
            }
            if (!file.exists()) {
                file = new File(resourcePath);
            }
            if (file.exists()) {
                input = new FileInputStream(file);
            }
        }

        if (input == null) {
            throw new IOException("Excel file not found in resources or filesystem: " + pathOrResourceName);
        }

        try (InputStream in = input) {
            workbook = WorkbookFactory.create(in);
        } catch (Exception e) {
            throw new IOException("Unable to open Excel workbook: " + pathOrResourceName, e);
        }
    }

    private Sheet getSheet(String sheetName) {
        if (workbook == null) {
            throw new IllegalStateException("Workbook is not open. Call openWorkbook() first.");
        }

        // 1. Direct exact match
        Sheet directSheet = workbook.getSheet(sheetName);
        if (directSheet != null) {
            return directSheet;
        }

        // 2. Fuzzy match: case-insensitive, stripping non-alphanumeric characters (spaces, underscores, etc.)
        String cleanTarget = sheetName.replaceAll("[^a-zA-Z0-9]", "").toLowerCase();
        for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
            Sheet currentSheet = workbook.getSheetAt(i);
            String cleanSheet = currentSheet.getSheetName().replaceAll("[^a-zA-Z0-9]", "").toLowerCase();
            if (cleanSheet.contains(cleanTarget) || cleanTarget.contains(cleanSheet)) {
                return currentSheet;
            }
        }

        // 3. Fallback: list all available sheets if no match is found
        List<String> availableSheets = new ArrayList<>();
        for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
            availableSheets.add(workbook.getSheetName(i));
        }
        throw new IllegalArgumentException("Worksheet not found: '" + sheetName + "'. Available sheets: " + availableSheets);
    }

    private String getCellValue(Row row, int columnIndex) {
        if (row == null) return "";
        Cell cell = row.getCell(columnIndex, MissingCellPolicy.RETURN_BLANK_AS_NULL);
        if (cell == null) return "";
        return formatter.formatCellValue(cell).trim();
    }

    public int getHeaderRowIndex(Sheet sheet) {
        int maxScan = Math.min(5, sheet.getLastRowNum());
        for (int r = 0; r <= maxScan; r++) {
            Row row = sheet.getRow(r);
            if (row == null) continue;
            for (int c = 0; c < row.getLastCellNum(); c++) {
                String val = getCellValue(row, c);
                if (val.equalsIgnoreCase("Test Case ID") || 
                    val.equalsIgnoreCase("Req ID") || 
                    val.equalsIgnoreCase("Scenario ID")) {
                    return r;
                }
            }
        }
        return sheet.getLastRowNum() >= 2 ? 2 : 0;
    }

    private boolean isRowCompletelyEmpty(Row row, int lastColumn) {
        if (row == null) return true;
        for (int c = 0; c < lastColumn; c++) {
            if (!getCellValue(row, c).isEmpty()) {
                return false;
            }
        }
        return true;
    }

    public List<Map<String, String>> getAllDataAsMaps(String sheetName) {
        Sheet sheet = getSheet(sheetName);
        List<Map<String, String>> records = new ArrayList<>();

        int headerRowIndex = getHeaderRowIndex(sheet);
        Row headerRow = sheet.getRow(headerRowIndex);
        if (headerRow == null) {
            throw new IllegalArgumentException("Header row missing in worksheet: " + sheetName);
        }

        int lastColumn = headerRow.getLastCellNum();
        List<String> headers = new ArrayList<>();
        for (int column = 0; column < lastColumn; column++) {
            String header = getCellValue(headerRow, column);
            headers.add(header.isEmpty() ? "Column_" + column : header);
        }

        for (int rowIndex = headerRowIndex + 1; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
            Row row = sheet.getRow(rowIndex);
            if (row == null || isRowCompletelyEmpty(row, lastColumn)) continue;

            Map<String, String> record = new LinkedHashMap<>();
            for (int column = 0; column < headers.size(); column++) {
                record.put(headers.get(column), getCellValue(row, column));
            }
            records.add(record);
        }
        return records;
    }

    public String getCellDataByHeader(String sheetName, int rowNum, String columnName) {
        if (rowNum < 0) {
            throw new IllegalArgumentException("Data row index cannot be negative.");
        }
        if (columnName == null || columnName.trim().isEmpty()) {
            throw new IllegalArgumentException("Column name cannot be empty.");
        }

        Sheet sheet = getSheet(sheetName);
        int headerRowIndex = getHeaderRowIndex(sheet);
        Row headerRow = sheet.getRow(headerRowIndex);
        if (headerRow == null) {
            throw new IllegalArgumentException("Header row is missing in worksheet: " + sheetName);
        }

        int columnIndex = -1;
        for (int column = 0; column < headerRow.getLastCellNum(); column++) {
            String header = getCellValue(headerRow, column);
            if (header.equalsIgnoreCase(columnName.trim())) {
                columnIndex = column;
                break;
            }
        }
        if (columnIndex == -1) {
            throw new IllegalArgumentException("Column '" + columnName + "' not found in worksheet: " + sheetName);
        }

        Row dataRow = sheet.getRow(headerRowIndex + 1 + rowNum);
        if (dataRow == null) return "";
        return getCellValue(dataRow, columnIndex);
    }

    public List<List<String>> getAllData(String sheetName) {
        Sheet sheet = getSheet(sheetName);
        List<List<String>> allRows = new ArrayList<>();

        int lastColumn = 0;
        int headerRowIndex = getHeaderRowIndex(sheet);
        Row headerRow = sheet.getRow(headerRowIndex);
        if (headerRow != null && headerRow.getLastCellNum() > 0) {
            lastColumn = headerRow.getLastCellNum();
        }

        for (int rowIndex = headerRowIndex; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
            Row row = sheet.getRow(rowIndex);
            if (row == null || isRowCompletelyEmpty(row, lastColumn)) continue;

            List<String> rowData = new ArrayList<>();
            for (int column = 0; column < lastColumn; column++) {
                rowData.add(getCellValue(row, column));
            }
            allRows.add(rowData);
        }
        return allRows;
    }

    public void closeWorkbook() throws IOException {
        if (workbook != null) {
            workbook.close();
            workbook = null;
        }
    }

    @Override
    public void close() throws IOException {
        closeWorkbook();
    }
}