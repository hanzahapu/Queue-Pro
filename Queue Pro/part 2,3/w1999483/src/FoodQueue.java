public class FoodQueue {
    private int queueNumber ;
    private static int queueNumberCounter = 1;

    private Customer[] customers;
    private int maxSize;
    private int size;

    public FoodQueue(int maxSize) {
        this.queueNumber = generateQueueNumber();
        this.maxSize = maxSize;
        this.size = 0;
        this.customers = new Customer[maxSize];
    }

    public int getQueueNumber() {
        return queueNumber;
    }

    public int getQueueSize() {
        return size;
    }

    public int getMaxSize() {
        return maxSize;
    }

    public boolean isFull() {
        return size == maxSize;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    //Displaying queue
    public void displayQueue() {
        System.out.print("Queue " + queueNumber + ": ");
        for (int i = 0; i < size; i++) {
            System.out.print("O ");
        }
        for (int i = size; i < maxSize; i++) {
            System.out.print("X ");
        }
        System.out.println();
    }

    // Adding customers
    public void addCustomer(Customer customer) {
        if (size < maxSize) {
            customers[size] = customer;
            size++;
        }
    }

    //Removing customers
    public Customer removeCustomer(int position) {
        if (position >= 1 && position <= size) {
            Customer removedCustomer = customers[position - 1];
            for (int i = position - 1; i < size - 1; i++) {
                customers[i] = customers[i + 1];
            }
            size--;
            customers[size] = null;
            return removedCustomer;
        }
        return null;
    }

    //Getting customers
    public Customer[] getAllCustomers() {
        Customer[] allCustomers = new Customer[size];
        System.arraycopy(customers, 0, allCustomers, 0, size);
        return allCustomers;
    }
    //generateQueueNumber


    //getting queue numbers
    private int generateQueueNumber() {
        int generatedQueueNumber = queueNumberCounter;
        queueNumberCounter = (queueNumberCounter % 3) + 1;
        return generatedQueueNumber;
    }
}