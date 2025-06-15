public class Employee {
    private final int id;
    private final String name;
    private final int totalTaskHours;

    public Employee(int id, String name, int totalTaskHours) {
        this.id = id;
        this.name = name;
        this.totalTaskHours = totalTaskHours;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public int getTotalTaskHours() { return totalTaskHours; }
}