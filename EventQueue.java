import jdk.jfr.Event;

import java.sql.SQLOutput;
import java.util.LinkedList;

import static java.lang.Thread.currentThread;

public class EventQueue {
    //用于学习线程之间的通信，该Queue有如下三种状态：
    //对列满
    //对列空
    //对列添加成功
    private final int max;
    //final修饰的变量不能被重复赋值，同时必须在初始化时给定初值
    // （给定初值可以在该类的构造函数中实现，即如果该类声明了
    // final类的变量，那么必须声明时赋值，或者在构造函数中赋值。）

    static class Event {
        //我们不在意该事件做什么，因此为一个空类；
    }

    private final LinkedList<Event> eventQueue = new LinkedList<>();//被final修饰的引用变量只能指向一个对象。

    private final static int DEFAULT_MAX_EVENT = 10;//相当于静态全局变量，对于该类的所有对象，共用同一个内存空间中的值。

    public EventQueue() {
        this(DEFAULT_MAX_EVENT);
    }

    public EventQueue(int max) {
        this.max = max;
    }
    //定义一个添加事件到对列尾的方法
    public void offer(Event event) {
        //使用synchronized关键字用于同步代码块
        //synchronized ()中参数用于指定线程将获取与eventQueue对象关联的monitor锁
        synchronized (eventQueue) {
            if (eventQueue.size() >= max) {
                try {
                    console("the queue is full.");
                    eventQueue.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            console("the new event is submitted");
            eventQueue.addLast(event);
            eventQueue.notify();
        }
    }
    //移除对列头的事件对象
    public Event take() {
        synchronized (eventQueue) {
            if (eventQueue.isEmpty()) {
                try {
                    console("the queue is empty.");
                    eventQueue.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            Event event = eventQueue.removeFirst();
            //this.eventQueue.notify();
            eventQueue.notify();
            console("the event " + event + "is handled.");
            return event;
        }
    }
    //定义一个打印信息的方法
    private void console(String s) {
        System.out.printf("%s:%s\n",currentThread().getName(),s);
    }
}
