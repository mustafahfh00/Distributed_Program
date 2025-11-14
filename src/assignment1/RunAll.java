package assignment1;

/**
 * RunAll
 * ------------------------------------------------------------
 * Convenience launcher for testing:
 *  - Starts P1–P6 worker processes in separate threads
 *  - Starts Main process (P0)
 *
 * NOTE:
 *  In real distributed systems, each process runs on a
 *  separate machine / JVM. This class is only for testing.
 *
 * References:
 *  - SE355 Notebook (Distributed Process Model)
 */
public class RunAll {

    public static void main(String[] args) throws Exception {

        // Launch workers P1–P6
        for (int workerId = 1; workerId <= 6; workerId++) {
            int id = workerId;

            new Thread(() -> {
                try {
                    new WorkerProcess(id, 7).startWorker();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();

            Thread.sleep(400); // small delay so ports open in order
        }

        // Launch Main process P0
        new MainProcess().run();
    }
}
