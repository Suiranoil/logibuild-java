package io.github.lionarius.engine.util;

import java.util.ArrayDeque;
import java.util.Queue;

public class AddRemoveQueue<T> {
    private final Queue<T> added = new ArrayDeque<>();
    private final Queue<T> removed = new ArrayDeque<>();

    public void add(T object) {
        this.added.add(object);
    }

    public void remove(T object) {
        this.removed.add(object);
    }

    public T pollAdded() {
        return this.added.poll();
    }

    public T pollRemoved() {
        return this.removed.poll();
    }
}
