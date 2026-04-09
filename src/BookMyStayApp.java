import java.util.*;

public class BookMyStayApp {
    public static void main(String[] args) {

        // ===================== UC1 START =====================
        System.out.println("Welcome to the Hotel Booking Management System");
        System.out.println("System initialized successfully.\n");
        // ===================== UC1 END =======================


        // ===================== UC2 START =====================
        Room single = new SingleRoom();
        Room dbl = new DoubleRoom();
        Room suite = new SuiteRoom();

        int singleAvailable = 5;
        int doubleAvailable = 3;
        int suiteAvailable = 2;

        System.out.println("Hotel Room Initialization\n");

        System.out.println("Single Room:");
        single.displayRoomDetails();
        System.out.println("Available: " + singleAvailable + "\n");

        System.out.println("Double Room:");
        dbl.displayRoomDetails();
        System.out.println("Available: " + doubleAvailable + "\n");

        System.out.println("Suite Room:");
        suite.displayRoomDetails();
        System.out.println("Available: " + suiteAvailable + "\n");
        // ===================== UC2 END =======================


        // ===================== UC3 START =====================
        RoomInventory inventory = new RoomInventory();
        Map<String, Integer> availability = inventory.getRoomAvailability();

        System.out.println("Hotel Room Inventory Status\n");

        System.out.println("Single Room:");
        single.displayRoomDetails();
        System.out.println("Available Rooms: " + availability.get("Single") + "\n");

        System.out.println("Double Room:");
        dbl.displayRoomDetails();
        System.out.println("Available Rooms: " + availability.get("Double") + "\n");

        System.out.println("Suite Room:");
        suite.displayRoomDetails();
        System.out.println("Available Rooms: " + availability.get("Suite") + "\n");
        // ===================== UC3 END =======================


        // ===================== UC4 START =====================
        RoomSearchService searchService = new RoomSearchService();

        System.out.println("Room Search\n");
        searchService.searchAvailableRooms(inventory, single, dbl, suite);
        // ===================== UC4 END =======================


        // ===================== UC5 START =====================
        BookingRequestQueue queue = new BookingRequestQueue();

        queue.addRequest(new Reservation("Abhi", "Single"));
        queue.addRequest(new Reservation("Subha", "Single"));
        queue.addRequest(new Reservation("Vanmathi", "Suite"));
        // ===================== UC5 END =======================


        // ===================== UC6 START =====================
        RoomAllocationService allocationService = new RoomAllocationService();

        System.out.println("\nRoom Allocation Processing");

        List<String> reservationIds = new ArrayList<>();

        while (queue.hasPendingRequests()) {
            Reservation r = queue.getNextRequest();
            String id = allocationService.allocateRoom(r, inventory);
            if (id != null) {
                reservationIds.add(id);
            }
        }
        // ===================== UC6 END =======================


        // ===================== UC7 START =====================
        AddOnServiceManager serviceManager = new AddOnServiceManager();

        if (!reservationIds.isEmpty()) {
            String resId = reservationIds.get(0);

            serviceManager.addService(resId, new AddOnService("Breakfast", 500));
            serviceManager.addService(resId, new AddOnService("Spa", 1000));

            double total = serviceManager.calculateTotalServiceCost(resId);

            System.out.println("\nAdd-On Service Selection");
            System.out.println("Reservation ID: " + resId);
            System.out.println("Total Add-On Cost: " + total);
        }
        // ===================== UC7 END =======================
    }
}


// ===================== SHARED CLASSES =====================

abstract class Room {
    protected int numberOfBeds;
    protected int squareFeet;
    protected double pricePerNight;

    public Room(int numberOfBeds, int squareFeet, double pricePerNight) {
        this.numberOfBeds = numberOfBeds;
        this.squareFeet = squareFeet;
        this.pricePerNight = pricePerNight;
    }

    public void displayRoomDetails() {
        System.out.println("Beds: " + numberOfBeds);
        System.out.println("Size: " + squareFeet + " sqft");
        System.out.println("Price per night: " + pricePerNight);
    }
}

class SingleRoom extends Room {
    public SingleRoom() {
        super(1, 250, 1500.0);
    }
}

class DoubleRoom extends Room {
    public DoubleRoom() {
        super(2, 400, 2500.0);
    }
}

class SuiteRoom extends Room {
    public SuiteRoom() {
        super(3, 750, 5000.0);
    }
}


// ===================== UC3 =====================

class RoomInventory {
    private Map<String, Integer> roomAvailability = new HashMap<>();

    public RoomInventory() {
        roomAvailability.put("Single", 5);
        roomAvailability.put("Double", 3);
        roomAvailability.put("Suite", 2);
    }

    public Map<String, Integer> getRoomAvailability() {
        return roomAvailability;
    }

    public void updateAvailability(String type, int count) {
        roomAvailability.put(type, count);
    }
}


// ===================== UC4 =====================

class RoomSearchService {
    public void searchAvailableRooms(RoomInventory inventory, Room s, Room d, Room su) {
        Map<String, Integer> map = inventory.getRoomAvailability();

        if (map.get("Single") > 0) {
            System.out.println("Single Room:");
            s.displayRoomDetails();
            System.out.println("Available: " + map.get("Single") + "\n");
        }

        if (map.get("Double") > 0) {
            System.out.println("Double Room:");
            d.displayRoomDetails();
            System.out.println("Available: " + map.get("Double") + "\n");
        }

        if (map.get("Suite") > 0) {
            System.out.println("Suite Room:");
            su.displayRoomDetails();
            System.out.println("Available: " + map.get("Suite") + "\n");
        }
    }
}


// ===================== UC5 =====================

class Reservation {
    private String guestName;
    private String roomType;

    public Reservation(String g, String r) {
        guestName = g;
        roomType = r;
    }

    public String getGuestName() { return guestName; }
    public String getRoomType() { return roomType; }
}

class BookingRequestQueue {
    private Queue<Reservation> q = new LinkedList<>();

    public void addRequest(Reservation r) { q.offer(r); }
    public Reservation getNextRequest() { return q.poll(); }
    public boolean hasPendingRequests() { return !q.isEmpty(); }
}


// ===================== UC6 =====================

class RoomAllocationService {
    private Set<String> allocated = new HashSet<>();
    private Map<String, Integer> countMap = new HashMap<>();

    public String allocateRoom(Reservation r, RoomInventory inv) {
        String type = r.getRoomType();
        Map<String, Integer> map = inv.getRoomAvailability();

        if (map.get(type) <= 0) {
            System.out.println("No rooms available for " + type);
            return null;
        }

        int count = countMap.getOrDefault(type, 0) + 1;
        countMap.put(type, count);

        String id = type + "-" + count;

        if (allocated.contains(id)) return null;

        allocated.add(id);
        inv.updateAvailability(type, map.get(type) - 1);

        System.out.println("Booking confirmed for Guest: " + r.getGuestName() + ", Room ID: " + id);

        return id;
    }
}


// ===================== UC7 =====================

class AddOnService {
    private String serviceName;
    private double cost;

    public AddOnService(String serviceName, double cost) {
        this.serviceName = serviceName;
        this.cost = cost;
    }

    public String getServiceName() { return serviceName; }
    public double getCost() { return cost; }
}

class AddOnServiceManager {
    private Map<String, List<AddOnService>> services = new HashMap<>();

    public void addService(String id, AddOnService s) {
        services.putIfAbsent(id, new ArrayList<>());
        services.get(id).add(s);
    }

    public double calculateTotalServiceCost(String id) {
        double total = 0;
        if (services.containsKey(id)) {
            for (AddOnService s : services.get(id)) {
                total += s.getCost();
            }
        }
        return total;
    }
}