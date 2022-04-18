public class Main {
    public static Customer[] customers;
    public static PickUpEmployee pickUpEmployee;
    public static int numSeats = 3;
    public static Table[] tables = new Table[3];
    public static TableEmployee[] tableEmployees;
    public static void main(String[] args) throws Exception {
        pickUpEmployee = new PickUpEmployee();
        pickUpEmployee.start();

        tableEmployees = new TableEmployee[2];
        //create the tables to pass to employees
        for(int i = 0; i < 3; i++) {
            tables[i] = new Table(null, 3, i);
        }

        // create 2 table employee
        for(int i = 0; i <2; i++) {
            tableEmployees[i] = new TableEmployee(Integer.toString(i), 3, numSeats, tables);
            tableEmployees[i].start();
        }

        customers = new Customer[17];
        // create 17 customer threads
        for (int i = 0; i < 17; i++) {
            customers[i] = new Customer(Integer.toString(i));
            customers[i].start();
        }

    }
}