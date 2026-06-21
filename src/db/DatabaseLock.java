package db;

import java.util.concurrent.locks.ReentrantLock;

public final class DatabaseLock {

    public static final ReentrantLock WRITE_LOCK = new ReentrantLock();

    private DatabaseLock() {
    }

    public static void write(Runnable action) {
        WRITE_LOCK.lock();
        try {
            action.run();
        } finally {
            WRITE_LOCK.unlock();
        }
    }
    public static <T> T write(java.util.function.Supplier<T> action) {
        WRITE_LOCK.lock();
        try {
            return action.get();
        } finally {
            WRITE_LOCK.unlock();
        }
    }
}
