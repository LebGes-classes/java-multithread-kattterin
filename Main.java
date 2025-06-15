import java.io.IOException;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        try {
            // Chteniye dannykh
            List<Employee> employees = EmployeeExcelReader.readEmployees("employees.xlsx");

            // Obrabotka
            EmployeeTaskProcessor processor = new EmployeeTaskProcessor();
            List<EmployeeStats> stats = processor.processTasks(employees);

            // Sokhraneniye
            EmployeeExcelWriter.writeStats(stats, "employee_stats.xlsx");

            System.out.println("Processing complete. File saved: employee_stats.xlsx");
        } catch (IOException e) {
            System.err.println("ERROR: " + e.getMessage());
            e.printStackTrace();
        }
    }
}