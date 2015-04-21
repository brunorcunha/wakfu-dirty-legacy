package gnu.trove;

import java.io.*;
import java.lang.reflect.*;
import java.util.*;

public class THashMap<K, V> extends TObjectHash<K> implements Map<K, V>, Externalizable
{
    static final long serialVersionUID = 1L;
    protected transient V[] _values;
    
    public THashMap() {
        super();
    }
    
    public THashMap(final TObjectHashingStrategy<K> strategy) {
        super(strategy);
    }
    
    public THashMap(final int initialCapacity) {
        super(initialCapacity);
    }
    
    public THashMap(final int initialCapacity, final TObjectHashingStrategy<K> strategy) {
        super(initialCapacity, strategy);
    }
    
    public THashMap(final int initialCapacity, final float loadFactor) {
        super(initialCapacity, loadFactor);
    }
    
    public THashMap(final int initialCapacity, final float loadFactor, final TObjectHashingStrategy<K> strategy) {
        super(initialCapacity, loadFactor, strategy);
    }
    
    public THashMap(final Map<K, V> map) {
        this(map.size());
        this.putAll(map);
    }
    
    public THashMap(final Map<K, V> map, final TObjectHashingStrategy<K> strategy) {
        this(map.size(), strategy);
        this.putAll(map);
    }
    
    @Override
	public THashMap<K, V> clone() {
        final THashMap<K, V> m = (THashMap)super.clone();
        m._values = this._values.clone();
        return m;
    }
    
    @Override
	protected int setUp(final int initialCapacity) {
        final int capacity = super.setUp(initialCapacity);
        this._values = (V[]) new Object[capacity];
        return capacity;
    }
    
    @Override
	public V put(final K key, final V value) {
        final int index = this.insertionIndex(key);
        return this.doPut(key, value, index);
    }
    
    @Override
	public V putIfAbsent(final K key, final V value) {
        final int index = this.insertionIndex(key);
        if (index < 0) {
            return this._values[-index - 1];
        }
        return this.doPut(key, value, index);
    }
    
    private V doPut(final K key, final V value, int index) {
        V previous = null;
        boolean isNewMapping = true;
        if (index < 0) {
            index = -index - 1;
            previous = this._values[index];
            isNewMapping = false;
        }
        final Object oldKey = this._set[index];
        this._set[index] = key;
        this._values[index] = value;
        if (isNewMapping) {
            this.postInsertHook(oldKey == TObjectHash.FREE);
        }
        return previous;
    }
    
    @Override
	public boolean equals(final Object other) {
        if (!(other instanceof Map)) {
            return false;
        }
        final Map<K, V> that = (Map<K, V>)other;
        return that.size() == this.size() && this.forEachEntry(new EqProcedure<K, V>(that));
    }
    
    @Override
	public int hashCode() {
        final HashProcedure p = new HashProcedure();
        this.forEachEntry(p);
        return p.getHashCode();
    }
    
    @Override
	public String toString() {
        final StringBuilder buf = new StringBuilder("{");
        this.forEachEntry(new TObjectObjectProcedure<K, V>() {
            private boolean first = true;
            
            @Override
			public boolean execute(final K key, final V value) {
                if (this.first) {
                    this.first = false;
                }
                else {
                    buf.append(",");
                }
                buf.append(key);
                buf.append("=");
                buf.append(value);
                return true;
            }
        });
        buf.append("}");
        return buf.toString();
    }
    
    public boolean forEachKey(final TObjectProcedure<K> procedure) {
        return this.forEach(procedure);
    }
    
    public boolean forEachValue(final TObjectProcedure<V> procedure) {
        final V[] values = this._values;
        final Object[] set = this._set;
        int i = values.length;
        while (i-- > 0) {
            if (set[i] != TObjectHash.FREE && set[i] != TObjectHash.REMOVED && !procedure.execute(values[i])) {
                return false;
            }
        }
        return true;
    }
    
    public boolean forEachEntry(final TObjectObjectProcedure<K, V> procedure) {
        final Object[] keys = this._set;
        final V[] values = this._values;
        int i = keys.length;
        while (i-- > 0) {
            if (keys[i] != TObjectHash.FREE && keys[i] != TObjectHash.REMOVED && !procedure.execute((K)keys[i], values[i])) {
                return false;
            }
        }
        return true;
    }
    
    public boolean retainEntries(final TObjectObjectProcedure<K, V> procedure) {
        boolean modified = false;
        final Object[] keys = this._set;
        final V[] values = this._values;
        this.tempDisableAutoCompaction();
        try {
            int i = keys.length;
            while (i-- > 0) {
                if (keys[i] != TObjectHash.FREE && keys[i] != TObjectHash.REMOVED && !procedure.execute((K)keys[i], values[i])) {
                    this.removeAt(i);
                    modified = true;
                }
            }
        }
        finally {
            this.reenableAutoCompaction(true);
        }
        return modified;
    }
    
    public void transformValues(final TObjectFunction<V, V> function) {
        final V[] values = this._values;
        final Object[] set = this._set;
        int i = values.length;
        while (i-- > 0) {
            if (set[i] != TObjectHash.FREE && set[i] != TObjectHash.REMOVED) {
                values[i] = function.execute(values[i]);
            }
        }
    }
    
    @Override
	protected void rehash(final int newCapacity) {
        final int oldCapacity = this._set.length;
        final Object[] oldKeys = this._set;
        final V[] oldVals = this._values;
        Arrays.fill(this._set = new Object[newCapacity], TObjectHash.FREE);
        this._values = (V[]) new Object[newCapacity];
        int i = oldCapacity;
        while (i-- > 0) {
            if (oldKeys[i] != TObjectHash.FREE && oldKeys[i] != TObjectHash.REMOVED) {
                final Object o = oldKeys[i];
                final int index = this.insertionIndex((K)o);
                if (index < 0) {
                    this.throwObjectContractViolation(this._set[-index - 1], o);
                }
                this._set[index] = o;
                this._values[index] = oldVals[i];
            }
        }
    }
    
    @Override
	public V get(final Object key) {
        final int index = this.index((K)key);
        return (index < 0) ? null : this._values[index];
    }
    
    @Override
	public void clear() {
        if (this.size() == 0) {
            return;
        }
        super.clear();
        Arrays.fill(this._set, 0, this._set.length, TObjectHash.FREE);
        Arrays.fill(this._values, 0, this._values.length, null);
    }
    
    @Override
	public V remove(final Object key) {
        V prev = null;
        final int index = this.index((K)key);
        if (index >= 0) {
            prev = this._values[index];
            this.removeAt(index);
        }
        return prev;
    }
    
    @Override
	protected void removeAt(final int index) {
        this._values[index] = null;
        super.removeAt(index);
    }
    
    @Override
	public Collection<V> values() {
        return new ValueView();
    }
    
    @Override
	public Set<K> keySet() {
        return new KeyView();
    }
    
    @Override
	public Set<Map.Entry<K, V>> entrySet() {
        return new EntryView();
    }
    
    @Override
	public boolean containsValue(final Object val) {
        final Object[] set = this._set;
        final V[] vals = this._values;
        if (null == val) {
            int i = vals.length;
            while (i-- > 0) {
                if (set[i] != TObjectHash.FREE && set[i] != TObjectHash.REMOVED && val == vals[i]) {
                    return true;
                }
            }
        }
        else {
            int i = vals.length;
            while (i-- > 0) {
                if (set[i] != TObjectHash.FREE && set[i] != TObjectHash.REMOVED && (val == vals[i] || val.equals(vals[i]))) {
                    return true;
                }
            }
        }
        return false;
    }
    
    @Override
	public boolean containsKey(final Object key) {
        return this.contains(key);
    }
    
    @Override
	public void putAll(final Map<? extends K, ? extends V> map) {
        this.ensureCapacity(map.size());
        for (final Map.Entry<? extends K, ? extends V> e : map.entrySet()) {
            this.put(e.getKey(), e.getValue());
        }
    }
    
    @Override
	public void writeExternal(final ObjectOutput out) throws IOException {
        out.writeByte(1);
        super.writeExternal(out);
        out.writeInt(this._size);
        final SerializationProcedure writeProcedure = new SerializationProcedure(out);
        if (!this.forEachEntry(writeProcedure)) {
            throw writeProcedure.exception;
        }
    }
    
    @Override
	public void readExternal(final ObjectInput in) throws IOException, ClassNotFoundException {
        final byte version = in.readByte();
        if (version != 0) {
            super.readExternal(in);
        }
        int size = in.readInt();
        this.setUp(size);
        while (size-- > 0) {
            final K key = (K)in.readObject();
            final V val = (V)in.readObject();
            this.put(key, val);
        }
    }
    
    private final class HashProcedure implements TObjectObjectProcedure<K, V>
    {
        private int h;
        
        private HashProcedure() {
            super();
            this.h = 0;
        }
        
        public int getHashCode() {
            return this.h;
        }
        
        @Override
		public final boolean execute(final K key, final V value) {
            this.h += (THashMap.this._hashingStrategy.computeHashCode(key) ^ ((value == null) ? 0 : value.hashCode()));
            return true;
        }
    }
    
    private static final class EqProcedure<K, V> implements TObjectObjectProcedure<K, V>
    {
        private final Map<K, V> _otherMap;
        
        EqProcedure(final Map<K, V> otherMap) {
            super();
            this._otherMap = otherMap;
        }
        
        @Override
		public final boolean execute(final K key, final V value) {
            if (value == null && !this._otherMap.containsKey(key)) {
                return false;
            }
            final V oValue = this._otherMap.get(key);
            return oValue == value || (oValue != null && oValue.equals(value));
        }
    }
    
    protected class ValueView extends MapBackedView<V>
    {
        @Override
		public Iterator<V> iterator() {
            return new THashIterator<V>(THashMap.this) {
                @Override
				protected V objectAtIndex(final int index) {
                    return THashMap.this._values[index];
                }
            };
        }
        
        @Override
		public boolean containsElement(final V value) {
            return THashMap.this.containsValue(value);
        }
        
        @Override
		public boolean removeElement(final V value) {
            final Object[] values = THashMap.this._values;
            final Object[] set = THashMap.this._set;
            int i = values.length;
            while (i-- > 0) {
                if ((set[i] != TObjectHash.FREE && set[i] != TObjectHash.REMOVED && value == values[i]) || (null != values[i] && values[i].equals(value))) {
                    THashMap.this.removeAt(i);
                    return true;
                }
            }
            return false;
        }
    }
    
    protected class EntryView extends MapBackedView<Map.Entry<K, V>>
    {
        @Override
		public Iterator<Map.Entry<K, V>> iterator() {
            return new EntryIterator(THashMap.this);
        }
        
        @Override
		public boolean removeElement(final Map.Entry<K, V> entry) {
            final K key = this.keyForEntry(entry);
            final int index = THashMap.this.index(key);
            if (index >= 0) {
                final Object val = this.valueForEntry(entry);
                if (val == THashMap.this._values[index] || (null != val && val.equals(THashMap.this._values[index]))) {
                    THashMap.this.removeAt(index);
                    return true;
                }
            }
            return false;
        }
        
        @Override
		public boolean containsElement(final Map.Entry<K, V> entry) {
            final Object val = THashMap.this.get(this.keyForEntry(entry));
            final Object entryValue = entry.getValue();
            return entryValue == val || (null != val && val.equals(entryValue));
        }
        
        protected V valueForEntry(final Map.Entry<K, V> entry) {
            return entry.getValue();
        }
        
        protected K keyForEntry(final Map.Entry<K, V> entry) {
            return entry.getKey();
        }
        
        private final class EntryIterator extends THashIterator<Map.Entry<K, V>>
        {
            EntryIterator(final THashMap<K, V> map) {
                super(map);
            }
            
            @Override
			public Entry objectAtIndex(final int index) {
                return new Entry((K)THashMap.this._set[index], THashMap.this._values[index], index);
            }
        }
    }
    
    private abstract class MapBackedView<E> extends AbstractSet<E> implements Set<E>, Iterable<E>
    {
        @Override
		public abstract Iterator<E> iterator();
        
        public abstract boolean removeElement(final E p0);
        
        public abstract boolean containsElement(final E p0);
        
        @Override
		public boolean contains(final Object key) {
            return this.containsElement((E) key);
        }
        
        @Override
		public boolean remove(final Object o) {
            return this.removeElement((E) o);
        }
        
        @Override
		public boolean containsAll(final Collection<?> collection) {
            final Iterator i = collection.iterator();
            while (i.hasNext()) {
                if (!this.contains(i.next())) {
                    return false;
                }
            }
            return true;
        }
        
        @Override
		public void clear() {
            THashMap.this.clear();
        }
        
        @Override
		public boolean add(final E obj) {
            throw new UnsupportedOperationException();
        }
        
        @Override
		public int size() {
            return THashMap.this.size();
        }
        
        @Override
		public Object[] toArray() {
            final Object[] result = new Object[this.size()];
            final Iterator e = this.iterator();
            int i = 0;
            while (e.hasNext()) {
                result[i] = e.next();
                ++i;
            }
            return result;
        }
        
        @Override
		public <T> T[] toArray(T[] a) {
            final int size = this.size();
            if (a.length < size) {
                a = (T[])Array.newInstance(a.getClass().getComponentType(), size);
            }
            final Iterator<E> it = this.iterator();
            final Object[] result = a;
            for (int i = 0; i < size; ++i) {
                result[i] = it.next();
            }
            if (a.length > size) {
                a[size] = null;
            }
            return a;
        }
        
        @Override
		public boolean isEmpty() {
            return THashMap.this.isEmpty();
        }
        
        @Override
		public boolean addAll(final Collection<? extends E> collection) {
            throw new UnsupportedOperationException();
        }
        
        @Override
		public boolean retainAll(final Collection<?> collection) {
            boolean changed = false;
            final Iterator i = this.iterator();
            while (i.hasNext()) {
                if (!collection.contains(i.next())) {
                    i.remove();
                    changed = true;
                }
            }
            return changed;
        }
    }
    
    protected class KeyView extends MapBackedView<K>
    {
        @Override
		public Iterator<K> iterator() {
            return new TObjectHashIterator<K>(THashMap.this);
        }
        
        @Override
		public boolean removeElement(final K key) {
            return null != THashMap.this.remove(key);
        }
        
        @Override
		public boolean containsElement(final K key) {
            return THashMap.this.contains(key);
        }
    }
    
    final class Entry implements Map.Entry<K, V>
    {
        private K key;
        private V val;
        private final int index;
        
        Entry(final K key, final V value, final int index) {
            super();
            this.key = key;
            this.val = value;
            this.index = index;
        }
        
        void setKey(final K aKey) {
            this.key = aKey;
        }
        
        void setValue0(final V aValue) {
            this.val = aValue;
        }
        
        @Override
		public K getKey() {
            return this.key;
        }
        
        @Override
		public V getValue() {
            return this.val;
        }
        
        @Override
		public V setValue(V o) {
            if (THashMap.this._values[this.index] != this.val) {
                throw new ConcurrentModificationException();
            }
            THashMap.this._values[this.index] = o;
            o = this.val;
            return this.val = o;
        }
        
        @Override
		public boolean equals(final Object o) {
            return false;
        }
        
        @Override
		public int hashCode() {
            return ((this.getKey() == null) ? 0 : this.getKey().hashCode()) ^ ((this.getValue() == null) ? 0 : this.getValue().hashCode());
        }
    }
}
