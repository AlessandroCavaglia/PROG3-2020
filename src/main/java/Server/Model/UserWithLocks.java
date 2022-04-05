package Server.Model;

import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.ReadLock;

/*
    This particular instance of User represent the user with it's Locks
    It is used in the system to manage concurrency on the user's mail box
    UserWithLocks and its referenced objects occupy 256-512 Bytes (Each lock occupies 80 Bytes)
*/
public class UserWithLocks extends User {

    private final ReadLock readLock;
    private final WriteLock writeLock;

    public UserWithLocks(String mail, String path) {
        super(mail, path);
        ReentrantReadWriteLock RWLock = new ReentrantReadWriteLock();
        this.readLock = RWLock.readLock();
        this.writeLock = RWLock.writeLock();
    }

    public void lockRead() { readLock.lock(); }

    public void lockWrite() { writeLock.lock(); }

    public void releaseRead() { readLock.unlock(); }

    public void releaseWrite() { writeLock.unlock(); }

    @Override
    public String toString() {
        return "UserWithLocks{" + super.toString() + ", locks}";
    }
}
