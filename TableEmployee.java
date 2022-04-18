import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

public class TableEmployee implements Runnable {
    private Thread thread;
    public static Queue customersInLine = new LinkedList<Customer>();
    private Table[] sharedListOfTables = new Table[3];
    private ArrayList<Table> thisEmployeeTables = new ArrayList<>();
    private String employeeNum;
    private boolean isTableAvailableToAssign = false;
    private boolean isAvailable = true; // employee is busy when serving table(bring food, wait for cust. finish. cust pay)
    private volatile static int customersSeated = 0;
    private boolean isTableBecomeAvailable = false;
    private volatile int countTables = 1;
    private volatile int zcountTables = 1;
    private volatile int customersReadyToPay = 0;
    public static int countCustomersThatLeft = 0;
    private volatile int customerPartionedInLine = 0;
    private volatile int totalCustomersInDineInLine;
    public static long time = System.currentTimeMillis();


    public TableEmployee(String employeeNum, int numOfTables, int numSeats, Table[] tables) {
        this.thread = new Thread(this);
        sharedListOfTables = tables; // this is all the tables in the restaurant that all employees looks at
        this.employeeNum = employeeNum;
    }

    public void start() {
        thread.start();
    }

    // template method of DineIn Employee
    public void run() {
        try{ Thread.sleep(6000);} catch(Exception e){}
        totalCustomersInDineInLine = customersInLine.size();

        setCustomerCount();

        // this while loop goes until all customers are served and leaves
        while (countCustomersThatLeft < totalCustomersInDineInLine) {
            try{ Thread.sleep(11000);} catch(Exception e){}
            seatCustomers();
            serveCustomers();

            // wait for customer to finish eating
            try{Thread.sleep(6000);}catch(Exception e){}

            customerFinishAndLeave();
        }
        print("Employee is Finished for the day and leaves.");
    }

    public void setCustomerCount() {
        // if there are more than or equal to 9 customers in the line, the first 9 will be group together and served
        if((totalCustomersInDineInLine - countCustomersThatLeft) >= 9) {
            try{ Thread.sleep(10000);} catch(Exception e){}
            customerPartionedInLine = 9; // because 3 seats per table
            customersSeated = 0;
        }
        // if there are less than 9 people, then the remaining customers will be grouped together at tables and served
        else {
            try{ Thread.sleep(10000);} catch(Exception e){}
            customerPartionedInLine = (totalCustomersInDineInLine - countCustomersThatLeft);
            customersSeated = 0;

        }
    }

    public void seatCustomers() {
        // grouped and seated the customers, assigned employee to tables
        while (customersSeated < customerPartionedInLine) {
            boolean isEveryCustomerSeated = groupCustomers();
            if(!isEveryCustomerSeated) { break; } // if all the seats are filled exit this loop
            //this sleep allows the other table employee thread to get tables
            try{Thread.sleep(2000);}catch(InterruptedException e){}
            while(!isEveryCustomerSeated && (customersSeated < customerPartionedInLine)) {
                if(isTableBecomeAvailable) {
                    // when a table is available-> exit BW
                    isEveryCustomerSeated = true;
                }
                try{Thread.sleep(2000);}catch(InterruptedException e){}
            }
        }
    }

    public void serveCustomers() {
        // wait for customer to place order.
        while(this.countTables < thisEmployeeTables.size()) {
            // check each table to see if any has 3 orders ready.
            for (Table t : thisEmployeeTables) {
                if(!t.getIsAllOrderTaken() && (t.getAssignedEmployee() == this)) {
                    int count = checkCountOfOrders(t);
                    // if all customers palced their orders then notify customer to eat
                    if(count == t.customersSeatedAtTable()) {
                        print("table: "+ t.getTableNum() + " customers all placed their orders.");
                        countTables++;
                        t.setIsAllOrderTaken(true);
                        bringCustomerFood(t);
                    }
                }
            }
        }
    }

    public void customerFinishAndLeave() {
        while(this.zcountTables < thisEmployeeTables.size()) {
            // check each table to see if any customer wants to pay
            for (Table t : thisEmployeeTables) {
                if((t.getAssignedEmployee() == this )) {
                    int c = 0;
                    while(t.getCustomersLeftAtTable() > 0) {
                        Customer currentCustomer = t.getListOfCustomerAtTable().get(c);
                        if(currentCustomer.getIsFinishedEating()) {
                            t.removeCustomerFromTable();
                            // if customer is finished eating, notify them to pay and remove them from the table. (customer leaves)
                            print("removed customer: " + currentCustomer.getCustomerNumber() + " table( " + t.getTableNum() +" ) removed by empl: "+ employeeNum);
                            c++;
                            customersReadyToPay++;
                            customerCanPayNow(t);
                            countCustomersThatLeft++;
                        }
                    }
                }
                if(customersReadyToPay == t.customersSeatedAtTable()) { 
                    // if a party of 3 at a table are all given their checks and leaves, 
                    // then the waiter must seat 3 other customers in empty table
                    zcountTables++;
                    prepareEmptyTableForNextGuests(t);
                }
            }
        }
    }

    public void prepareEmptyTableForNextGuests(Table t) {
        // empties the tables by setting it to have no customers
        customersSeated--;
        t.resetCustomersSeatedAtTable();
        t.setHasBeenEmptied();
    }

    public int checkCountOfOrders(Table t) {
        int count = 0;
        ArrayList<Customer> customersAtTable = t.getListOfCustomerAtTable();
        for (Customer c : customersAtTable) {
            if(c.getIsPlacedOrder()) count++;
        }
        return count;
    }

    public void customerCanPayNow(Table t) {
        for(Customer c : t.getListOfCustomerAtTable()) {
            // signals customer that the check is ready to pay and they can leave
            c.setIsCheckReady(true);
        }
    }

    public void bringCustomerFood(Table t) {
        print("employee " +employeeNum+" brings table " + t.getTableNum() + " 's food.");
        for (Customer c : t.getListOfCustomerAtTable()) {
            c.setIsFoodServed(true);
        }
    }

    // returns true if all customers have been seated in table || false if tables are full and all customers aren't seated
    public boolean groupCustomers() {
        if(customersInLine.size() >= 3) {
            Table availableTable = sharedListOfTables[0];
            // this assigns an available employee to table[counter]
            for (Table t : sharedListOfTables) {
                if(t.getAssignedEmployee()==null) {
                    availableTable = t;
                    t.setAssignedEmployee(this);
                    break;
                }
                if(t.getHasBeenEmptied() && (t.customersSeatedAtTable() == 0)) {
                    // this means this table previously had customers, so an employee is already assigned to it
                    availableTable = t;
                    t.setAssignedEmployee(this);
                    break;
                }
            }

            thisEmployeeTables.add(availableTable);

            // employee adds 3 customers in queue to its assinged table 
            if(!availableTable.isTableFull() && (availableTable.getAssignedEmployee() == this)) {
                if(customersInLine.size() >= 3){
                    for(int i =0; i < 3; i++) {
                        Customer c = (Customer)customersInLine.remove();
                        availableTable.addCustomerToTable(c);
                        print("Customer: " + c.getCustomerNumber() + " was added to table: " + availableTable.getTableNum()
                        + "  by employee " + employeeNum);
                        c.setisDineInTableAvailable(true);
                        c.getThread().interrupt();
                    }
                }
                customersSeated += 3;
                return true;
            }
        }
        else {
            int counter = -1;
            // this assigns an available employee to table[counter]
            for (Table t : sharedListOfTables) {
                counter++;
                if(t.getAssignedEmployee()==null) {
                    t.setAssignedEmployee(this);
                    print("table: "+t.getTableNum()+ " is assigned to : "+ t.getAssignedEmployee().employeeNum + " instead of employee: " + this.employeeNum);
                    break;
                }

                if(t.getHasBeenEmptied() && (t.customersSeatedAtTable() == 0)) {
                    // this means this table previously had customers, so an employee is already assigned to it
                    break;
                }
            }

            Table availableTable = sharedListOfTables[counter];
            thisEmployeeTables.add(availableTable);
            // employee adds 3 customers in queue to its assidnged table 
            if(!availableTable.isTableFull() && (availableTable.getAssignedEmployee() == this)) {
                int num = customersInLine.size();
                for(int i =0; i < num; i++) {
                    Customer c = (Customer)customersInLine.remove();
                    availableTable.addCustomerToTable(c);
                    // customersSeated++;
                    print("Customer: " + c.getCustomerNumber() + " was added to table: " + availableTable.getTableNum()
                    + "  by employee " + employeeNum);
                    c.setisDineInTableAvailable(true);
                    c.getThread().interrupt();
                }
                customersSeated += num;
                return true;
            }
            // there are extra customers and no available table for them, return false
            return (!areAllTablesFill());
        }
        return true;
    }

    public boolean areAllTablesFill() {
        int counter = 0;
        for (Table t : sharedListOfTables) {
            if(t.isTableFull()) counter++;
        }
        if(counter == 3) return true; 
        return false;
    }

    public void assignTableToEmployee() {
        while(!isTableAvailableToAssign) {
            try{
                if (thread.isInterrupted()) {
                    isTableAvailableToAssign = true;
                }
                else { Thread.sleep(1000);}
            }
            catch(InterruptedException e) {}
        }
    }

    public int getEmployeeNum() { return Integer.parseInt(employeeNum);}

    public int randomNum(int min, int max) {
        return (int) ((Math.random() * (max - min)) + min);
    }

    public boolean getEmployeeStatus(){return isAvailable;}

    public void print(String m) {
        System.out.println("["+(System.currentTimeMillis()-time)+"] : "+ m);
        }
}