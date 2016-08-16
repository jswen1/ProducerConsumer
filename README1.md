# ProducerConsumer
生产者消费者模式java实现，其中用到了可重入锁ReentrantLock来实现有界缓冲区的互斥访问，Executors类的静态方法newScheduledThreadPool()产生
线程池，管理执行生产和消费的线程。
生产者与消费者规则：
* 1 同一时间内只能有一个生产者生产     生产方法加锁 
* 2 同一时间内只能有一个消费者消费     消费方法加锁 
* 3 生产者生产的同时消费者不能消费     生产方法加锁 
* 4 消费者消费的同时生产者不能生产     消费方法加锁 
* 5 共享空间空时消费者不能继续消费     消费前循环判断是否为空，空的话将该线程wait，释放锁允许其他同步方法执行 
* 6 共享空间满时生产者不能继续生产     生产前循环判断是否为满，满的话将该线程wait，释放锁允许其他同步方法执行    