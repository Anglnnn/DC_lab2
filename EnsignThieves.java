import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.Random;

public class EnsignThieves {

    private static final int NUM_ENSIGNS = 3;
    private static final int NUM_WEAPONS = 150;
    private static int totalValue = 0;
    private static Lock lock = new ReentrantLock();
    private static int ensignIndex = 0;

    public static List<Ensign> ensigns = new ArrayList<>();

    public static void main(String[] args) {
        // Create a blocking queue to store the stolen weapons
        BlockingQueue<Weapon> stolenWeaponQueue = new LinkedBlockingQueue<>();

        // Initialize the weapons queue
        for (int i = 1; i <= NUM_WEAPONS; i++) {
            stolenWeaponQueue.add(new Weapon("Weapon", new Random().nextInt(100)));
        }

        // Create a list to store the ensigns
        ensigns.add(new Ensign("Ivanov", stolenWeaponQueue, "weapon"));
        ensigns.add(new Ensign("Petrov", stolenWeaponQueue, "truck"));
        ensigns.add(new Ensign("Nechiporchuk", stolenWeaponQueue, "value"));

        // Start all the ensigns working
        for (Ensign ensign : ensigns) {
            ensign.start();
        }

        // Wait for all the ensigns to finish working
        for (Ensign ensign : ensigns) {
            try {
                ensign.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        // Print the total value of the stolen weapons
        System.out.println("The total value of the stolen weapons is: " + totalValue);
    }

    private static class Weapon {
        private final String name;
        private final int value;

        public Weapon(String name, int value) {
            this.name = name;
            this.value = value;
        }

        public String getName() {
            return name;
        }

        public int getValue() {
            return value;
        }
    }

    private static class Ensign extends Thread {

        private final String name;
        private final BlockingQueue<Weapon> stolenWeaponQueue;
        private final String rangeOfResponsibility;

        public Ensign(String name, BlockingQueue<Weapon> stolenWeaponQueue, String rangeOfResponsibility) {
            this.name = name;
            this.stolenWeaponQueue = stolenWeaponQueue;
            this.rangeOfResponsibility = rangeOfResponsibility;
        }

        @Override
        public void run() {
            // Acquire the lock
            lock.lock();

            // While the weapons queue is not empty, perform the appropriate action for the next weapon in the queue.
            while (!stolenWeaponQueue.isEmpty()) {
                // Get the next weapon from the queue
                Weapon weapon = null;
                try {
                    weapon = stolenWeaponQueue.take();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

                // Determine the ensign to perform the action
                Ensign ensign = ensigns.get(ensignIndex);
                ensignIndex = (ensignIndex + 1) % ensigns.size();

                // Perform the appropriate action based on the range of responsibility
                switch (ensign.rangeOfResponsibility) {
                    case "weapon":
                        // Take the weapon from the warehouse
                        System.out.println("Ensign " + ensign.name + " took weapon "  + "from warehouse.");
                        break;
                    case "truck":
                        // Load the weapon into the truck
                        System.out.println("Ensign " + ensign.name + " loaded weapon " +  "into the truck.");
                        break;
                    case "value":
                        // Calculate the value of the weapon
                        System.out.println("Ensign " + ensign.name + " calculated the value of weapon " + "to be " + weapon.getValue() + "." + "\n");
                        totalValue += weapon.getValue();
                        break;
                }


            }

            // Release the lock
            lock.unlock();
        }
    }
}