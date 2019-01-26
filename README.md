# 简介
线程Thread是一个重量级资源，线程的创建、启动以及销毁都是比较耗费系统资源的，同时受限于系统资源的限制，线程的数量与系统性能是一种抛物线的关系，因此对线程的管理，是一种非常好的程序设计习惯，自JDK1.5起，utils包提供了ExecutorService[ɪɡˈzɛkjətɚ]线程池的实现。通俗的将：为了避免重复的创建线程，线程池的出现可以让线程进行复用。当有工作来，就会向线程池拿一个线程，当工作完成后，并不是直接关闭线程，而是将这个线程归还给线程池供其他任务使用。
 一个线程池包括以下四个基本组成部分：
* 1、线程池管理器（ThreadPool）：用于创建并管理线程池，包括 创建线程池，销毁线程池，添加新任务；
* 2、工作线程（PoolWorker）：线程池中线程，在没有任务时处于等待状态，可以循环的执行任务；
* 3、任务接口（Task）：每个任务必须实现的接口，以供工作线程调度任务的执行，它主要规定了任务的入口，任务执行完后的收尾工作，任务的执行状态等；
* 4、任务队列（taskQueue）：用于存放没有处理的任务。提供一种缓冲机制。

## 线程池的作用
线程池作用就是限制系统中执行线程的数量。
根据系统的环境情况，可以自动或手动设置线程数量，达到运行的最佳效果；少了浪费了系统资源，多了造成系统拥挤效率不高。用线程池控制线程数量，其他线程排队等候。一个任务执行完毕，再从队列的中取最前面的任务开始执行。若队列中没有等待进程，线程池的这一资源处于等待。当一个新任务需要运行时，如果线程池中有等待的工作线程，就可以开始运行了；否则进入等待队列。
* 减少了创建和销毁线程的次数，每个工作线程都可以被重复利用，可执行多个任务。
* 可以根据系统的承受能力，调整线程池中工作线线程的数目，防止因线程过多消耗内存，也避免了因线程过少，浪费系统资源。
## 线程池的创建
Java里面线程池的顶级接口是Executor，但是严格意义上讲Executor并不是一个线程池，而只是一个执行线程的工具。真正的线程池接口是ExecutorService。要配置一个线程池是比较复杂的，尤其是对于线程池的原理不是很清楚的情况下，很有可能配置的线程池不是较优的，因此在Executors类里面提供了一些静态工厂，生成一些常用的线程池。
1. newSingleThreadExecutor
说明：初始化只有一个线程的线程池，内部使用LinkedBlockingQueue作为阻塞队列。
特点：相当于单线程串行执行所有任务如果该线程异常结束，会重新创建一个新的线程继续执行任务，唯一的线程可以保证所提交任务的顺序执行
2. newFixedThreadPool
说明：初始化一个指定线程数的线程池，其中corePoolSize == maxiPoolSize，使用LinkedBlockingQuene作为阻塞队列
特点：每次提交一个任务就创建一个线程，直到线程达到线程池的最大大小。线程池的大小一旦达到最大值就会保持不变，即使当线程池没有可执行任务时，也不会释放线程。如果某个线程因为执行异常而结束，那么线程池会补充一个新线程。
3. newCachedThreadPool
说明：初始化一个可以缓存线程的线程池，此线程池不会对线程池大小做限制，线程池大小完全依赖于操作系统（或者说JVM）能够创建的最大线程大小。线程池的线程数可达到Integer.MAX_VALUE，即2147483647，内部使用SynchronousQueue作为阻塞队列；
特点：在没有任务执行时，当线程的空闲时间超过keepAliveTime，默认为60s，会自动释放线程资源；当提交新任务时，如果没有空闲线程，则创建新线程执行任务，会导致一定的系统开销；
因此，使用时要注意控制并发的任务数，防止因创建大量的线程导致而降低性能。
4. newScheduledThreadPool
特定：初始化的线程池可以在指定的时间内周期性的执行所提交的任务，在实际的业务场景中可以使用该线程池定期的同步数据。
总结：除了newScheduledThreadPool的内部实现特殊一点之外，其它线程池内部都是基于ThreadPoolExecutor类（Executor的子类）实现的。
## 线程池的状态
其中AtomicInteger变量ctl的功能非常强大：利用低29位表示线程池中线程数，通过高3位表示线程池的运行状态：
1、RUNNING：-1 << COUNT_BITS，即高3位为111，该状态的线程池会接收新任务，并处理阻塞队列中的任务；
2、SHUTDOWN： 0 << COUNT_BITS，即高3位为000，该状态的线程池不会接收新任务，但会处理阻塞队列中的任务；
3、STOP ： 1 << COUNT_BITS，即高3位为001，该状态的线程不会接收新任务，也不会处理阻塞队列中的任务，而且会中断正在运行的任务；
4、TIDYING ： 2 << COUNT_BITS，即高3位为010，该状态表示线程池对线程进行整理优化；
5、TERMINATED： 3 << COUNT_BITS，即高3位为011，该状态表示线程池停止工作；

## 线程池的关闭
1. ThreadPoolExecutor提供了两个方法，用于线程池的关闭，分别是shutdown()和shutdownNow()，其中：
shutdown()：不会立即终止线程池，而是要等所有任务缓存队列中的任务都执行完后才终止，但再也不会接受新的任务
shutdownNow()：立即终止线程池，并尝试打断正在执行的任务，并且清空任务缓存队列，返回尚未执行的任务

# 应用实例
首先创建一个自定义的线程，模拟并发任务。
```
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
```
## 1、newSingleThreadExecutor
```
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MyThreadText {
    public static void main(String[] args) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        for (int i = 0; i < 10; i++) {
            executorService.execute(new MyThread(i));
        }
        executorService.shutdown();
    }
}
```

## 2、newFixedThreadPool
```
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MyThreadText {
    public static void main(String[] args) {
        ExecutorService executorService = Executors.newFixedThreadPool(5);
        for (int i = 0; i < 10; i++) {
            executorService.execute(new MyThread(i));
        }
        executorService.shutdown();
    }
}
```

## 3、newCachedThreadPool
```
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MyThreadText {
    public static void main(String[] args) {
        ExecutorService executorService = Executors.newCachedThreadPool();
        for (int i = 0; i < 10; i++) {
            executorService.execute(new MyThread(i));
        }
        executorService.shutdown();
    }
}
```

## 4、newScheduledThreadPool
为了体现其在指定时间内周期性的执行所提交的任务我们编写一个循环打印当前时间的线程
```
import java.text.SimpleDateFormat;
import java.util.Date;

public class ScheduledThread extends Thread {
    private int i;

    public ScheduledThread(int in) {
        this.i = in;
    }

    @Override
    public void run() {
        while (true) {
            try {
                this.sleep(2000);
            } catch (InterruptedException E) {
                E.printStackTrace();
            }
            //SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = new Date();
            System.out.println(currentThread().getName()+"打印编号："+i+"======>"+date);//答应当前时间
        }
    }
}
```
编写验证的主程序，设置线程池有5个线程可用，：
```
import java.util.concurrent.ScheduledThreadPoolExecutor;

public class ScheduledThreadTest {
    public static void main(String[] args) {
        ScheduledThreadPoolExecutor atpe = new ScheduledThreadPoolExecutor(5);//设置线程个数
        for (int i = 0; i < 5; i++) {
            atpe.execute(new ScheduledThread(i));//普通的提交方式，只提交一次，执行结束，线程不会退出。
        }
    }
}
```

通过实验我们发现，本实验例程需要创建的线程数应小于等于线程池的线程容量，否则线程不会回收。具体表现在，当  for (int i = 0; i < 5; i++) 修改为 for (int i = 0; i <10 i++) 以后，仍然只有前5个线程执行，因为线程循环执行，会一直占用线程池的资源。
为了验证这一猜想我们将程序修改如下
```
import java.util.Date;
public class ScheduledThread extends Thread {
    private int i;
    public ScheduledThread(int in) {
        this.i = in;
    }

    @Override
    public void run() {
        Date date = new Date();
        System.out.println(currentThread().getName()+"打印编号："+i+"======>"+date);//答应当前时间
    }
}
//主程序如下：
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
public class ScheduledThreadTest {
    public static void main(String[] args) {
        ScheduledThreadPoolExecutor atpe = new ScheduledThreadPoolExecutor(4);//设置线程个数
        for (int i = 0; i < 5; i++) {
            atpe.execute(new ScheduledThread(i));
        }
    }
}
```
得到如下结果：

程序正常结束，且线程3被重复利用，并没达到线程池的最大容量4。
我们可以这样认为，newScheduledThreadPool这线程池可以使只执行一遍的线程以一定速率循环执行，但是如果以execute方式提交线程则不会重复执行。
我们对程序作出如下修改，使线程只执行一次：
```
import java.util.Date;

public class ScheduledThread extends Thread {
    private int i;

    public ScheduledThread(int in) {
        this.i = in;
    }

    @Override
    public void run() {
        Date date = new Date();
        System.out.println(currentThread().getName()+"打印编号："+i+"======>"+date);//答应当前时间
    }
}
```
同时主程序修改为：
```
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
public class ScheduledThreadTest {
    public static void main(String[] args) {
        ScheduledThreadPoolExecutor atpe = new ScheduledThreadPoolExecutor(4);//设置线程个数
        for (int i = 0; i < 5; i++) {
             //参数1：initialDelay表示首次执行任务的延迟时间，参数2：period表示每次执行任务的间隔时间，参数3：TimeUnit.MILLISECONDS执行的时间间隔数值单位
            atpe.scheduleAtFixedRate(new ScheduledThread(i),1000,2000,TimeUnit.MILLISECONDS);//以固定频率重复执行线程
        }
    }
}
```
可以得到类似的结果：

我们可以发现线程2实现了重复利用，虽然创建的线程是一次执行，但却实现了重复执行的效果，这就是该线程池最大的特点。
# 参考：
https://www.cnblogs.com/aspirant/p/6920418.html
https://www.cnblogs.com/superfj/p/7544971.html
https://www.cnblogs.com/zhaojinxin/p/6668247.html

