package tschuba.util.collection;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class LinkedListMap<K, V> implements Map<K, V> {
    private DoubleLink<Entry<K, V>> lastLink;
    private Set<K> keys;

    public LinkedListMap() {
        this.keys = new HashSet<K>();
    }

    @Override
    public int size() {
        int size = 0;
        if (this.lastLink != null) {
            size = this.lastLink.getIndex() + 1;
        }
        return size;
    }

    @Override
    public boolean isEmpty() {
        return this.lastLink != null;
    }

    @Override
    public boolean containsKey(Object key) {
        return this.keys.contains(key);
    }

    @Override
    public boolean containsValue(Object value) {
        if (this.lastLink != null) {
            DoubleLink<Entry<K, V>> currentLink = lastLink;
            while (currentLink != null) {
                Entry<K, V> currentValue = currentLink.getValue();
                if (currentValue == null && value == null || currentValue != null && value != null && currentValue.equals(value)) {
                    return true;
                }
                currentLink = currentLink.getPredecessor();
            }
        }
        return false;
    }

    @Override
    public V get(Object key) {
        DoubleLink<Entry<K, V>> link = this.findLinkByKey(key);
        if (link != null) {
            return link.getValue().getValue();
        } else {
            return null;
        }
    }

    @Override
    public V put(K key, V value) {
        DoubleLink<Entry<K, V>> currentLinkForKey = this.findLinkByKey(key);
        if (currentLinkForKey != null) {
            Entry<K, V> entry = currentLinkForKey.getValue();
            V oldValue = entry.getValue();
            entry.setValue(value);
            return oldValue;
        } else {
            Entry<K, V> entry = new Entry<K, V>(key, value);
            DoubleLink<Entry<K, V>> link = new DoubleLink<Entry<K, V>>();
            link.setValue(entry);
            if (this.lastLink != null) {
                this.lastLink.setSuccessor(link);
            } else {
                this.lastLink = link;
            }
            this.keys.add(key);
            return null;
        }
    }

    @Override
    public V remove(Object key) {
        DoubleLink<Entry<K, V>> linkByKey = this.findLinkByKey(key);
        if (linkByKey != null) {
            Entry<K, V> entry = linkByKey.getValue();
            linkByKey.remove();
            this.keys.remove(entry.getKey());
            return entry.getValue();
        } else {
            return null;
        }
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> map) {
        if (map == null || map.isEmpty()) {
            return;
        }

        for (Map.Entry<? extends K, ? extends V> entry : map.entrySet()) {
            this.put(entry.getKey(), entry.getValue());
        }
    }

    @Override
    public void clear() {
        DoubleLink<Entry<K, V>> currentLink = this.lastLink;
        while (currentLink != null) {
            DoubleLink<Entry<K, V>> predecessor = currentLink.getPredecessor();
            currentLink.remove();
            currentLink = predecessor;
        }
    }

    @Override
    public Set<K> keySet() {
        return this.keys;
    }

    @Override
    public Collection<V> values() {
        // TODO:
        throw new UnsupportedOperationException();
    }

    @Override
    public Set<Map.Entry<K, V>> entrySet() {
        // TODO:
        throw new UnsupportedOperationException();
    }

    private DoubleLink<Entry<K, V>> findLinkByKey(Object key) {
        if (this.keys.contains(key)) {
            DoubleLink<Entry<K, V>> currentLink = this.lastLink;
            while (currentLink != null) {
                Entry<K, V> currentValue = currentLink.getValue();
                if (currentValue.getKey() == null && key == null || currentValue.getKey() != null && key != null && currentValue.getKey().equals(key)) {
                    return currentLink;
                }
                currentLink = currentLink.getPredecessor();
            }
        }
        return null;
    }

    private class Entry<K, V> implements Map.Entry<K, V> {
        private K key;
        private V value;

        public Entry(K key, V value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public K getKey() {
            return this.key;
        }

        @Override
        public V getValue() {
            return this.value;
        }

        @Override
        public V setValue(V value) {
            V oldValue = this.value;
            this.value = value;
            return oldValue;
        }
    }
}
