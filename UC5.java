import java.util.*;

// -------------------- DOMAIN --------------------

// Abstract Room
abstract class Room {
    private int beds;
    private double size;
    private double price;

    public Room(int beds, double size, double price) {
        this.beds = beds;
        this.size = size;
        this.price = price;
    }

    public abstract String getRoomType();

    public void displayDetails() {
        System.out.println(getRoomType() + " | Beds: " + beds +
                " | Size: " + size + " sq.ft | Price: ₹" + price);
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

// -------------------- INVENTORY (UNCHANGED STATE) --------------------

class RoomInventory {
    private HashMap<String, Integer> availability = new HashMap<>();

    public RoomInventory() {
        availability.put("Single", 5);
        availability.put("Double", 3);
        availability.put("Suite", 2);
    }

    public int getAvailability(String type) {
        return availability.getOrDefault(type, 0);
    }
}

// -------------------- RESERVATION (NEW ACTOR) --------------------

class Reservation {
    private String guestName;
    private String roomType;

    public Reservation(String guestName, String roomType) {
        this.guestName = guestName;
        this.roomType = roomType;
    }

    public String getGuestName() { return guestName; }
    public String getRoomType() { return roomType; }

    public void display() {
        System.out.println("Guest: " + guestName + " | Requested: " + roomType);
    }
}

// -------------------- BOOKING QUEUE (CORE OF UC5) --------------------

class BookingQueue {

    private Queue<Reservation> queue;

    public BookingQueue() {
        queue = new LinkedList<>();
    }

    // Add booking request (FIFO maintained automatically)
    public void addRequest(Reservation reservation) {
        queue.offer(reservation);
        System.out.println("Request added for " + reservation.getGuestName());
    }

    // View all queued requests (read-only)
    public void displayQueue() {
        System.out.println("\n=== Booking Request Queue ===");

        if (queue.isEmpty()) {
            System.out.println("No pending requests.");
            return;
        }

        for (Reservation r : queue) {
            r.display();
        }
    }

    // Peek next request (without removing)
    public Reservation peekNext() {
        return queue.peek();
    }

    // Remove next request (for future UC6 processing)
    public Reservation pollNext() {
        return queue.poll();
    }
}

// -------------------- MAIN APP --------------------

public class HotelAppUC5 {

    public static void main(String[] args) {

        // Inventory (NOT modified here)
        RoomInventory inventory = new RoomInventory();

        // Booking Queue
        BookingQueue bookingQueue = new BookingQueue();

        // Guest submits requests
        bookingQueue.addRequest(new Reservation("Shagun", "Single"));
        bookingQueue.addRequest(new Reservation("Avani", "Suite"));
        bookingQueue.addRequest(new Reservation("Shreya", "Double"));
        bookingQueue.addRequest(new Reservation("Sahithi", "Single"));

        // Display queue (arrival order preserved)
        bookingQueue.displayQueue();

        // Show next request (FIFO)
        System.out.println("\nNext request to process:");
        Reservation next = bookingQueue.peekNext();
        if (next != null) {
            next.display();
        }

        System.out.println("\n(No inventory changes yet — requests only queued)");
    }
}
