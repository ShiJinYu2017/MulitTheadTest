import java.util.concurrent.TimeUnit;

public class FlagThreadExit {
    public static void main(String[] args) throws InterruptedException{
        MyTask t = new MyTask();
        t.start();
        TimeUnit.SECONDS.sleep(10);
        System.out.println("Sysem will be shutdown");
        t.close();
    }

    static class MyTask extends Thread {
        private volatile boolean closed = false;

        public void run() {
            System.out.println("I will start work!");
            while (!closed && !isInterrupted()) {

            }
            System.out.println("I will be exiting.");

        }

        public void close(){
            this.closed = true;
            this.interrupt();
        }
    }
}
