public class Customer implements Comparable<Customer> {
    private String firstName;
    private String lastName;
    private int burgersRequired;

    public Customer(String firstName, String lastName, int burgersRequired) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.burgersRequired = burgersRequired;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public int getBurgersRequired() {
        return burgersRequired;
    }

    public String getFullName() {
        return firstName + " " + lastName;
    }

    @Override
    public int compareTo(Customer otherCustomer) {
        return this.getFullName().compareToIgnoreCase(otherCustomer.getFullName());
    }
}

