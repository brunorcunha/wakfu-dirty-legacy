package com.ankamagames.framework.kernel.core.common.collections.lightweight.intKey;

import java.util.*;
import com.ankamagames.framework.kernel.core.common.collections.iterators.*;

public class IntObjectLightWeightMap<T> extends AbstractIntKeyLightWeightMap implements Iterable<T>
{
    private T[] m_values;
    
    public IntObjectLightWeightMap() {
        this(10);
    }
    
    public IntObjectLightWeightMap(final int initialCapacity) {
        super(initialCapacity);
        this.m_values = (T[]) new Object[initialCapacity];
    }
    
    @Override
    public boolean ensureCapacity(final int newCapacity) {
        final int oldCapacity = this.m_indexes.length;
        if (!super.ensureCapacity(newCapacity)) {
            return false;
        }
        final T[] values = this.m_values;
        System.arraycopy(values, 0, this.m_values = (T[]) new Object[newCapacity], 0, oldCapacity);
        return true;
    }
    
    public void put(final int key, final T value) {
        this.checkCapacity();
        int index = this.insertIndex(key);
        if (index < 0) {
            index = -index - 1;
        }
        else {
            ++this.m_size;
            this.m_indexes[index] = key;
        }
        this.m_values[index] = value;
    }
    
    public T remove(final int key) {
        if (this.m_size == 0) {
            return null;
        }
        final int index = this.index(key);
        if (index < 0) {
            return null;
        }
        final T removed = this.m_values[index];
        if (index < this.m_size - 1) {
            this.m_indexes[index] = this.m_indexes[this.m_size - 1];
            this.m_values[index] = this.m_values[this.m_size - 1];
            this.m_indexes[this.m_size - 1] = 0;
            this.m_values[this.m_size - 1] = null;
        }
        else {
            this.m_indexes[index] = 0;
            this.m_values[index] = null;
        }
        --this.m_size;
        return removed;
    }
    
    @Override
    public void clear() {
        super.clear();
        for (int i = 0, size = this.m_values.length; i < size; ++i) {
            this.m_values[i] = null;
        }
    }
    
    public T get(final int key) {
        final int index = this.index(key);
        if (index < 0) {
            return null;
        }
        return this.m_values[index];
    }
    
    public T getQuickValue(final int index) {
        return this.m_values[index];
    }
    
    @Override
    public Iterator<T> iterator() {
        return new ArrayIterator<T>(this.m_values, false);
    }
    
    @Override
    public void compact() {
        super.compact();
        final T[] values = this.m_values;
        System.arraycopy(values, 0, this.m_values = (T[]) new Object[this.m_size], 0, this.m_size);
    }
}
