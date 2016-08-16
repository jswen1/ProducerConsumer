import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

class BoundedBuffer  {  
	  final Lock lock = new ReentrantLock();          //锁对象  
	  final Condition notFull  = lock.newCondition(); //写线程锁  
	  final Condition notEmpty = lock.newCondition(); //读线程锁  
	  
	  public final Object[] items = new Object[100];//缓存队列  
	  public volatile int putptr = 0;  //写索引  
	  public volatile int takeptr = 0; //读索引  
	  public volatile int count = 0;   //队列中数据数目  
	  public DateFormat df = new SimpleDateFormat("HH:mm:ss");
	  //写  
	  public  void put(Object x) throws InterruptedException {  
	    lock.lock(); //锁定  
	    try {  
	      // 如果队列满，则阻塞<写线程>  
	      while (count == items.length) {
	    	  Thread thread = Thread.currentThread();
	    	  System.out.println("生产者阻塞了！" + thread);
	    	  notFull.await();   
	      }  
	      // 写入队列，并更新写索引  
	      items[putptr] = x; 
	     // System.out.print("我要睡眠了！"+(df.format(new Date())));
	      //Thread.sleep(10000);
	      //System.out.println("我睡醒了！"+(df.format(new Date())));
	      System.out.print("生产者已生产"+putptr+"号产品！");
	      System.out.println("生产的产品为"+x);
	      if (++putptr == items.length) putptr = 0;   
	      ++count;  
	  
	      // 唤醒<读线程>  
	      notEmpty.signal();   
	    } finally {	    	
	    	lock.unlock();//解除锁定 
	    }   
	  }  
	  
	  //读   
	  public  Object take() throws InterruptedException {   
	    lock.lock(); //锁定   
	    try {  
	      // 如果队列空，则阻塞<读线程>  
	      while (count == 0) {
	    	  Thread thread = Thread.currentThread();
	    	  System.out.println("消费者阻塞了！ "+thread);
	    	  notEmpty.await();  
	      }  
	  
	      //读取队列，并更新读索引  
	      Object x = items[takeptr]; 
	      //System.out.print("我要睡眠了！"+(df.format(new Date())));
	      //Thread.sleep(10000);
	      //System.out.println("我睡醒了！"+(df.format(new Date())));
	      System.out.print("消费者已消费"+takeptr+"号产品！");
	      System.out.println("消费的产品为"+x);
	      if (++takeptr == items.length) takeptr = 0;
	      --count;  
	  
	      // 唤醒<写线程>  
	      notFull.signal();   
	      return x;   
	    } finally {
	    	Thread.sleep(2000);
	    	lock.unlock();//解除锁定   
	    }   
	  }
 
	  public static void main(String []args){
		  ScheduledExecutorService pool = Executors.newScheduledThreadPool(3); //生产者线程池
		  ScheduledExecutorService pool0 = Executors.newScheduledThreadPool(2); //消费者线程池
		  BoundedBuffer boundedBuffer = new BoundedBuffer();
		  Producer producer = new Producer(boundedBuffer);
		  Consumer consumer = new Consumer(boundedBuffer);
		  pool.scheduleAtFixedRate(producer, 1, 1, TimeUnit.NANOSECONDS);
		  pool0.scheduleAtFixedRate(consumer, 1, 1, TimeUnit.NANOSECONDS);
		  //ExecutorService pool = Executors.newFixedThreadPool(10);
//		  Thread t1 = new Thread(new Producer());
//		  Thread t2 = new Thread(new Producer());
//		  Thread t3 = new Thread(new Producer());
//		  Thread t4 = new Thread(new Producer());
//		  Thread t5 = new Thread(new Producer());
//		 		  
//		  Thread t6 = new Thread(new Consumer());
//		  Thread t7 = new Thread(new Consumer());
//		  Thread t8 = new Thread(new Consumer());
//		  Thread t9 = new Thread(new Consumer());
//		  Thread t10 = new Thread(new Consumer());
//		  pool.execute(t1);
//		  pool.execute(t2);
//		  pool.execute(t3);
//		  pool.execute(t4);
//		  pool.execute(t5);
//		  
//		  pool.execute(t6);
//		  pool.execute(t7);
//		  pool.execute(t8);
//		  pool.execute(t9);
//		  pool.execute(t10);
		  
	  }
}
//生产者
class Producer implements Runnable{
	
	private BoundedBuffer boundedBuffer;
	public Producer(BoundedBuffer bb){
		this.boundedBuffer = bb;
	}
	public void run(){
		try {
			boundedBuffer.put((int)(Math.random()*100));
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
//消费者
class Consumer implements Runnable{

	private BoundedBuffer boundedBuffer;
	public Consumer(BoundedBuffer bb){
		this.boundedBuffer = bb;
	}
	public void run(){
		try {
			boundedBuffer.take();
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}

