import java.util.InputMismatchException;
import java.util.Scanner;
import java.io.FileWriter;
import java.io.IOException;
import java.io.File;

//data
class foodCount {
    private static final int maxqueue1 = 2;
    private static final int maxqueue2 = 3;
    private static final int maxqueue3 = 5;

    private String[] queue1;
    private String[] queue2;
    private String[] queue3;

    // Number of customers in each queue
    private int queue1Size;
    private int queue2Size;
    private int queue3Size;

    // remaining burgers in stock
    private int remainingBurgers;

    public foodCount() {
        queue1 = new String[maxqueue1];
        queue2 = new String[maxqueue2];
        queue3 = new String[maxqueue3];
        queue1Size = 0;
        queue2Size = 0;
        queue3Size = 0;
        remainingBurgers = 50;
    }

    // Display the queues of cashiers
    private void displayQueues() {
        int max=queue3.length;
        System.out.println("\n*****************");
        System.out.println("*    cashiers   *");
        System.out.println("*****************");


        int maxQueueSize = Math.max(queue1Size, Math.max(queue2Size, queue3Size));


        for (int i = 0 ; i<max; i++){
            if (i<2){
                if (queue1[i] == null){
                    System.out.print("X");
                }
                else{
                    System.out.print("O");
                }
            }
            if (i<3){
                if (queue2[i] == null){
                    System.out.print("\t\tX");
                }
                else{
                    System.out.print("\t\tO");
                }
            }
            if (i<5){
                if (i==3 || i==4){
                    System.out.print("\t\t");
                }
                if (queue3[i] == null){
                    System.out.print("\t\tX");
                }
                else{
                    System.out.print("\t\tO");
                }
            }
            System.out.println();


        }
        System.out.println("\nX – Not Occupied O – Occupied");
    }


    // View all queues
    private void viewAllQueues() {
        displayQueues();
    }

    // View empty queues
    private void viewEmptyQueues() {
        if (queue1Size == 0) System.out.println("Queue 1 is empty.");
        if (queue2Size == 0) System.out.println("Queue 2 is empty.");
        if (queue3Size == 0) System.out.println("Queue 3 is empty.");
        else{
            System.out.println(":No Empty Queues:");
        }
    }

    // Add customer
    private void addCustomerToQueue() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter the name of the customer: ");
        String name = scanner.nextLine();

        while(true) {
            try {
                System.out.print("Please enter the queue number : ");
                int queueNum = scanner.nextInt();

                String[] selectedQueue;
                int maxQueueSize = getMaxQueueSize(queueNum);

                if (queueNum == 1) {
                    selectedQueue = queue1;
                } else if (queueNum == 2) {
                    selectedQueue = queue2;
                } else if (queueNum == 3) {
                    selectedQueue = queue3;
                } else {
                    System.out.println("Invalid queue number.");
                    continue;
                }

                if (getQueueSize(queueNum) == maxQueueSize) {
                    System.out.println("\nQueue " + queueNum + " is already full.");
                } else {
                    selectedQueue[getQueueSize(queueNum)] = name;
                    incrementQueueSize(queueNum);
                    remainingBurgers -= 5;
                    if (remainingBurgers <= 10) {
                        System.out.println("\n...Warning... Remaining burgers stock is low (" + remainingBurgers + " burgers left).");
                    }
                    System.out.println("\nCustomer " + name + " added to Queue " + queueNum);
                    break;
                }
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Enter QUEUE NUMBER.");
                scanner.nextLine();
            }
        }

    }




    // Remove customer
    private void removeCustomerFromQueue() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Please enter the queue number : ");
        int queueN = scanner.nextInt();

        String[] selectedQueue;
        int queueSize = getQueueSize(queueN);

        if (queueN == 1) {
            selectedQueue = queue1;
        } else if (queueN == 2) {
            selectedQueue = queue2;
        } else if (queueN == 3) {
            selectedQueue = queue3;
        } else {
            System.out.println("Invalid queue number Only 3 queues available");
            return;
        }

        if (queueSize == 0) {
            System.out.println("Queue " + queueN + " is empty.");
        } else {
            System.out.print("Enter the position of the customer to remove (1-" + queueSize + "): ");
            int position = scanner.nextInt();

            if (position < 1 || position > queueSize) {
                System.out.println("Invalid position.");
            } else {
                String removedCustomer = selectedQueue[position - 1];
                for (int i = position - 1; i < queueSize - 1; i++) {
                    selectedQueue[i] = selectedQueue[i + 1];
                }
                selectedQueue[queueSize - 1] = null;
                decrementQueueSize(queueN);
                System.out.println("Customer " + removedCustomer + " removed from Queue " + queueN);
            }
        }
    }

    // Remove served customer
    private void removeServedCustomer() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter the name of the served customer: ");
        String served = scanner.nextLine();

        boolean customerRemoved = removeCustomerFromQueue(queue1, served);
        customerRemoved |= removeCustomerFromQueue(queue2, served);
        customerRemoved |= removeCustomerFromQueue(queue3, served);

        if (customerRemoved) {
            System.out.println("\n");
            System.out.println("Customer " + served + " removed from the queues.");
        } else {
            System.out.println("\n");
            System.out.println("Customer " + served + " not founded.");
        }
    }

    // remove a customer from a queue
    private boolean removeCustomerFromQueue(String[] queue, String customerName) {
        for (int i = 0; i < queue.length; i++) {
            if (queue[i] != null && queue[i].equals(customerName)) {
                for (int j = i; j < queue.length - 1; j++) {
                    queue[j] = queue[j + 1];
                }
                queue[queue.length - 1] = null;
                decrementQueueSize(queue);
                return true;
            }
        }
        return false;
    }

    // sort alphabetical order
    private void viewCustomersSorted() {
        String[] sortedCustomers = getSortedCustomers();
        if (sortedCustomers.length == 0) {
            System.out.println("There are no queues for customers..");
        } else {
            System.out.println("Customers are arranged alphabetically:");
            for (String customer : sortedCustomers) {
                System.out.println(customer);
            }
        }
    }

    // get all customers from the queues and sort them
    private String[] getSortedCustomers() {
        int totalCustomers = queue1Size + queue2Size + queue3Size;
        String[] allCustomers = new String[totalCustomers];
        int index = 0;

        for (String customer : queue1) {
            if (customer != null) {
                allCustomers[index++] = customer;
            }
        }

        for (String customer : queue2) {
            if (customer != null) {
                allCustomers[index++] = customer;
            }
        }

        for (String customer : queue3) {
            if (customer != null) {
                allCustomers[index++] = customer;
            }
        }

        // Sort the customers using bubble sort
        for (int i = 0; i < totalCustomers - 1; i++) {
            for (int j = 0; j < totalCustomers - i - 1; j++) {
                if (allCustomers[j].compareTo(allCustomers[j + 1]) > 0) {
                    String temp = allCustomers[j];
                    allCustomers[j] = allCustomers[j + 1];
                    allCustomers[j + 1] = temp;
                }
            }
        }

        return allCustomers;
    }

    // Store Data
    private void storeProgramData() {
        // store program data into a file
        try{
            FileWriter writer =new FileWriter("details.txt");
            writer.write("Queue 1:\n");for (int i = 0; i < queue1Size; i++) {
                writer.write(queue1[i] + "\n");
            }

            writer.write("Queue 2:\n");
            for (int i = 0; i < queue2Size; i++) {
                writer.write(queue2[i] + "\n");
            }

            writer.write("Queue 3:\n");
            for (int i = 0; i < queue3Size; i++) {
                writer.write(queue3[i] + "\n");
            }

            writer.write("Remaining Burgers : " + remainingBurgers + "\n");

            writer.close();

            System.out.println("\nProgram data succesfully stored into file.");
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();

        }

    }

    // Load text file data
    private void loadProgramData() {
        try{
            File file=new File("program_data.txt");
            Scanner rf=new Scanner(file);
            String fileLine;
            while(rf.hasNext()){
                fileLine=rf.nextLine();
                System.out.println(fileLine);


            }
            rf.close();
        }
        catch(IOException e){
            System.out.println();
        }
        // Implement the code to load program data from a file
        System.out.println("Program data loaded from a file.");
    }

    // remaining burgers stock
    private void viewRemainingStock() {
        System.out.println("Remaining Burgers Stock: " + remainingBurgers);
    }

    // Add burgers stock
    private void addBurgersToStock() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter the number of burgers to add: ");
        int Add = scanner.nextInt();

        remainingBurgers += Add;
        System.out.println("\n");
        System.out.println( Add + " burgers added to stock.");
    }

    // exit program
    private void exitProgram() {
        System.out.println(": Exiting the program :");
        System.exit(0);
    }

    // get the maximum queue size
    private int getMaxQueueSize(int queueNumber) {
        if (queueNumber == 1) {
            return maxqueue1;
        } else if (queueNumber == 2) {
            return maxqueue2;
        } else if (queueNumber == 3) {
            return maxqueue3;
        } else {
            return 0;
        }
    }

    // get the current size
    private int getQueueSize(int queueNumber) {
        if (queueNumber == 1) {
            return queue1Size;
        } else if (queueNumber == 2) {
            return queue2Size;
        } else if (queueNumber == 3) {
            return queue3Size;
        } else {
            return 0;
        }
    }

    // increment the size of a queue
    private void incrementQueueSize(int queueNumber) {
        if (queueNumber == 1) {
            queue1Size++;
        } else if (queueNumber == 2) {
            queue2Size++;
        } else if (queueNumber == 3) {
            queue3Size++;
        }
    }

    // decrement the size of a queue
    private void decrementQueueSize(int queueNumber) {
        if (queueNumber == 1) {
            queue1Size--;
        } else if (queueNumber == 2) {
            queue2Size--;
        } else if (queueNumber == 3) {
            queue3Size--;
        }
    }

    // decrement the size of a queue
    private void decrementQueueSize(String[] queue) {
        if (queue == queue1) {
            queue1Size--;
        } else if (queue == queue2) {
            queue2Size--;
        } else if (queue == queue3) {
            queue3Size--;
        }
    }

    public void run() {
        Scanner scanner = new Scanner(System.in);
        String choice;

        while (true){
            System.out.println("\nOptions Bar:");
            System.out.println("100 or VFQ: View all Queues");
            System.out.println("101 or VEQ: View all Empty Queues");
            System.out.println("102 or ACQ: Add customer to a Queue");
            System.out.println("103 or RCQ: Remove a customer from a Queue. (From a specific location)");
            System.out.println("104 or PCQ: Remove a served customer");
            System.out.println("105 or VCS: View Customers Sorted in alphabetical order (Do not use library sort routine)");
            System.out.println("106 or SPD: Store Program Data into file");
            System.out.println("107 or LPD: Load Program Data from file");
            System.out.println("108 or STK: View Remaining burgers Stock");
            System.out.println("109 or AFS: Add burgers to Stock");
            System.out.println("999 or EXT: Exit the Program");

            System.out.print("\nEnter your choice: ");
            choice = scanner.nextLine();

            switch (choice) {
                case "100":
                case "VFQ":
                    viewAllQueues();
                    break;
                case "101":
                case "VEQ":
                    viewEmptyQueues();
                    break;
                case "102":
                case "ACQ":
                    addCustomerToQueue();
                    break;
                case "103":
                case "RCQ":
                    removeCustomerFromQueue();
                    break;
                case "104":
                case "PCQ" :
                    removeServedCustomer();
                    break;
                case "105":
                case "VCS":
                    viewCustomersSorted();
                    break;
                case "106":
                case "SPD":
                    storeProgramData();
                    break;
                case "107":
                case "LPD":
                    loadProgramData();
                    break;
                case "108":
                case "STK":
                    viewRemainingStock();
                    break;
                case "109":
                case "AFS":
                    addBurgersToStock();
                    break;
                case "999":
                case "EXT":
                    exitProgram();
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        } 
    }
}

public class shop {
    public static void main(String[] args) {
        foodCount foodCenter = new foodCount();
        foodCenter.run();
    }
}