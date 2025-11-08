package assignment1;

public class RunAll {
    public static void main(String[] args) throws Exception {
        // Launch 6 workers
        for (int i = 1; i <= 6; i++) {
            final int pid = i;
            new Thread(() -> {
                try {
                    WorkerProcess wp = new WorkerProcess(pid);
                    wp.runLoop();
                } catch (Exception e) { e.printStackTrace(); }
            }, "worker-" + i).start();
        }

        // Give workers time to start listening
        Thread.sleep(2000); // <-- add this line

        // Launch Main
        String paragraph = (args.length == 0)
                ? "Hello from AUIS this is SE355 assignment one"
                : String.join(" ", args);

        MainProcess main = new MainProcess(paragraph);
        main.runScenario();

        Thread.sleep(1000);
        System.exit(0);
    }
}
