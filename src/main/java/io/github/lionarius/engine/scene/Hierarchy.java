package io.github.lionarius.engine.scene;

import java.util.*;

public class Hierarchy<T> implements Iterable<T> {
    private final transient Queue<Action> queuedActions = new ArrayDeque<>();
    private final Map<T, List<T>> children = new LinkedHashMap<>();
    private final transient Map<T, T> parents = new HashMap<>();

    public Hierarchy() {
        this.children.put(null, new ArrayList<>());
    }

    public void processChanges() {
        Action action;
        while ((action = this.queuedActions.poll()) != null)
            action.execute();
    }

    public void add(T object) {
        this.children.put(object, new ArrayList<>());
        this.setParent(object, null);
    }

    public void remove(T object) {
        for (var child : this.getChildren(object))
            this.remove(child);

        this.queuedActions.add(() -> {
            this.parents.remove(object);
            this.children.remove(object);
        });
    }

    public void addChild(T parent, T child) {
        this.setParent(child, parent);
    }

    public void setParent(T child, T parent) {
        if (parent != null && this.getParent(child) == parent)
            return;

        if (this.isInHierarchy(child, parent))
            return;

        this.queuedActions.add(() -> {
            this.children.get(this.getParent(child)).remove(child);

            this.children.get(parent).add(child);
            this.parents.put(child, parent);
        });
    }

    public T getParent(T child) {
        return this.parents.get(child);
    }

    public Iterable<T> getChildren(T parent) {
        return this.children.get(parent);
    }

    public boolean isParent(T object) {
        return !this.children.get(object).isEmpty();
    }

    public boolean isInHierarchy(T ancestor, T child) {
        if (ancestor == null)
            return false;
        if (ancestor == child)
            return true;

        var parent = this.parents.get(child);

        if (parent == null)
            return false;
        if (ancestor == parent)
            return true;

        return this.isInHierarchy(ancestor, parent);
    }

    @Override
    public Iterator<T> iterator() {
        return this.children.keySet().stream().skip(1).iterator();
    }

    @FunctionalInterface
    private interface Action {
        void execute();
    }
}
