import java.util.*;

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

    public int getNumberOfBeds() { return numberOfBeds; }
    public double getSize() { return size; }
    public double getPrice() { return price; }

    public abstract String getRoomType();

    public void displayDetails() {
        System.out.println("Room Type: " + getRoomType());
        System.out.println("Beds: " + numberOfBeds);
        System.out.println("Size: " + size + " sq.ft");
        System.out.println("Price: ₹" + price);
    }
}

// Concrete Rooms
class SingleRoom extends Room {
    public SingleRoom() { super(1, 200, 1500); }
    public String getRoomType() { return "Single"; }
}

class DoubleRoom extends Room {
    public DoubleRoom() { super(2, 350, 2500); }
    public String getRoomType() { return "Double"; }
}

class SuiteRoom extends Room {
    public SuiteRoom() { super(3, 600, 5000); }
    public String getRoomType() { return "Suite"; }
}

// Central Inventory (Same as UC3)
class RoomInventory {
    private HashMap<String, Integer> availability;

    public RoomInventory() {
        availability = new HashMap<>();
        availability.put("Single", 5);
        availability.put("Double", 3);
        availability.put("Suite", 0); // purposely 0 to test filtering
    }

    // READ-ONLY access
    public int getAvailability(String roomType) {
        return availability.getOrDefault(roomType, 0);
    }

    // Optional: expose full map safely
    public Map<String, Integer> getAllAvailability() {
        return Collections.unmodifiableMap(availability);
    }
}

// 🔥 Search Service (CORE OF UC4)
class RoomSearchService {

    private RoomInventory inventory;
    private List<Room> rooms;

    public RoomSearchService(RoomInventory inventory, List<Room> rooms) {
        this.inventory = inventory;
        this.rooms = rooms;
    }

    // Read-only search
    public void searchAvailableRooms() {
        System.out.println("\n=== Available Rooms ===");

        boolean found = false;

        for (Room room : rooms) {

            int available = inventory.getAvailability(room.getRoomType());

            // Validation → only show available rooms
            if (available > 0) {
                room.displayDetails();
                System.out.println("Available: " + available);
                System.out.println("--------------------------");
                found = true;
            }
        }

        // Defensive programming
        if (!found) {
            System.out.println("No rooms available at the moment.");
        }
    }
}

// Main Application
public class HotelAppUC4 {

    public static void main(String[] args) {

        // Initialize inventory (state)
        RoomInventory inventory = new RoomInventory();

        // Initialize room domain objects
        List<Room> rooms = new ArrayList<>();
        rooms.add(new SingleRoom());
        rooms.add(new DoubleRoom());
        rooms.add(new SuiteRoom());

        // Initialize search service
        RoomSearchService searchService = new RoomSearchService(inventory, rooms);

        // Guest triggers search
        searchService.searchAvailableRooms();

        System.out.println("\n(Search completed — no state changed)");
    }
}
