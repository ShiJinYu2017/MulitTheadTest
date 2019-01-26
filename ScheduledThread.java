import java.util.Date;

public class ScheduledThread extends Thread {
    private int i;

    public ScheduledThread(int in) {
        this.i = in;
    }

    @Override
    public void run() {
        /*while (true) {
            try {
                this.sleep(2000);
            } catch (InterruptedException E) {
                E.printStackTrace();
            }
            //SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = new Date();
            System.out.println(currentThread().getName()+"打印编号："+i+"======>"+date);//答应当前时间
        }*/
        Date date = new Date();
        System.out.println(currentThread().getName()+"打印编号："+i+"======>"+date);//答应当前时间
    }
}
