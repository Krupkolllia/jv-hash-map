package core.basesyntax;

import java.util.Objects;

public class MyHashMap<K, V> implements MyMap<K, V> {
    public static final int DEFAULT_CAPACITY = 1 << 4;
    public static final float DEFAULT_LOAD_FACTOR = 0.75f;
    public static final int DEFAULT_RESIZE_MULTIPLIER = 2;

    private Node<K, V>[] table;
    private int size;
    private int capacity;
    private final float loadFactor;
    private int threshold;

    public MyHashMap(int initialCapacity, float loadFactor) {
        this.capacity = Math.max(initialCapacity, DEFAULT_CAPACITY);
        this.loadFactor = loadFactor;
        this.threshold = calculateThreshold(this.capacity, this.loadFactor);
        this.size = 0;
    }

    public MyHashMap(int initialCapacity) {
        this(initialCapacity, DEFAULT_LOAD_FACTOR);
    }

    public MyHashMap(float loadFactor) {
        this(DEFAULT_CAPACITY, loadFactor);
    }

    public MyHashMap() {
        this(DEFAULT_CAPACITY, DEFAULT_LOAD_FACTOR);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void put(K key, V value) {
        if (table == null) {
            table = (Node<K, V>[]) new Node[capacity];
        }

        if (size >= threshold) {
            resize();
        }

        int keyHash = hash(key);

        Node<K, V> current = table[keyHash];
        while (current != null) {
            if (Objects.equals(key, current.key)) {
                current.value = value;
                return;
            }
            current = current.next;
        }

        Node<K, V> nextNode = table[keyHash];
        int rawHash = (key == null) ? 0 : key.hashCode();
        table[keyHash] = new Node<>(rawHash, key, value, nextNode);
        size++;
    }

    @Override
    public V getValue(K key) {
        if (table == null) {
            return null;
        }

        Node<K, V> node = table[hash(key)];
        while (node != null) {
            if (Objects.equals(node.key, key)) {
                return node.value;
            }
            node = node.next;
        }
        return null;
    }

    @Override
    public int getSize() {
        return size;
    }

    static class Node<K, V> {
        private final int hash;
        private final K key;
        private V value;
        private Node<K, V> next;

        Node(int hash, K key, V value, Node<K,V> next) {
            this.hash = hash;
            this.key = key;
            this.value = value;
            this.next = next;
        }
    }

    private int calculateThreshold(int capacity, float loadFactor) {
        return (int) (capacity * loadFactor);
    }

    private int hash(K key) {
        return (key == null) ? 0 : (key.hashCode() & 0x7FFFFFFF) % capacity;
    }

    @SuppressWarnings("unchecked")
    private void resize() {
        capacity *= DEFAULT_RESIZE_MULTIPLIER;
        threshold = calculateThreshold(capacity, loadFactor);
        Node<K, V>[] newTable = (Node<K, V>[]) new Node[capacity];
        for (Node<K, V> node : table) {
            while (node != null) {
                Node<K, V> next = node.next;
                int index = hash(node.key);
                node.next = newTable[index];
                newTable[index] = node;
                node = next;
            }
        }
        table = newTable;
    }
}
