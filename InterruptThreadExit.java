import java.util.concurrent.TimeUnit;

public class InterruptThreadExit {
    public static void main(String[] args) throws InterruptedException{
        Thread t = new Thread(){
            public void run() {
                System.out.println("The Thread will start work!");
                while (!isInterrupted()) {

                }
                System.out.println("The Thread will be exiting!");
            }
        };
        t.start();
        TimeUnit.SECONDS.sleep(10);//主线程睡眠10秒
        System.out.println("System will be shutdown！");
        t.interrupt();//中断线程t
    }
}
