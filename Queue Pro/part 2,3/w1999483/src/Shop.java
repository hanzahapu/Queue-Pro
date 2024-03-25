import java.io.FileWriter;
import java.io.FileReader;
import java.io.IOException;
import java.io.BufferedReader;
import java.util.Scanner;

public class Shop {
    private FoodQueue[] queues;
    private FoodQueue waitingList;
    private int remainingBurgers;
    private int burgerPrice;
    private int income;

    public Shop() {
        queues = new FoodQueue[3];
        queues[0] = new FoodQueue(2);
        queues[1] = new FoodQueue(3);
        queues[2] = new FoodQueue(5);
        waitingList = new FoodQueue(10);
        remainingBurgers = 50;
        burgerPrice = 650;
        income = 0;
    }

    //Displaying the menu
    private void displayMenu() {
        System.out.println("\n\nOptions Menu:");
        System.out.println("100 or VFQ: View all Queues");
        System.out.println("101 or VEQ: View all Empty Queues");
        System.out.println("102 or ACQ: Add customer to a Queue");
        System.out.println("103 or RCQ: Remove a customer from a Queue (From a specific location)");
        System.out.println("104 or PCQ: Remove a served customer");
        System.out.println("105 or VCS: View Customers Sorted in alphabetical order (Do not use library sort routine)");
        System.out.println("106 or SPD: Store Program Data into file");
        System.out.println("107 or LPD: Load Program Data from file");
        System.out.println("108 or STK: View Remaining Burgers Stock");
        System.out.println("109 or AFS: Add burgers to Stock");
        System.out.println("110 or IFQ: Print income of each queue");
        System.out.println("999 or EXT: Exit the Program");
    }

    //Displaying all queues
    private void viewAllQueues() {
        System.out.println("\n");
        System.out.println("*****************");
        System.out.println("*   Cashiers    *");
        System.out.println("*****************\n");

        for (FoodQueue queue : queues) {
            queue.displayQueue();
        }
        System.out.println("\nX - Not Occupied\tO - Occupied");
    }

    //Displaying empty queues
    private void viewEmptyQueues() {
        boolean emptyQueues = true;

        for (FoodQueue queue : queues) {
            if (!queue.isEmpty()) {
                emptyQueues = false;
                break;
            }
        }

        if (emptyQueues) {
            System.out.println("All queues are empty.");
        } else {
            System.out.println(" Empty Queues.");
            for (FoodQueue queue : queues) {
                if (queue.isEmpty()) {
                    System.out.println("Queue " + queue.getQueueNumber());
                }
            }
        }
    }

    private int nextQueueSlot = 0; // To keep track of the next available slot for each queue

    //Customers Adding
    private void addCustomerToQueue() {
        if (waitingList.isFull()) {
            System.out.println("Waiting List is full. Customer cannot be added.");
            return;
        }

        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter the first name of the customer: ");
        String firstName = scanner.nextLine();
        System.out.print("Enter the last name of the customer: ");
        String lastName = scanner.nextLine();

        int burgersRequired = 0;
        boolean validBurgers = false;
        while (!validBurgers) {
            System.out.print("Enter the number of burgers required: ");
            if (scanner.hasNextInt()) {
                burgersRequired = scanner.nextInt();
                validBurgers = true;
            } else {
                System.out.println("Invalid input. Please enter a valid integer.");
                scanner.nextLine(); // Consume invalid input
            }
        }

        Customer customer = new Customer(firstName, lastName, burgersRequired);

        FoodQueue targetQueue = null;
        for (int i = 0; i < queues.length; i++) {
            int queueIndex = (nextQueueSlot + i) % queues.length;
            FoodQueue currentQueue = queues[queueIndex];
            if (!currentQueue.isFull()) {
                targetQueue = currentQueue;
                nextQueueSlot = (queueIndex + 1) % queues.length; // Update nextQueueSlot for the next customer
                break;
            }
        }

        if (targetQueue != null) {
            targetQueue.addCustomer(customer);
            remainingBurgers -= burgersRequired;
            if (remainingBurgers <= 10) {
                System.out.println("...Warning... Remaining burgers stock is low (" + remainingBurgers + " burgers left).");
            }
            System.out.println("Customer " + customer.getFullName() + " added to Queue " + targetQueue.getQueueNumber());
        } else {
            waitingList.addCustomer(customer);
            System.out.println("All queues are full. Customer added to Waiting List.");
        }
    }




    //Customers removing
    private void removeCustomerFromQueue() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter the queue number: ");
        int queueNumber = 0;
        boolean validQueue = false;
        while (!validQueue) {
            if (scanner.hasNextInt()) {
                queueNumber = scanner.nextInt();
                if (queueNumber >= 1 && queueNumber <= 3) {
                    validQueue = true;
                } else {
                    System.out.println("Invalid queue number. Please enter a valid queue number (1-3).");
                    System.out.print("Enter the queue number: ");
                }
            } else {
                System.out.println("Invalid input. Please enter a valid integer.");
                System.out.print("Enter the queue number: ");
                scanner.nextLine(); // Consume invalid input
            }
        }
        scanner.nextLine(); // Consume newline character

        FoodQueue queue = getQueue(queueNumber);
        if (queue != null) {
            int position = 0;
            boolean validPosition = false;
            while (!validPosition) {
                System.out.print("Enter the position of the customer to remove (1-" + queue.getQueueSize() + "): ");
                if (scanner.hasNextInt()) {
                    position = scanner.nextInt();
                    if (position >= 1 && position <= queue.getQueueSize()) {
                        validPosition = true;
                    } else {
                        System.out.println("Invalid position. Please enter a valid position (1-" + queue.getQueueSize() + ").");
                    }
                } else {
                    System.out.println("Invalid input. Please enter a valid integer.");
                    scanner.nextLine(); //Consume invalid input
                }
            }
            scanner.nextLine(); //Consume newline character

            Customer removedCustomer = queue.removeCustomer(position);
            if (removedCustomer != null) {
                System.out.println("Customer " + removedCustomer.getFullName() + " removed from Queue " + queueNumber);

                if (!waitingList.isEmpty()) {
                    Customer nextCustomer = waitingList.removeCustomer(1);
                    queue.addCustomer(nextCustomer);
                    System.out.println("Next customer in Waiting List added to Queue " + queueNumber);
                }
            } else {
                System.out.println("Invalid position. Customer removal failed.");
            }
        } else {
            System.out.println("Invalid queue number. Customer removal failed.");
        }
    }


    //Served customers removing
    private void removeServedCustomer() {
        for (FoodQueue queue : queues) {
            if (!queue.isEmpty()) {
                Customer servedCustomer = queue.removeCustomer(1);
                int burgersServed = servedCustomer.getBurgersRequired();
                int customerIncome = burgersServed * burgerPrice;
                income += customerIncome;
                System.out.println("Customer " + servedCustomer.getFullName() + " served from Queue " + queue.getQueueNumber() + " (Income: $" + customerIncome + ")");
                return; // Serve only the first customer found and exit the method
            }
        }
        System.out.println("No customers to serve in any queue.");
    }

    //Sorting customers
    private void viewCustomersSorted() {
        Customer[] allCustomers = new Customer[getTotalCustomers()];
        int index = 0;

        for (FoodQueue queue : queues) {
            Customer[] queueCustomers = queue.getAllCustomers();
            System.arraycopy(queueCustomers, 0, allCustomers, index, queue.getQueueSize());
            index += queue.getQueueSize();
        }

        if (index > 0) {
            //Sort the customers alphabetically
            sortCustomers(allCustomers);

            System.out.println("Customers Sorted in Alphabetical Order:");
            for (Customer customer : allCustomers) {
                System.out.println(customer.getFullName());
            }
        } else {
            System.out.println("No customers found.");
        }
    }

    //Store data
    private void storeProgramData() {
        try {
            FileWriter writer = new FileWriter("program_data.txt");
            for (FoodQueue queue : queues) {
                Customer[] customers = queue.getAllCustomers();
                writer.write("Queue " + queue.getQueueNumber() + ":\n");
                for (int i = 0; i < queue.getQueueSize(); i++) {
                    Customer customer = customers[i];
                    writer.write(customer.getFullName() + " - Burgers: " + customer.getBurgersRequired() + "\n");
                }
                writer.write("\n");
            }
            writer.write("Remaining Burgers: " + remainingBurgers + "\n");
            writer.close();
            System.out.println("Program data successfully stored into file.");
        } catch (IOException e) {
            System.out.println("An error occurred while storing program data.");
            e.printStackTrace();
        }
    }

    //Loading data
    private void loadProgramData() {
        try {
            BufferedReader reader = new BufferedReader(new FileReader("program_data.txt"));
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.isEmpty()) {
                    System.out.println(line);
                }
            }
            reader.close();
        } catch (IOException e) {
            System.out.println("An error occurred while loading program data.");
            e.printStackTrace();
        }
    }

    //Remaining stocks
    private void viewRemainingStock() {
        System.out.println("Remaining Burgers Stock: " + remainingBurgers);
    }

    //Adding burgers
    private void addBurgersToStock() {
        Scanner scanner = new Scanner(System.in);
        int burgersToAdd = 0;
        boolean validBurgers = false;
        while (!validBurgers) {
            System.out.print("Enter the number of burgers to add: ");
            if (scanner.hasNextInt()) {
                burgersToAdd = scanner.nextInt();
                validBurgers = true;
            } else {
                System.out.println("Invalid input. Please enter a valid integer.");
                scanner.nextLine(); //Consume invalid input
            }
        }
        scanner.nextLine(); //Consume newline character

        remainingBurgers += burgersToAdd;
        System.out.println(burgersToAdd + " burgers added to stock.");
    }


    //Printing income
    private void printIncomeOfEachQueue() {
        System.out.println("Income of Each Queue:");
        for (FoodQueue queue : queues) {
            int totalIncome = 0;
            Customer[] customers = queue.getAllCustomers();
            for (Customer customer : customers) {
                int burgersRequired = customer.getBurgersRequired();
                totalIncome += burgersRequired * burgerPrice;
            }
            System.out.println("Queue " + queue.getQueueNumber() + ": Rs." + totalIncome+".00");
        }
        System.out.println("Served customer income: Rs." + income+".00");
    }


    //Exiting the programme
    private void exitProgram() {
        System.out.println("Exiting the program...");
        System.exit(0);
    }



    private FoodQueue getQueue(int queueNumber) {
        for (FoodQueue queue : queues) {
            if (queue.getQueueNumber() == queueNumber) {
                return queue;
            }
        }
        return null;
    }

    private int getTotalCustomers() {
        int totalCustomers = 0;
        for (FoodQueue queue : queues) {
            totalCustomers += queue.getQueueSize();
        }
        return totalCustomers;
    }

    private void sortCustomers(Customer[] customers) {
        for (int i = 0; i < customers.length - 1; i++) {
            for (int j = i + 1; j < customers.length; j++) {
                if (customers[i].compareTo(customers[j]) > 0) {
                    Customer temp = customers[i];
                    customers[i] = customers[j];
                    customers[j] = temp;
                }
            }
        }
    }

    //Switch cases
    public void start() {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            displayMenu();
            System.out.print("\nEnter your choice: ");
            String choice = scanner.nextLine().toUpperCase();

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
                case "PCQ":
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
                case "110":
                case "IFQ":
                    printIncomeOfEachQueue();
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

    //Main method
    public static void main(String[] args) {
        Shop shop = new Shop();
        shop.start();
    }
}