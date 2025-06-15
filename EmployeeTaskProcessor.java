import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class EmployeeTaskProcessor {
    private static final int WORKING_DAY_HOURS = 8;

    public List<EmployeeStats> processTasks(List<Employee> employees) {
        ExecutorService executor = Executors.newFixedThreadPool(employees.size());
        List<Future<EmployeeStats>> futures = new ArrayList<>();

        for (Employee employee : employees) {
            Callable<EmployeeStats> task = () -> processEmployee(employee);
            futures.add(executor.submit(task));
        }

        List<EmployeeStats> stats = new ArrayList<>();
        for (Future<EmployeeStats> future : futures) {
            try {
                stats.add(future.get());
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }

        executor.shutdown();
        return stats;
    }

    private EmployeeStats processEmployee(Employee employee) {
        int totalHours = employee.getTotalTaskHours();
        int daysWorked = 0;
        int hoursWorked = 0;
        int idleHours = 0;

        while (totalHours > 0) {
            daysWorked++;
            int hoursToday = Math.min(WORKING_DAY_HOURS, totalHours);
            hoursWorked += hoursToday;
            idleHours += WORKING_DAY_HOURS - hoursToday;
            totalHours -= hoursToday;
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
}