import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CountDownLatch;

public class WinnieThePoohSearch {

    private static final int NUM_BEE_SQUADS = 5;

    public static void main(String[] args) {
        List<BeeSquad> beeSquads = new ArrayList<>();
        for (int i = 0; i < NUM_BEE_SQUADS; i++) {
            beeSquads.add(new BeeSquad(i));
        }

        CountDownLatch latch = new CountDownLatch(NUM_BEE_SQUADS);

        for (BeeSquad beeSquad : beeSquads) {
            beeSquad.start();
        }

        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static class WinnieThePoohFound {
        private static boolean found = false;

        public static boolean get() {
            return found;
        }

        public static void set(boolean found) {
            WinnieThePoohFound.found = found;
        }
    }

    private static class BeeSquad extends Thread {

        private final int id;

        public BeeSquad(int id) {
            this.id = id;
        }

        @Override
        public void run() {

            if (!WinnieThePoohFound.get() && new Random().nextInt(10) == 0) {
                WinnieThePoohFound.set(true);
                System.out.println("Bee Squad #" + id + " found the bear and performed a show of punishment");
            } else {
                System.out.println("Bee Squad #" + id + " was unsuccessful and returned to hive");
            }


            CountDownLatch latch = new CountDownLatch(1);
            latch.countDown();
        }
    }
}
