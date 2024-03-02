package com.dori.SpringStory.connection.netty;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.ReadLock;

import com.dori.SpringStory.connection.packet.OutPacket;

public class BroadcastSet<T extends NettyClient> {
    private final Map<Integer, T> clients;
    private final ReentrantReadWriteLock lock;

    public BroadcastSet() {
        this.clients = new HashMap<>();
        // A fair lock is not required as locks will be short-lived
        this.lock = new ReentrantReadWriteLock(false);
    }

    /**
     * Broadcasts the given packet to all clients in the set
     *
     * @param packet
     */
    public void broadcast(OutPacket packet) {
        ReadLock lock = this.lock.readLock();
        try {
            for (T client : clients.values()) {
                client.write((OutPacket) packet.retainedDuplicate());
            }
        } finally {
            packet.release();
            lock.unlock();
        }
    }
    
    /**
     * Broadcasts the given packet to all clients in the set, excluding the client with the given id
     *
     * @param packet
     * @param excludeId
     */
    public void broadcastFilter(OutPacket packet, int excludeId) {
        ReadLock lock = this.lock.readLock();
        try {
            for (Entry<Integer, T> e : clients.entrySet()) {
                if (e.getKey() != excludeId) {
                    e.getValue().write((OutPacket) packet.retainedDuplicate());
                }
            }
        } finally {
            packet.release();
            lock.unlock();
        }
    }

    public void addClient(int id, T client) {
        lock.writeLock().lock();
        try {
            clients.put(id, client);
        } finally {
            lock.writeLock().unlock();
        }
    }

    public void removeClient(int id) {
        lock.writeLock().lock();
        try {
            clients.remove(id);
        } finally {
            lock.writeLock().unlock();
        }
    }
    
}
