// Abstract Class
abstract class Room {
    private int numberOfBeds;
    private double size;
    private double price;

    // Constructor
    public Room(int numberOfBeds, double size, double price) {
        this.numberOfBeds = numberOfBeds;
        this.size = size;
        this.price = price;
    }

    // Getters (Encapsulation)
    public int getNumberOfBeds() {
        return numberOfBeds;
    }

    public double getSize() {
        return size;
    }

    public double getPrice() {
        return price;
    }

    // Abstract method (to be implemented by subclasses)
    public abstract String getRoomType();

    // Common method
    public void displayDetails() {
        System.out.println("Room Type: " + getRoomType());
        System.out.println("Beds: " + numberOfBeds);
        System.out.println("Size: " + size + " sq.ft");
        System.out.println("Price: ₹" + price);
    }
}

// Single Room Class
class SingleRoom extends Room {

    public SingleRoom() {
        super(1, 200, 1500);
    }

    @Override
    public String getRoomType() {
        return "Single Room";
    }
}

// Double Room Class
class DoubleRoom extends Room {

    public DoubleRoom() {
        super(2, 350, 2500);
    }

    @Override
    public String getRoomType() {
        return "Double Room";
    }
}

// Suite Room Class
class SuiteRoom extends Room {

    public SuiteRoom() {
        super(3, 600, 5000);
    }

    @Override
    public String getRoomType() {
        return "Suite Room";
    }
}

// Main Application
public class HotelApp {

    public static void main(String[] args) {

        // Static Availability Variables
        int singleRoomAvailable = 5;
        int doubleRoomAvailable = 3;
        int suiteRoomAvailable = 2;

        // Creating Room Objects (Polymorphism)
        Room single = new SingleRoom();
        Room doubleRoom = new DoubleRoom();
        Room suite = new SuiteRoom();

        // Display Details
        System.out.println("=== Room Details & Availability ===\n");

        single.displayDetails();
        System.out.println("Available: " + singleRoomAvailable);
        System.out.println("-----------------------------------");

        doubleRoom.displayDetails();
        System.out.println("Available: " + doubleRoomAvailable);
        System.out.println("-----------------------------------");

        suite.displayDetails();
        System.out.println("Available: " + suiteRoomAvailable);
        System.out.println("-----------------------------------");

        System.out.println("\nApplication Terminated.");
    }
}
