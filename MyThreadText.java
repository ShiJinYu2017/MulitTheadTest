
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MyThreadText {
    public static void main(String[] args) {
        //ExecutorService executorService = Executors.newSingleThreadExecutor();
        //ExecutorService executorService = Executors.newFixedThreadPool(5);//指定线程数
        ExecutorService executorService = Executors.newCachedThreadPool();
        for (int i = 0; i < 10; i++) {
            executorService.execute(new MyThread(i));
        }
        executorService.shutdown();
    }
}
