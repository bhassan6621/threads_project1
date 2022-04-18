public class Customer implements Runnable{
    private Thread thread;
    private String customerNumber; 
    private boolean isDineIn = false;
    private boolean isPickUp = false;
    private boolean isPickUpEmployeeAvailable = false;
    private boolean isDineInTableAvailable = false;
    private static boolean isPlacedOrder = false;
    private static boolean isFoodServed = false;
    private static boolean isFinishedEating = false;
    private static boolean isCheckReady = false;
    public static long time = System.currentTimeMillis();

    public Customer(String num) {
        this.thread = new Thread(this, num);
        customerNumber = num;
    }
    
    public void start() {
        thread.start();
    }

    // template function of Customer Thread:
    public void run() {
        commuteToRestaurant();
        pickDiningOption();
        if(isPickUp) {
            waitForPickUp(); // does BW -> pays (by sleeping) & terminates
        }

        if(isDineIn) {
            waitToBeSeated();
            placeOrder();
            eat();
            pay();
        }
    }

    public void eat() {
        while(!isFoodServed) {
            //BW until employee serves the food
        }
        if(isFoodServed) {
            print("customer " + customerNumber +" is eating food, yumm");
            isFinishedEating = true;
            try{
                Thread.sleep(randomNum(1000, 2000));
                Thread.yield();
                Thread.yield();
            }
            catch(InterruptedException e) {

            }
        }
    }

    public void pay() {
        while(!isCheckReady) {
            try{Thread.sleep(1000);}
            catch(InterruptedException e) {}
        } // BW until employee brings check
        if(isCheckReady) {
            print("customer " + customerNumber + " pays the check and leaves.");
        }
    }

    public void placeOrder() {
        isPlacedOrder = true;
        thread.setPriority(8);
        try{Thread.sleep(randomNum(5000, 7000));}
        catch(InterruptedException e) {}
        thread.setPriority(5);
    }

    public void waitToBeSeated() {
        while(!isDineInTableAvailable) {
            // Busy Wait until table becomes available
            try {
                if(thread.isInterrupted()) {
                    isDineInTableAvailable = true;
                }
                else {
                    Thread.sleep(5000);
                }
            }
            catch(InterruptedException e) {}
        }
    }

    public void commuteToRestaurant() {
        try {
            // customer commutes to restaurant
            Thread.sleep(randomNum(1000, 3000));
            print("Customer "+ customerNumber + "walks into restaurant");
        }
        catch(InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void pickDiningOption() {
        int num = randomNum(1, 10);
        if (num < 2) {
            isPickUp = true;
            PickUpEmployee.customersInLine.add(this);
        }
        else {
            isDineIn = true;
            TableEmployee.customersInLine.add(this);
        }
    }

    public void waitForPickUp() {
        while (!isPickUpEmployeeAvailable) {
            // Busy Wait
            try{
                if (thread.isInterrupted()) {
                    isPickUpEmployeeAvailable = true;
                }
                else {
                    // customer sleeps for a "long" time
                    Thread.sleep(5000);
                    print("customer "+ customerNumber+ " waiting on pick up order");
                }
            }
            catch(InterruptedException e) {}
        }
        // when customer exits BW
        try{
            Thread.sleep(randomNum(1000, 3000));
            print("Customer "+ customerNumber + " pays for pick up order");
        }
        catch(InterruptedException e) { }
    }

    public int randomNum(int min, int max) {
        return (int) ((Math.random() * (max - min)) + min);
    }

    public boolean getIsDineIn() {return isDineIn;}
   
    public boolean getIsPickUp() {return isPickUp;}

    public String getCustomerNumber() {return customerNumber;}

    public Thread getThread() {return thread;}

    public boolean getIsFinishedEating() {return isFinishedEating;}

    public void setIsPickUpEmployeeAvailable(boolean bool) { isPickUpEmployeeAvailable = bool; }
    
    public void setisDineInTableAvailable(boolean bool) { isDineInTableAvailable = bool; }

    public void setIsFoodServed(boolean b) {isFoodServed = b;}

    public void setIsCheckReady(boolean b) {isCheckReady = b;}

    public boolean getIsPlacedOrder() {return isPlacedOrder;}

    public void print(String str) {
        System.out.println("["+(System.currentTimeMillis()-time)+"] : "+ str);    }

}