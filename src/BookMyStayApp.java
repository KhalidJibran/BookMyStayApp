import java.util.*;

public class BookMyStayApp {
    public static void main(String[] args) {

        // ===================== UC1 =====================
        System.out.println("Welcome to the Hotel Booking Management System");
        System.out.println("System initialized successfully.\n");

        // ===================== UC2 =====================
        Room single = new SingleRoom();
        Room dbl = new DoubleRoom();
        Room suite = new SuiteRoom();

        // ===================== UC3 =====================
        RoomInventory inventory = new RoomInventory();

        // ===================== UC4 =====================
        RoomSearchService search = new RoomSearchService();
        System.out.println("Room Search\n");
        search.searchAvailableRooms(inventory, single, dbl, suite);

        // ===================== UC5 =====================
        BookingRequestQueue queue = new BookingRequestQueue();
        queue.addRequest(new Reservation("Abhi", "Single"));
        queue.addRequest(new Reservation("Subha", "Double"));
        queue.addRequest(new Reservation("Vanmathi", "Suite"));

        // ===================== UC6 =====================
        RoomAllocationService allocator = new RoomAllocationService();
        BookingHistory history = new BookingHistory();

        System.out.println("\nRoom Allocation Processing");

        List<String> reservationIds = new ArrayList<>();

        while (queue.hasPendingRequests()) {
            Reservation r = queue.getNextRequest();
            String id = allocator.allocateRoom(r, inventory);
            if (id != null) {
                history.addReservation(r);
                reservationIds.add(id);
            }
        }

        // ===================== UC7 =====================
        AddOnServiceManager serviceManager = new AddOnServiceManager();
        if (!reservationIds.isEmpty()) {
            String resId = reservationIds.get(0);
            serviceManager.addService(resId, new AddOnService("Breakfast", 500));
            serviceManager.addService(resId, new AddOnService("Spa", 1000));

            System.out.println("\nAdd-On Service Selection");
            System.out.println("Reservation ID: " + resId);
            System.out.println("Total Add-On Cost: " +
                    serviceManager.calculateTotalServiceCost(resId));
        }

        // ===================== UC8 =====================
        BookingReportService report = new BookingReportService();
        System.out.println("\nBooking History and Reporting\n");
        report.generateReport(history);

        // ===================== UC9 =====================
        System.out.println("\nBooking Validation\n");
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter guest name: ");
        String guestName = scanner.nextLine();

        System.out.print("Enter room type (Single/Double/Suite): ");
        String roomType = scanner.nextLine();

        ReservationValidator validator = new ReservationValidator();

        try {
            validator.validate(guestName, roomType, inventory);
            System.out.println("Validation successful!");
        } catch (InvalidBookingException e) {
            System.out.println("Booking failed: " + e.getMessage());
        }

        // ===================== UC10 =====================
        System.out.println("\nBooking Cancellation\n");

        CancellationService cancelService = new CancellationService();

        if (!reservationIds.isEmpty()) {
            String cancelId = reservationIds.get(0);

            cancelService.registerBooking(cancelId, "Single");
            cancelService.cancelBooking(cancelId, inventory);
            cancelService.showRollbackHistory();

            System.out.println("\nUpdated Single Room Availability: "
                    + inventory.getRoomAvailability().get("Single"));
        }

        // ===================== UC11 START =====================
        System.out.println("\nConcurrent Booking Simulation\n");

        BookingRequestQueue concurrentQueue = new BookingRequestQueue();

        concurrentQueue.addRequest(new Reservation("Abhi", "Single"));
        concurrentQueue.addRequest(new Reservation("Vanmathi", "Double"));
        concurrentQueue.addRequest(new Reservation("Kural", "Suite"));
        concurrentQueue.addRequest(new Reservation("Subha", "Single"));

        RoomInventory sharedInventory = new RoomInventory();
        RoomAllocationService sharedAllocator = new RoomAllocationService();

        Thread t1 = new Thread(
                new ConcurrentBookingProcessor(concurrentQueue, sharedInventory, sharedAllocator)
        );

        Thread t2 = new Thread(
                new ConcurrentBookingProcessor(concurrentQueue, sharedInventory, sharedAllocator)
        );

        t1.start();
        t2.start();

        try {
            t1.join();
            t2.join();
        } catch (InterruptedException e) {
            System.out.println("Thread execution interrupted.");
        }

        System.out.println("\nRemaining Inventory:");
        for (String key : sharedInventory.getRoomAvailability().keySet()) {
            System.out.println(key + ": " + sharedInventory.getRoomAvailability().get(key));
        }
        // ===================== UC11 END =====================

        scanner.close();
    }
}


// ===================== ROOM =====================
abstract class Room {
    protected int beds;
    protected int size;
    protected double price;

    public Room(int b, int s, double p) {
        beds = b;
        size = s;
        price = p;
    }

    public void displayRoomDetails() {
        System.out.println("Beds: " + beds);
        System.out.println("Size: " + size + " sqft");
        System.out.println("Price per night: " + price);
    }
}

class SingleRoom extends Room { public SingleRoom() { super(1, 250, 1500); } }
class DoubleRoom extends Room { public DoubleRoom() { super(2, 400, 2500); } }
class SuiteRoom extends Room { public SuiteRoom() { super(3, 750, 5000); } }


// ===================== UC3 =====================
class RoomInventory {
    private Map<String, Integer> map = new HashMap<>();

    public RoomInventory() {
        map.put("Single", 5);
        map.put("Double", 3);
        map.put("Suite", 2);
    }

    public Map<String, Integer> getRoomAvailability() { return map; }
    public void updateAvailability(String t, int c) { map.put(t, c); }
}


// ===================== UC4 =====================
class RoomSearchService {
    public void searchAvailableRooms(RoomInventory inv, Room s, Room d, Room su) {
        Map<String, Integer> m = inv.getRoomAvailability();

        if (m.get("Single") > 0) {
            System.out.println("Single Room:");
            s.displayRoomDetails();
            System.out.println("Available: " + m.get("Single") + "\n");
        }
        if (m.get("Double") > 0) {
            System.out.println("Double Room:");
            d.displayRoomDetails();
            System.out.println("Available: " + m.get("Double") + "\n");
        }
        if (m.get("Suite") > 0) {
            System.out.println("Suite Room:");
            su.displayRoomDetails();
            System.out.println("Available: " + m.get("Suite") + "\n");
        }
    }
}


// ===================== UC5 =====================
class Reservation {
    private String name, type;

    public Reservation(String n, String t) {
        name = n;
        type = t;
    }

    public String getGuestName() { return name; }
    public String getRoomType() { return type; }
}

class BookingRequestQueue {
    private Queue<Reservation> q = new LinkedList<>();

    public void addRequest(Reservation r) { q.offer(r); }

    public Reservation getNextRequest() { return q.poll(); }

    public boolean hasPendingRequests() { return !q.isEmpty(); }
}


// ===================== UC6 =====================
class RoomAllocationService {
    private Map<String, Integer> count = new HashMap<>();

    public String allocateRoom(Reservation r, RoomInventory inv) {
        String t = r.getRoomType();
        Map<String, Integer> m = inv.getRoomAvailability();

        if (!m.containsKey(t) || m.get(t) <= 0) return null;

        int c = count.getOrDefault(t, 0) + 1;
        count.put(t, c);

        String id = t + "-" + c;
        inv.updateAvailability(t, m.get(t) - 1);

        System.out.println("Booking confirmed for Guest: " + r.getGuestName() + ", Room ID: " + id);
        return id;
    }
}


// ===================== UC7 =====================
class AddOnService {
    private double cost;
    public AddOnService(String n, double c) { cost = c; }
    public double getCost() { return cost; }
}

class AddOnServiceManager {
    private Map<String, List<AddOnService>> map = new HashMap<>();

    public void addService(String id, AddOnService s) {
        map.putIfAbsent(id, new ArrayList<>());
        map.get(id).add(s);
    }

    public double calculateTotalServiceCost(String id) {
        double total = 0;
        if (map.containsKey(id)) {
            for (AddOnService s : map.get(id)) total += s.getCost();
        }
        return total;
    }
}


// ===================== UC8 =====================
class BookingHistory {
    private List<Reservation> list = new ArrayList<>();
    public void addReservation(Reservation r) { list.add(r); }
    public List<Reservation> getConfirmedReservations() { return list; }
}

class BookingReportService {
    public void generateReport(BookingHistory h) {
        System.out.println("Booking History Report\n");
        for (Reservation r : h.getConfirmedReservations()) {
            System.out.println("Guest: " + r.getGuestName() + ", Room Type: " + r.getRoomType());
        }
    }
}


// ===================== UC9 =====================
class InvalidBookingException extends Exception {
    public InvalidBookingException(String message) { super(message); }
}

class ReservationValidator {
    public void validate(String guestName, String roomType, RoomInventory inventory)
            throws InvalidBookingException {

        if (guestName == null || guestName.trim().isEmpty())
            throw new InvalidBookingException("Guest name cannot be empty.");

        if (!inventory.getRoomAvailability().containsKey(roomType))
            throw new InvalidBookingException("Invalid room type selected.");

        if (inventory.getRoomAvailability().get(roomType) <= 0)
            throw new InvalidBookingException("No rooms available.");
    }
}


// ===================== UC10 =====================
class CancellationService {
    private Stack<String> releasedRoomIds = new Stack<>();
    private Map<String, String> reservationRoomTypeMap = new HashMap<>();

    public void registerBooking(String id, String type) {
        reservationRoomTypeMap.put(id, type);
    }

    public void cancelBooking(String id, RoomInventory inventory) {
        if (!reservationRoomTypeMap.containsKey(id)) {
            System.out.println("Invalid cancellation request.");
            return;
        }

        String type = reservationRoomTypeMap.get(id);
        releasedRoomIds.push(id);

        int current = inventory.getRoomAvailability().get(type);
        inventory.updateAvailability(type, current + 1);

        reservationRoomTypeMap.remove(id);

        System.out.println("Booking cancelled successfully. Inventory restored for room type: " + type);
    }

    public void showRollbackHistory() {
        System.out.println("\nRollback History (Most Recent First):");
        for (String id : releasedRoomIds) {
            System.out.println("Released Reservation ID: " + id);
        }
    }
}


// ===================== UC11 =====================
class ConcurrentBookingProcessor implements Runnable {

    private BookingRequestQueue bookingQueue;
    private RoomInventory inventory;
    private RoomAllocationService allocationService;

    public ConcurrentBookingProcessor(
            BookingRequestQueue bookingQueue,
            RoomInventory inventory,
            RoomAllocationService allocationService) {

        this.bookingQueue = bookingQueue;
        this.inventory = inventory;
        this.allocationService = allocationService;
    }

    @Override
    public void run() {

        while (true) {
            Reservation reservation;

            synchronized (bookingQueue) {
                if (!bookingQueue.hasPendingRequests()) break;
                reservation = bookingQueue.getNextRequest();
            }

            synchronized (inventory) {
                allocationService.allocateRoom(reservation, inventory);
            }
        }
    }
}