public class EmployeeStats {
    private int id;
    private String name;
    private int daysWorked;
    private int hoursWorked;
    private int idleHours;
    private double efficiency;

    public EmployeeStats(int id, String name, int daysWorked, int hoursWorked, int idleHours, double efficiency) {
        this.id = id;
        this.name = name;
        this.daysWorked = daysWorked;
        this.hoursWorked = hoursWorked;
        this.idleHours = idleHours;
        this.efficiency = efficiency;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public int getDaysWorked() { return daysWorked; }
    public int getHoursWorked() { return hoursWorked; }
    public int getIdleHours() { return idleHours; }
    public double getEfficiency() { return efficiency; }
}