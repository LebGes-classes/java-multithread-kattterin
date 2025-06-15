
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class EmployeeExcelWriter {
    public static void writeStats(List<EmployeeStats> stats, String filePath) throws IOException {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Статистика");

            // Sozdayem tol'ko stroki dlya real'nykh dannykh
            createHeaderRow(sheet, workbook);

            // Zapisyvayem tol'ko dannyye sotrudnikov (bez pustykh strok)
            for (int i = 0; i < stats.size(); i++) {
                createDataRow(sheet, stats.get(i), i + 1);
            }

            // Avtopodbor shiriny stolbtsov
            for (int i = 0; i < 6; i++) {
                sheet.autoSizeColumn(i);
            }

            // Sokhraneniye fayla
            try (FileOutputStream out = new FileOutputStream(filePath)) {
                workbook.write(out);
            }
        }
    }

    private static void createHeaderRow(Sheet sheet, Workbook workbook) {
        Row headerRow = sheet.createRow(0);
        String[] headers = {"ID", "Имя", "Дней работы", "Часов работы", "Часов простоя", "Эффективность (%)"};

        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
        }
    }

    private static void createDataRow(Sheet sheet, EmployeeStats stat, int rowNum) {
        Row row = sheet.createRow(rowNum);

        row.createCell(0).setCellValue(stat.getId());
        row.createCell(1).setCellValue(stat.getName());
        row.createCell(2).setCellValue(stat.getDaysWorked());
        row.createCell(3).setCellValue(stat.getHoursWorked());
        row.createCell(4).setCellValue(stat.getIdleHours());

        // Formatiruyem protsenty
        Cell percentCell = row.createCell(5);
        percentCell.setCellValue(stat.getEfficiency() / 100); // Excel хранит проценты как десятичные дроби
    }
}