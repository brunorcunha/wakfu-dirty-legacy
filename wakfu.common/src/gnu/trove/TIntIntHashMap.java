package gnu.trove;

import java.util.*;
import java.lang.reflect.*;
import java.io.*;

public class TIntIntHashMap extends TIntHash implements Externalizable
{
    static final long serialVersionUID = 1L;
    private final TIntIntProcedure PUT_ALL_PROC;
    protected transient int[] _values;
    
    public TIntIntHashMap() {
        super();
        this.PUT_ALL_PROC = new TIntIntProcedure() {
            @Override
			public boolean execute(final int key, final int value) {
                TIntIntHashMap.this.put(key, value);
                return true;
            }
        };
    }
    
    public TIntIntHashMap(final int initialCapacity) {
        super(initialCapacity);
        this.PUT_ALL_PROC = new TIntIntProcedure() {
            @Override
			public boolean execute(final int key, final int value) {
                TIntIntHashMap.this.put(key, value);
                return true;
            }
        };
    }
    
    public TIntIntHashMap(final int initialCapacity, final float loadFactor) {
        super(initialCapacity, loadFactor);
        this.PUT_ALL_PROC = new TIntIntProcedure() {
            @Override
			public boolean execute(final int key, final int value) {
                TIntIntHashMap.this.put(key, value);
                return true;
            }
        };
    }
    
    public TIntIntHashMap(final TIntHashingStrategy strategy) {
        super(strategy);
        this.PUT_ALL_PROC = new TIntIntProcedure() {
            @Override
			public boolean execute(final int key, final int value) {
                TIntIntHashMap.this.put(key, value);
                return true;
            }
        };
    }
    
    public TIntIntHashMap(final int initialCapacity, final TIntHashingStrategy strategy) {
        super(initialCapacity, strategy);
        this.PUT_ALL_PROC = new TIntIntProcedure() {
            @Override
			public boolean execute(final int key, final int value) {
                TIntIntHashMap.this.put(key, value);
                return true;
            }
        };
    }
    
    public TIntIntHashMap(final int initialCapacity, final float loadFactor, final TIntHashingStrategy strategy) {
        super(initialCapacity, loadFactor, strategy);
        this.PUT_ALL_PROC = new TIntIntProcedure() {
            @Override
			public boolean execute(final int key, final int value) {
                TIntIntHashMap.this.put(key, value);
                return true;
            }
        };
    }
    
    @Override
	public Object clone() {
        final TIntIntHashMap m = (TIntIntHashMap)super.clone();
        m._values = this._values.clone();
        return m;
    }
    
    public TIntIntIterator iterator() {
        return new TIntIntIterator(this);
    }
    
    @Override
	protected int setUp(final int initialCapacity) {
        final int capacity = super.setUp(initialCapacity);
        this._values = new int[capacity];
        return capacity;
    }
    
    public int put(final int key, final int value) {
        final int index = this.insertionIndex(key);
        return this.doPut(key, value, index);
    }
    
    public int putIfAbsent(final int key, final int value) {
        final int index = this.insertionIndex(key);
        if (index < 0) {
            return this._values[-index - 1];
        }
        return this.doPut(key, value, index);
    }
    
    private int doPut(final int key, final int value, int index) {
        int previous = 0;
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
    
    public void putAll(final TIntIntHashMap map) {
        map.forEachEntry(this.PUT_ALL_PROC);
    }
    
    @Override
	protected void rehash(final int newCapacity) {
        final int oldCapacity = this._set.length;
        final int[] oldKeys = this._set;
        final int[] oldVals = this._values;
        final byte[] oldStates = this._states;
        this._set = new int[newCapacity];
        this._values = new int[newCapacity];
        this._states = new byte[newCapacity];
        int i = oldCapacity;
        while (i-- > 0) {
            if (oldStates[i] == 1) {
                final int o = oldKeys[i];
                final int index = this.insertionIndex(o);
                this._set[index] = o;
                this._values[index] = oldVals[i];
                this._states[index] = 1;
            }
        }
    }
    
    public int get(final int key) {
        final int index = this.index(key);
        return (index < 0) ? 0 : this._values[index];
    }
    
    @Override
	public void clear() {
        super.clear();
        final int[] keys = this._set;
        final int[] vals = this._values;
        final byte[] states = this._states;
        Arrays.fill(this._set, 0, this._set.length, 0);
        Arrays.fill(this._values, 0, this._values.length, 0);
        Arrays.fill(this._states, 0, this._states.length, (byte)0);
    }
    
    public int remove(final int key) {
        int prev = 0;
        final int index = this.index(key);
        if (index >= 0) {
            prev = this._values[index];
            this.removeAt(index);
        }
        return prev;
    }
    
    @Override
	public boolean equals(final Object other) {
        if (!(other instanceof TIntIntHashMap)) {
            return false;
        }
        final TIntIntHashMap that = (TIntIntHashMap)other;
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
        this._values[index] = 0;
        super.removeAt(index);
    }
    
    public int[] getValues() {
        final int[] vals = new int[this.size()];
        final int[] v = this._values;
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
    
    public int[] keys() {
        final int[] keys = new int[this.size()];
        final int[] k = this._set;
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
    
    public int[] keys(int[] a) {
        final int size = this.size();
        if (a.length < size) {
            a = (int[])Array.newInstance(a.getClass().getComponentType(), size);
        }
        final int[] k = this._set;
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
    
    public boolean containsValue(final int val) {
        final byte[] states = this._states;
        final int[] vals = this._values;
        int i = vals.length;
        while (i-- > 0) {
            if (states[i] == 1 && val == vals[i]) {
                return true;
            }
        }
        return false;
    }
    
    public boolean containsKey(final int key) {
        return this.contains(key);
    }
    
    public boolean forEachKey(final TIntProcedure procedure) {
        return this.forEach(procedure);
    }
    
    public boolean forEachValue(final TIntProcedure procedure) {
        final byte[] states = this._states;
        final int[] values = this._values;
        int i = values.length;
        while (i-- > 0) {
            if (states[i] == 1 && !procedure.execute(values[i])) {
                return false;
            }
        }
        return true;
    }
    
    public boolean forEachEntry(final TIntIntProcedure procedure) {
        final byte[] states = this._states;
        final int[] keys = this._set;
        final int[] values = this._values;
        int i = keys.length;
        while (i-- > 0) {
            if (states[i] == 1 && !procedure.execute(keys[i], values[i])) {
                return false;
            }
        }
        return true;
    }
    
    public boolean retainEntries(final TIntIntProcedure procedure) {
        boolean modified = false;
        final byte[] states = this._states;
        final int[] keys = this._set;
        final int[] values = this._values;
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
    
    public void transformValues(final TIntFunction function) {
        final byte[] states = this._states;
        final int[] values = this._values;
        int i = values.length;
        while (i-- > 0) {
            if (states[i] == 1) {
                values[i] = function.execute(values[i]);
            }
        }
    }
    
    public boolean increment(final int key) {
        return this.adjustValue(key, 1);
    }
    
    public boolean adjustValue(final int key, final int amount) {
        final int index = this.index(key);
        if (index < 0) {
            return false;
        }
        final int[] values = this._values;
        final int n = index;
        values[n] += amount;
        return true;
    }
    
    public int adjustOrPutValue(final int key, final int adjust_amount, final int put_amount) {
        int index = this.insertionIndex(key);
        int newValue;
        boolean isNewMapping;
        if (index < 0) {
            index = -index - 1;
            final int[] values = this._values;
            final int n = index;
            final int n2 = values[n] + adjust_amount;
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
            final int key = in.readInt();
            final int val = in.readInt();
            this.put(key, val);
        }
    }
    
    @Override
	public String toString() {
        final StringBuilder buf = new StringBuilder("{");
        this.forEachEntry(new TIntIntProcedure() {
            private boolean first = true;
            
            @Override
			public boolean execute(final int key, final int value) {
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
    
    private final class HashProcedure implements TIntIntProcedure
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
		public final boolean execute(final int key, final int value) {
            this.h += (TIntIntHashMap.this._hashingStrategy.computeHashCode(key) ^ HashFunctions.hash(value));
            return true;
        }
    }
    
    private static final class EqProcedure implements TIntIntProcedure
    {
        private final TIntIntHashMap _otherMap;
        
        EqProcedure(final TIntIntHashMap otherMap) {
            super();
            this._otherMap = otherMap;
        }
        
        @Override
		public final boolean execute(final int key, final int value) {
            final int index = this._otherMap.index(key);
            return index >= 0 && this.eq(value, this._otherMap.get(key));
        }
        
        private final boolean eq(final int v1, final int v2) {
            return v1 == v2;
        }
    }
}
