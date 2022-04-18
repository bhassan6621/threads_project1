import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

public class Table {
    private int tableNum;
    private TableEmployee assignedEmployee;
    private int numSeats;
    private ArrayList<Customer> listOfCustomersAtTable = new ArrayList<>(); 
    public static Queue employeesInLine = new LinkedList<TableEmployee>();
    private boolean isTableFull = false;
    private volatile boolean isAssigningTable = true; 
    private boolean isAllOrdersTaken = false;
    private int customersLeftAtTable;
    private boolean isHasBeenEmptied = false;

    public Table(TableEmployee assignedEmployee, int numSeats, int tableNum) {
        this.assignedEmployee = assignedEmployee;
        this.numSeats = numSeats;
        this.tableNum = tableNum;
    }

    public boolean addCustomerToTable(Customer c){
        if(!isTableFull && (listOfCustomersAtTable.size() <= 3)) {
            listOfCustomersAtTable.add(c);
            customersLeftAtTable++;
            return true;
        }
        else {
            isTableFull = false;
            System.out.println("TABLE " + tableNum + "is full, cannot add more people");
            return false;
        }
    }

    public void removeCustomerFromTable() {
        customersLeftAtTable--;
    }

    public int getCustomersLeftAtTable() {
        return customersLeftAtTable;
    }

    public int customersSeatedAtTable(){
        return listOfCustomersAtTable.size();
    }

    public boolean getHasBeenEmptied() {
        return isHasBeenEmptied;
    }

    public void setHasBeenEmptied() {
        isHasBeenEmptied = true;
    }


    public boolean isTableFull() {
        if(listOfCustomersAtTable.size() >= numSeats) return true;
        return false;
    }

    public void setAssignedEmployee(TableEmployee employee) {
        while(isAssigningTable){
            isAssigningTable = false;
            if(assignedEmployee == null) {
                assignedEmployee = employee;
            }
            isAssigningTable = false;
            break;
        }
    }

    public void resetCustomersSeatedAtTable() {
        listOfCustomersAtTable.clear();
        // System.out.println("size left at table: " + listOfCustomersAtTable.size());
    }

    public boolean getIsAllOrderTaken() {return isAllOrdersTaken; }

    public void setIsAllOrderTaken(boolean b) {isAllOrdersTaken = b;}

    public TableEmployee getAssignedEmployee() {return assignedEmployee;}

    public ArrayList<Customer> getListOfCustomerAtTable(){ return listOfCustomersAtTable;}

    public int getTableNum() { return tableNum; }
}
