import java.util.LinkedList;
import java.util.Queue;

public class PickUpEmployee implements Runnable{
    private Thread thread;
    public static Queue customersInLine = new LinkedList<Customer>();
    private static Customer currentCustomer; 
    private volatile boolean isEmployeeOccupied = true; 

    public PickUpEmployee() {
        this.thread = new Thread(this);
    }

    public void start() {
        thread.start();
    }

    public void run() {
        // addCustomerToQueue() gets called in Main
       try{ Thread.sleep(5000);} catch(Exception e){}
       /////// // System.out.println(customersInLine);
        while (!customersInLine.isEmpty()) { //if there are customers in queue
            if (isEmployeeOccupied) {
                isEmployeeOccupied = false;
                // ^ line 26 locks the while loop from removing the next customer in queue until current customer is finished being served.
                currentCustomer = (Customer)customersInLine.remove(); // take the first customers order
                takeCustomerOrder(currentCustomer); 

                // send interrupt to customer
                currentCustomer.setIsPickUpEmployeeAvailable(true); 
                currentCustomer.getThread().interrupt();
                isEmployeeOccupied = true;
            }
        }
    }

    public void takeCustomerOrder(Customer currentCustomer) {
        try{ 
            Thread.sleep(randomNum(1000, 5000));
            print("pick up Employee took customer " +currentCustomer.getCustomerNumber() +" order and is making it."); 
        }
        catch(InterruptedException e) {}
    }

    public int randomNum(int min, int max) {
        return (int) ((Math.random() * (max - min)) + min);
    }

    public void print(String str) {
        System.out.println(str);
    }
}