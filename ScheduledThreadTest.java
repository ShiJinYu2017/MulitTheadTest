import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ScheduledThreadTest {
    public static void main(String[] args) {
        ScheduledThreadPoolExecutor atpe = new ScheduledThreadPoolExecutor(4);//设置线程个数
        for (int i = 0; i < 5; i++) {
            //atpe.execute(new ScheduledThread(i));
            atpe.scheduleAtFixedRate(new ScheduledThread(i),1000,2000,TimeUnit.MILLISECONDS);
            //参数1：initialDelay表示首次执行任务的延迟时间，参数2：period表示每次执行任务的间隔时间，参数3：TimeUnit.MILLISECONDS执行的时间间隔数值单位
        }

    }
}
