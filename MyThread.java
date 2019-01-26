public class MyThread extends Thread {
    private int i;
    public MyThread(int in) {
        this.i = in;
    }
    public void run() {
        try {
            this.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(currentThread().getName()+"正在打印："+i);
    }
}
