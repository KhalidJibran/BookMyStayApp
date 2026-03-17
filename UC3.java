import java.util.HashMap;
import java.util.Map;

// Abstract Room Class
abstract class Room {
    private int numberOfBeds;
    private double size;
    private double price;

    public Room(int numberOfBeds, double size, double price) {
        this.numberOfBeds = numberOfBeds;
        this.size = size;
        this.price = price;
    }

    public int getNumberOfBeds() {
        return numberOfBeds;
    }

    public double getSize() {
        return size;
    }

    public double getPrice() {
        return price;
    }

    public abstract String getRoomType();

    public void displayDetails() {
        System.out.println("Room Type: " + getRoomType());
        System.out.println("Beds: " + numberOfBeds);
        System.out.println("Size: " + size + " sq.ft");
        System.out.println("Price: ₹" + price);
    }
}

// Concrete Classes
class SingleRoom extends Room {
    public SingleRoom() {
        super(1, 200, 1500);
    }

    public String getRoomType() {
        return "Single";
    }
}

class DoubleRoom extends Room {
    public DoubleRoom() {
        super(2, 350, 2500);
    }

    public String getRoomType() {
        return "Double";
    }
}

class SuiteRoom extends Room {
    public SuiteRoom() {
        super(3, 600, 5000);
    }

    public String getRoomType() {
        return "Suite";
    }
}

// Inventory Class (CORE OF UC3)
class RoomInventory {

    private HashMap<String, Integer> availability;

    // Constructor → initialize inventory
    public RoomInventory() {
        availability = new HashMap<>();

        // Initial room counts
        availability.put("Single", 5);
        availability.put("Double", 3);
        availability.put("Suite", 2);
    }

    // Get availability of specific room
    public int getAvailability(String roomType) {
        return availability.getOrDefault(roomType, 0);
    }

    // Update availability (controlled update)
    public void updateAvailability(String roomType, int count) {
        availability.put(roomType, count);
    }

    // Reduce availability (like booking)
    public void bookRoom(String roomType) {
        int current = getAvailability(roomType);
        if (current > 0) {
            availability.put(roomType, current - 1);
            System.out.println(roomType + " room booked successfully.");
        } else {
            System.out.println("No " + roomType + " rooms available.");
        }
    }

    // Display full inventory
    public void displayInventory() {
        System.out.println("\n=== Current Room Inventory ===");
        for (Map.Entry<String, Integer> entry : availability.entrySet()) {
            System.out.println(entry.getKey() + " Rooms Available: " + entry.getValue());
        }
    }
}

// Main Application
public class HotelAppUC3 {

    public static void main(String[] args) {

        // Initialize inventory
        RoomInventory inventory = new RoomInventory();

        // Create room objects (domain stays separate)
        Room single = new SingleRoom();
        Room doubleRoom = new DoubleRoom();
        Room suite = new SuiteRoom();

        // Display room details
        System.out.println("=== Room Details ===\n");

        single.displayDetails();
        System.out.println();

        doubleRoom.displayDetails();
        System.out.println();

        suite.displayDetails();

        // Display centralized inventory
        inventory.displayInventory();

        // Simulate booking
        System.out.println("\n--- Booking Simulation ---");
        inventory.bookRoom("Single");
        inventory.bookRoom("Suite");

        // Updated inventory
        inventory.displayInventory();

        System.out.println("\nApplication Terminated.");
    }
}
