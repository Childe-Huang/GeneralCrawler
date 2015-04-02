package fetcher;

import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by Bin on 2015/3/2.
 */
public class FetchQueue extends ConcurrentLinkedQueue<FetchQueueItem> {
    /**
     * ConcurrentLinkedQueue 是一个无界的线程安全队列（jdk1.6及以上），根据FIFO给元素排序，元素不允许为null
     *
     * 方法摘要：
     * boolean add(E e) 将指定元素插入队尾
     * boolean contains(Object o) 若队列中包含指定元素，返回true
     * boolean isEmpty() 若队列为空，返回true
     * Iterator<E> iterator() 返回在此队列元素上以恰当顺序进行迭代的迭代器
     * boolean offer(E e) 将指定元素插入队尾
     * E peek() 获取但不移除队头，若队列为空，则返回null
     * E poll() 获取并移除队头，若队列为空，则返回null
     * boolean remove(Object o) 从队列中移除指定元素的单个实例（如果存在）
     * int size() 返回此队列中的元素数量
     * Object[] toArray 返回以恰当顺序包含此队列所有元素的数组
     * <T> T[] toArray(T[] a) 返回以恰当顺序包含此队列所有元素的数组，返回数组的运行时类型是指定数组的运行时类型
     *
     * FetchQueue作为任务队列（待抓取队列）类型
     * 设计模式：单例
     */
    private FetchQueue(){}
    private static FetchQueue fetchQueue = null;
    public static FetchQueue getFetchQueue() {
        if (fetchQueue == null) {
            fetchQueue = new FetchQueue();
        }
        return fetchQueue;
    }
    public static void releaseResource() {
        if (fetchQueue != null) {
            fetchQueue = null;
        }
    }

}
