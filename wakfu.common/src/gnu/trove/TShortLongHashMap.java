package gnu.trove;

import java.util.*;
import java.lang.reflect.*;
import java.io.*;

public class TShortLongHashMap extends TShortHash implements Externalizable
{
    static final long serialVersionUID = 1L;
    private final TShortLongProcedure PUT_ALL_PROC;
    protected transient long[] _values;
    
    public TShortLongHashMap() {
        super();
        this.PUT_ALL_PROC = new TShortLongProcedure() {
            @Override
			public boolean execute(final short key, final long value) {
                TShortLongHashMap.this.put(key, value);
                return true;
            }
        };
    }
    
    public TShortLongHashMap(final int initialCapacity) {
        super(initialCapacity);
        this.PUT_ALL_PROC = new TShortLongProcedure() {
            @Override
			public boolean execute(final short key, final long value) {
                TShortLongHashMap.this.put(key, value);
                return true;
            }
        };
    }
    
    public TShortLongHashMap(final int initialCapacity, final float loadFactor) {
        super(initialCapacity, loadFactor);
        this.PUT_ALL_PROC = new TShortLongProcedure() {
            @Override
			public boolean execute(final short key, final long value) {
                TShortLongHashMap.this.put(key, value);
                return true;
            }
        };
    }
    
    public TShortLongHashMap(final TShortHashingStrategy strategy) {
        super(strategy);
        this.PUT_ALL_PROC = new TShortLongProcedure() {
            @Override
			public boolean execute(final short key, final long value) {
                TShortLongHashMap.this.put(key, value);
                return true;
            }
        };
    }
    
    public TShortLongHashMap(final int initialCapacity, final TShortHashingStrategy strategy) {
        super(initialCapacity, strategy);
        this.PUT_ALL_PROC = new TShortLongProcedure() {
            @Override
			public boolean execute(final short key, final long value) {
                TShortLongHashMap.this.put(key, value);
                return true;
            }
        };
    }
    
    public TShortLongHashMap(final int initialCapacity, final float loadFactor, final TShortHashingStrategy strategy) {
        super(initialCapacity, loadFactor, strategy);
        this.PUT_ALL_PROC = new TShortLongProcedure() {
            @Override
			public boolean execute(final short key, final long value) {
                TShortLongHashMap.this.put(key, value);
                return true;
            }
        };
    }
    
    @Override
	public Object clone() {
        final TShortLongHashMap m = (TShortLongHashMap)super.clone();
        m._values = this._values.clone();
        return m;
    }
    
    public TShortLongIterator iterator() {
        return new TShortLongIterator(this);
    }
    
    @Override
	protected int setUp(final int initialCapacity) {
        final int capacity = super.setUp(initialCapacity);
        this._values = new long[capacity];
        return capacity;
    }
    
    public long put(final short key, final long value) {
        final int index = this.insertionIndex(key);
        return this.doPut(key, value, index);
    }
    
    public long putIfAbsent(final short key, final long value) {
        final int index = this.insertionIndex(key);
        if (index < 0) {
            return this._values[-index - 1];
        }
        return this.doPut(key, value, index);
    }
    
    private long doPut(final short key, final long value, int index) {
        long previous = 0L;
        boolean isNewMapping = true;
        if (index < 0) {
            index = -index - 1;
            previous = this._values[index];
            isNewMapping = false;
        }
        final byte previousState = this._states[index];
        this._set[index] = key;
        this._states[index] = 1;
        this._values[index] = value;
        if (isNewMapping) {
            this.postInsertHook(previousState == 0);
        }
        return previous;
    }
    
    public void putAll(final TShortLongHashMap map) {
        map.forEachEntry(this.PUT_ALL_PROC);
    }
    
    @Override
	protected void rehash(final int newCapacity) {
        final int oldCapacity = this._set.length;
        final short[] oldKeys = this._set;
        final long[] oldVals = this._values;
        final byte[] oldStates = this._states;
        this._set = new short[newCapacity];
        this._values = new long[newCapacity];
        this._states = new byte[newCapacity];
        int i = oldCapacity;
        while (i-- > 0) {
            if (oldStates[i] == 1) {
                final short o = oldKeys[i];
                final int index = this.insertionIndex(o);
                this._set[index] = o;
                this._values[index] = oldVals[i];
                this._states[index] = 1;
            }
        }
    }
    
    public long get(final short key) {
        final int index = this.index(key);
        return (index < 0) ? 0L : this._values[index];
    }
    
    @Override
	public void clear() {
        super.clear();
        final short[] keys = this._set;
        final long[] vals = this._values;
        final byte[] states = this._states;
        Arrays.fill(this._set, 0, this._set.length, (short)0);
        Arrays.fill(this._values, 0, this._values.length, 0L);
        Arrays.fill(this._states, 0, this._states.length, (byte)0);
    }
    
    public long remove(final short key) {
        long prev = 0L;
        final int index = this.index(key);
        if (index >= 0) {
            prev = this._values[index];
            this.removeAt(index);
        }
        return prev;
    }
    
    @Override
	public boolean equals(final Object other) {
        if (!(other instanceof TShortLongHashMap)) {
            return false;
        }
        final TShortLongHashMap that = (TShortLongHashMap)other;
        return that.size() == this.size() && this.forEachEntry(new EqProcedure(that));
    }
    
    @Override
	public int hashCode() {
        final HashProcedure p = new HashProcedure();
        this.forEachEntry(p);
        return p.getHashCode();
    }
    
    @Override
	protected void removeAt(final int index) {
        this._values[index] = 0L;
        super.removeAt(index);
    }
    
    public long[] getValues() {
        final long[] vals = new long[this.size()];
        final long[] v = this._values;
        final byte[] states = this._states;
        int i = v.length;
        int j = 0;
        while (i-- > 0) {
            if (states[i] == 1) {
                vals[j++] = v[i];
            }
        }
        return vals;
    }
    
    public short[] keys() {
        final short[] keys = new short[this.size()];
        final short[] k = this._set;
        final byte[] states = this._states;
        int i = k.length;
        int j = 0;
        while (i-- > 0) {
            if (states[i] == 1) {
                keys[j++] = k[i];
            }
        }
        return keys;
    }
    
    public short[] keys(short[] a) {
        final int size = this.size();
        if (a.length < size) {
            a = (short[])Array.newInstance(a.getClass().getComponentType(), size);
        }
        final short[] k = this._set;
        final byte[] states = this._states;
        int i = k.length;
        int j = 0;
        while (i-- > 0) {
            if (states[i] == 1) {
                a[j++] = k[i];
            }
        }
        return a;
    }
    
    public boolean containsValue(final long val) {
        final byte[] states = this._states;
        final long[] vals = this._values;
        int i = vals.length;
        while (i-- > 0) {
            if (states[i] == 1 && val == vals[i]) {
                return true;
            }
        }
        return false;
    }
    
    public boolean containsKey(final short key) {
        return this.contains(key);
    }
    
    public boolean forEachKey(final TShortProcedure procedure) {
        return this.forEach(procedure);
    }
    
    public boolean forEachValue(final TLongProcedure procedure) {
        final byte[] states = this._states;
        final long[] values = this._values;
        int i = values.length;
        while (i-- > 0) {
            if (states[i] == 1 && !procedure.execute(values[i])) {
                return false;
            }
        }
        return true;
    }
    
    public boolean forEachEntry(final TShortLongProcedure procedure) {
        final byte[] states = this._states;
        final short[] keys = this._set;
        final long[] values = this._values;
        int i = keys.length;
        while (i-- > 0) {
            if (states[i] == 1 && !procedure.execute(keys[i], values[i])) {
                return false;
            }
        }
        return true;
    }
    
    public boolean retainEntries(final TShortLongProcedure procedure) {
        boolean modified = false;
        final byte[] states = this._states;
        final short[] keys = this._set;
        final long[] values = this._values;
        this.tempDisableAutoCompaction();
        try {
            int i = keys.length;
            while (i-- > 0) {
                if (states[i] == 1 && !procedure.execute(keys[i], values[i])) {
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
    
    public void transformValues(final TLongFunction function) {
        final byte[] states = this._states;
        final long[] values = this._values;
        int i = values.length;
        while (i-- > 0) {
            if (states[i] == 1) {
                values[i] = function.execute(values[i]);
            }
        }
    }
    
    public boolean increment(final short key) {
        return this.adjustValue(key, 1L);
    }
    
    public boolean adjustValue(final short key, final long amount) {
        final int index = this.index(key);
        if (index < 0) {
            return false;
        }
        final long[] values = this._values;
        final int n = index;
        values[n] += amount;
        return true;
    }
    
    public long adjustOrPutValue(final short key, final long adjust_amount, final long put_amount) {
        int index = this.insertionIndex(key);
        long newValue;
        boolean isNewMapping;
        if (index < 0) {
            index = -index - 1;
            final long[] values = this._values;
            final int n = index;
            final long n2 = values[n] + adjust_amount;
            values[n] = n2;
            newValue = n2;
            isNewMapping = false;
        }
        else {
            this._values[index] = put_amount;
            newValue = put_amount;
            isNewMapping = true;
        }
        final byte previousState = this._states[index];
        this._set[index] = key;
        this._states[index] = 1;
        if (isNewMapping) {
            this.postInsertHook(previousState == 0);
        }
        return newValue;
    }
    
    @Override
	public void writeExternal(final ObjectOutput out) throws IOException {
        out.writeByte(0);
        out.writeInt(this._size);
        final SerializationProcedure writeProcedure = new SerializationProcedure(out);
        if (!this.forEachEntry(writeProcedure)) {
            throw writeProcedure.exception;
        }
    }
    
    @Override
	public void readExternal(final ObjectInput in) throws IOException, ClassNotFoundException {
        in.readByte();
        int size = in.readInt();
        this.setUp(size);
        while (size-- > 0) {
            final short key = in.readShort();
            final long val = in.readLong();
            this.put(key, val);
        }
    }
    
    @Override
	public String toString() {
        final StringBuilder buf = new StringBuilder("{");
        this.forEachEntry(new TShortLongProcedure() {
            private boolean first = true;
            
            @Override
			public boolean execute(final short key, final long value) {
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
    
    private final class HashProcedure implements TShortLongProcedure
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
		public final boolean execute(final short key, final long value) {
            this.h += (TShortLongHashMap.this._hashingStrategy.computeHashCode(key) ^ HashFunctions.hash(value));
            return true;
        }
    }
    
    private static final class EqProcedure implements TShortLongProcedure
    {
        private final TShortLongHashMap _otherMap;
        
        EqProcedure(final TShortLongHashMap otherMap) {
            super();
            this._otherMap = otherMap;
        }
        
        @Override
		public final boolean execute(final short key, final long value) {
            final int index = this._otherMap.index(key);
            return index >= 0 && this.eq(value, this._otherMap.get(key));
        }
        
        private final boolean eq(final long v1, final long v2) {
            return v1 == v2;
        }
    }
}
