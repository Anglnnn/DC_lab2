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
        BlockingQueue<Weapon> stolenWeaponQueue = new LinkedBlockingQueue<>();

        for (int i = 1; i <= NUM_WEAPONS; i++) {
            stolenWeaponQueue.add(new Weapon("Weapon", new Random().nextInt(100)));
        }

        ensigns.add(new Ensign("Ivanov", stolenWeaponQueue, "weapon"));
        ensigns.add(new Ensign("Petrov", stolenWeaponQueue, "truck"));
        ensigns.add(new Ensign("Nechiporchuk", stolenWeaponQueue, "value"));

        for (Ensign ensign : ensigns) {
            ensign.start();
        }

        for (Ensign ensign : ensigns) {
            try {
                ensign.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

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
            lock.lock();

            while (!stolenWeaponQueue.isEmpty()) {
                Weapon weapon = null;
                try {
                    weapon = stolenWeaponQueue.take();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

                Ensign ensign = ensigns.get(ensignIndex);
                ensignIndex = (ensignIndex + 1) % ensigns.size();

                switch (ensign.rangeOfResponsibility) {
                    case "weapon":
                        System.out.println("Ensign " + ensign.name + " took weapon "  + "from warehouse.");
                        break;
                    case "truck":
                        System.out.println("Ensign " + ensign.name + " loaded weapon " +  "into the truck.");
                        break;
                    case "value":
                        System.out.println("Ensign " + ensign.name + " calculated the value of weapon " + "to be " + weapon.getValue() + "." + "\n");
                        totalValue += weapon.getValue();
                        break;
                }


            }

            lock.unlock();
        }
    }
}