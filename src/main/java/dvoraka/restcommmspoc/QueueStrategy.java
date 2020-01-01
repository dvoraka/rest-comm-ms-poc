package dvoraka.restcommmspoc;

public interface QueueStrategy<T> {

    void saveItem(T item);

    T loadItem();

    void storeQueue(Iterable<T> items);

    Iterable<T> loadQueue();
}
