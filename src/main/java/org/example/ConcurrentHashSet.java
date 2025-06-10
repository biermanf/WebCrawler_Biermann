package org.example;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class ConcurrentHashSet<E> {
    private final Set<E> set = Collections.newSetFromMap(new ConcurrentHashMap<>());

    public boolean add(E element) {
        return set.add(element);
    }

    public boolean contains(E element) {
        return set.contains(element);
    }

    public Set<E> getSet() {
        return set;
    }
}