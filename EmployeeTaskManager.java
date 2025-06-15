import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class EmployeeTaskManager {
    private static final int WORKING_DAY_HOURS = 8;
    private static final String INPUT_FILE = "employees.xlsx";
    private static final String OUTPUT_FILE = "employee_stats.xlsx";

    public static void main(String[] args) {
        try {
            // Чтение данных сотрудников из Excel
            List<Employee> employees = readEmployeesFromFile(INPUT_FILE);
            if (employees.isEmpty()) {
                throw new IOException("Файл employees.xlsx пуст или не содержит данных");
            }

            //Многопоточная обработка задач сотрудников
            ExecutorService executor = Executors.newFixedThreadPool(employees.size());
            List<Future<EmployeeStats>> futures = new ArrayList<>();

            for (Employee employee : employees) {
                futures.add(executor.submit(() -> processEmployeeTasks(employee)));
            }

            //  Сбор результатов
            List<EmployeeStats> stats = new ArrayList<>();
            for (Future<EmployeeStats> future : futures) {
                stats.add(future.get());
            }

            executor.shutdown();

            //  Сохранение статистики в Excel
            writeStatsToFile(stats, OUTPUT_FILE);
            System.out.println("Обработка завершена. Результаты сохранены в " + OUTPUT_FILE);

            // Запуск дополнительной задачи
            System.out.println("\n=== Дополнительная задача ===");
            calculateFibonacciInParallel(10); // Расчет 10 первых чисел Фибоначчи

        } catch (Exception e) {
            System.err.println("Ошибка: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Метод для обработки задач одного сотрудника
    private static EmployeeStats processEmployeeTasks(Employee employee) {
        int totalTaskHours = employee.getTotalTaskHours();
        int daysWorked = 0;
        int hoursWorked = 0;
        int idleHours = 0;

        while (totalTaskHours > 0) {
            daysWorked++;
            int hoursToday = Math.min(WORKING_DAY_HOURS, totalTaskHours);
            hoursWorked += hoursToday;
            idleHours += WORKING_DAY_HOURS - hoursToday;
            totalTaskHours -= hoursToday;
        }

        double efficiency = (double) hoursWorked / (daysWorked * WORKING_DAY_HOURS) * 100;
        return new EmployeeStats(
                employee.getId(),
                employee.getName(),
                daysWorked,
                hoursWorked,
                idleHours,
                efficiency
        );
    }

    // Дополнительная задача: многопоточный расчет чисел Фибоначчи
    public static void calculateFibonacciInParallel(int n) {
        ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        List<Future<Long>> futures = new ArrayList<>();

        System.out.println("Многопоточный расчет первых " + n + " чисел Фибоначчи:");

        // Запуск задач в отдельных потоках
        for (int i = 0; i < n; i++) {
            final int num = i;
            Callable<Long> task = () -> {
                long result = fibonacci(num);
                System.out.printf("[Поток %s] F(%d) = %d\n",
                        Thread.currentThread().getName(), num, result);
                return result;
            };
            futures.add(executor.submit(task));
        }

        // Ожидание завершения и вывод результатов
        System.out.println("\nРезультаты:");
        for (int i = 0; i < futures.size(); i++) {
            try {
                System.out.printf("F(%d) = %d\n", i, futures.get(i).get());
            } catch (InterruptedException | ExecutionException e) {
                System.err.println("Ошибка при расчете F(" + i + "): " + e.getMessage());
            }
        }

        executor.shutdown();
        System.out.println("=== Расчет завершен ===");
    }

    // Рекурсивный метод вычисления чисел Фибоначчи
    private static long fibonacci(int n) {
        if (n <= 1) return n;
        return fibonacci(n - 1) + fibonacci(n - 2);
    }

    // Метод чтения данных из Excel
    private static List<Employee> readEmployeesFromFile(String filename) throws IOException {
        List<Employee> employees = new ArrayList<>();

        try (FileInputStream file = new FileInputStream(filename);
             Workbook workbook = new XSSFWorkbook(file)) {

            Sheet sheet = workbook.getSheetAt(0);
            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue; // Пропускаем заголовок

                int id = (int) row.getCell(0).getNumericCellValue();
                String name = row.getCell(1).getStringCellValue();
                int totalHours = (int) row.getCell(2).getNumericCellValue();

                employees.add(new Employee(id, name, totalHours));
            }
        }
        return employees;
    }

    // Метод записи статистики в Excel
    private static void writeStatsToFile(List<EmployeeStats> stats, String filename) throws IOException {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Статистика");

            // Заголовки
            Row headerRow = sheet.createRow(0);
            String[] headers = {"ID", "Имя", "Дней работы", "Часов работы", "Часов простоя", "Эффективность (%)"};
            for (int i = 0; i < headers.length; i++) {
                headerRow.createCell(i).setCellValue(headers[i]);
            }

            // Данные
            for (int i = 0; i < stats.size(); i++) {
                EmployeeStats stat = stats.get(i);
                Row row = sheet.createRow(i + 1);

                row.createCell(0).setCellValue(stat.getId());
                row.createCell(1).setCellValue(stat.getName());
                row.createCell(2).setCellValue(stat.getDaysWorked());
                row.createCell(3).setCellValue(stat.getHoursWorked());
                row.createCell(4).setCellValue(stat.getIdleHours());
                row.createCell(5).setCellValue(stat.getEfficiency());
            }

            // Автоподбор ширины столбцов
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            try (FileOutputStream out = new FileOutputStream(filename)) {
                workbook.write(out);
            }
        }
    }

}