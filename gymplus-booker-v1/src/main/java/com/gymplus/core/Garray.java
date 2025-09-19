package com.gymplus.core;

import java.lang.reflect.Array;
import java.util.*;

/** convenience wrapper over ArrayList<Object> */
public class Garray extends ArrayList<Object> implements Cloneable {

    // TODO:
    // - implement treeGroup(arrays, parent-child definitions) and use it instead of consolidateBy for efficiency, in cases where there are many children
    // - implement getColumn(clazz, name, default), should return ArrayList<T>? 

    /** a flag indicating if this is an array of Gmap object of a uniform structure (allows "table" - like operations, such as groupBy, etc.) */
    public boolean isRecordset = true;

    /** constructs a new array */
    public Garray() {
        super();
    }

    /** constructs Garray from several objects */
    public Garray(Object... objects) {
        for (Object o : objects) {
            add(o);
        }
    }

    /** constructs Garray from Java array */
    public static <T> Garray fromArray(T[] array) {
        Garray a = new Garray();
        for (T o : array) {
            a.add(o);
        }
        return a;
    }
    
    /** constructs Garray from any other collection of objects */
    public static <T> Garray fromCollection(Collection<T> collection) { // this method exists because new Garray(Gmap) should not 'expand' the map into items, so it can't be used for this purpose
        Garray a = new Garray();
        for (T o : collection) {
            a.add(o);
        }
        return a;
    }
    
    /** returns a shallow-clone of this array */
    @Override
    public Garray clone() { //NOPMD - super clone specifically not used
        Garray array = new Garray();
        array.addAll(this);
        return array;
    }

    /** gets a record by its index (returns null if no such record); throws exception if this is not a recordset */
    public Gmap record(int index) {
        return (0 <= index && index < size()) ? records().get(index) : null;
    }

    /** casts this array to list of Gmap objects (if this array is a recordset); the resulting list is an editable view on the original array */
    public List<Gmap> records() {
        if (!isRecordset) { throw new GspRuntimeException("This Garray is not a recordset"); }
        return _records;
    }
    private GmapArray _records = new GmapArray();

    /** gets element by its index converting to a type specified by the dflt */
    public <T> T get(int index, T dflt) {
        return get(index, null, dflt);
    }

    /** gets element by its index converting to a given type */
    public <T> T get(int index, Class<T> to) {
        return get(index, to, null);
    }

    /** gets element by its index converting to given type, and using given default if not found or cannot be converted */
    public <T> T get(int index, Class<T> to, T dflt) {
        Object o = (0 <= index && index < size()) ? super.get(index) : null;
        return ReflectionUtils.convert(o, to, dflt);
    }

    // TODO: this will not work with T = non-nullable primitive type, see ReflectionUtils.parseArrayOrEmpty in order how to do it right (and also notes there) 
    /** returns a copy of this array (assuming it's not a recordset), casting items to given type */
    @SuppressWarnings("unchecked")
    public <T> T[] toArray(Class<T> to, T dflt) {
        T[] array = (T[])Array.newInstance(to, size());
        for (int i = 0; i < size(); i++) { array[i] = (T)get(i, to, dflt); }
        return array;
    }

    // TODO: this will not work with T = primitive type, see ReflectionUtils.parseArrayOrEmpty in order how to do it right (and also notes there) 
    /** gets values in specific column as array, casting items to given type */
    @SuppressWarnings("unchecked")
    public <T> T[] toArray(String column, Class<T> to, T dflt) {
        T[] array = (T[])Array.newInstance(to, size());
        for (int i = 0; i < size(); i++) { array[i] = record(i).get(column, to, dflt); }
        return array;
    }

    /** returns a Set constructed from this array (assuming it's not a recordset), casting items to given type */
    public <T> HashSet<T> toSet(Class<T> to, T dflt) {
        return new HashSet<T>(Arrays.asList(toArray(to, dflt)));
    }
    
    /** returns a Set constructed from values of the specific column, casting items to given type */
    public <T> HashSet<T> toSet(String column, Class<T> to, T dflt) {
        return new HashSet<T>(Arrays.asList(toArray(column, to, dflt)));
    }
    
    /** transforms keyColumn/valueColumn into corresponding Gmap (keyColumn must not contain null values) */
    public Gmap toMap(String keyColumn, String valueColumn) {
        Gmap map = new Gmap();
        for (int i = 0; i < size(); i++) {
            Gmap m = record(i);
            map.put(m.get(keyColumn, String.class), m.get(valueColumn));
        }
        return map;
    }
    
    /** checks if a non-recordset Garray contains specified value */
    public boolean contains(Object value) {
        if (isRecordset) { throw new GspRuntimeException("A recordset Garray cannot contain scalar value"); }
        return super.contains(value);
    }
    
    /** checks if a specified column of a recordset Garray contains specified value */
    public boolean contains(String column, Object value) {
        return indexOf(column, value) > -1;
    }
    
    /** returns an index in a recordset Garray of the object having specified column with specified value */
    public int indexOf(String column, Object value) {
        if (!isRecordset) { throw new GspRuntimeException("This Garray is not a recordset"); }
        for (int i = 0; i < size(); i++) {
            if (value == null) {
                if (record(i).get(column) == null) { return i; }
            } else {
                if (value.equals(record(i).get(column))) { return i; }
            }
        }
        return -1;
    }
    
    @Override
    public boolean add(Object o) {
        if (isRecordset && !(o instanceof Gmap)) { isRecordset = false; }
        return super.add(o);
    }

    @Override
    public void add(int index, Object o) {
        if (isRecordset && !(o instanceof Gmap)) { isRecordset = false; }
        super.add(index, o);
    }

    @Override
    public boolean addAll(Collection<? extends Object> c) {
        for (Object o : c) { if (isRecordset && !(o instanceof Gmap)) { isRecordset = false; } }
        return super.addAll(c);
    }

    @Override
    public boolean addAll(int index, Collection<? extends Object> c) {
        for (Object o : c) { if (isRecordset && !(o instanceof Gmap)) { isRecordset = false; } }
        return super.addAll(index, c);
    }

    @Override
    public Object set(int index, Object o) {
        if (isRecordset && !(o instanceof Gmap)) { isRecordset = false; }
        return super.set(index, o);
    }

    /** removes null items from this array */
    public void removeNulls() {
        Iterator<Object> i = this.iterator();
        while (i.hasNext()) {
            Object entry = i.next();
            if (entry == null) { i.remove(); }
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////
    // TRANSFORMATIONS
    //

    /** adds a column to this array by adding the same entry to all contained maps; if entries with this key already exist - their values are replaced */
    public void addColumn(String key, Object value) {
        for (Gmap map : records()) {
            map.put(key, value);
        }
    }

    /** removes a column(s) from this array by removing the same entry from all contained maps */
    public void removeColumns(String... keys) {
        for (Gmap map : records()) {
            for (int i = 0; i < keys.length; i++) {
                map.remove(keys[i]);
            }
        }
    }

    /** removes a column(s) from this array by removing the same entry from all contained maps */
    public Garray renameColumn(String src, String dst) {
        for (Gmap map : records()) {
            map.append(dst, map.get(src)).except(src);
        }
        return this;
    }

    /** selects specified columns and returns a new array containing only selected ones */
    public Garray select(String... keys) {
        Garray array = new Garray();
        for (Gmap map : records()) {
            array.add(map.select(keys));
        }
        return array;
    }

    /** creates a new array where all keys with given prefix are grouped under given new key (i.e. for each record, creates map inside map), non-prefixed keys are copied as is */
    public Garray groupBy(String... prefixesAndKeys) throws Exception {
        Garray array = new Garray();
        for (Gmap map : records()) {
            array.add(map.groupBy(prefixesAndKeys));
        }
        return array;
    }

    /**
     * Creates a new array where all maps are consolidated using specified consolidation factors, which are ordered (top to bottom of the resulting tree) triples: 
     * <ul>
     * <li>array.consolidateBy("a$", "accounts", "accountId", "p$", "persons", "personId", "c$", "contacts", "id")
     * </ul>
     * The behavior is similar to groupBy, but hierarchical, i.e. the above example will create an array of accounts (distinct by account.id), each with its properties, 
     * and each with array of persons (stored in 'persons' key; distinct by person.id), each with its properties and each with array of contacts (stored in 'contacts'
     * key; NOT DISTINCT because there's no need to group by leaf nodes). Note that if any of the group_key values is null, that subtree is skipped (this allows 
     * to filter out all-null areas which typically present in JOINed recordsets)
     */
    public Garray consolidateBy(String... factors) throws Exception {
        if (factors.length % 3 != 0) { throw new I2AException("Consolidation factors must be defined as triples of (prefix, group_name, group_key)"); }

        // TODO: this method is far from the fastest implementation - improve it. For example, both calls to groupBy 
        //       are made many times for the same data due to recursive nature of this method. Replace by simple loop which 
        //       removes prefixed parts and puts them to 'parent'. 
        //
        // TODO: prefix-less entries are removed, which is wrong - they should remain at top level array (just like with groupBy), see how to fix 
        //
        boolean isLast = factors.length == 3;
        String prefix = factors[0];
        String pname = factors[1];
        String pkey = factors[2];

        if (isLast) {
            Garray tree = new Garray();
            for (int i = 0; i < this.size(); i++) {
                Gmap m = record(i).groupBy(prefix, pname);
                Gmap parent = (Gmap)m.remove(pname);
                if (parent == null || parent.get(pkey) == null) { continue; }
                tree.add(parent);
            }
            return tree;
        }

        // factors.length = 6+, so we can access factors[4] which is name of next level's group
        String cname = factors[4];

        Gmap parents = new Gmap();
        for (int i = 0; i < this.size(); i++) {
            Gmap m = record(i).groupBy(prefix, pname);
            Gmap parent = (Gmap)m.remove(pname); // what remains in 'm' will become child
            if (parent == null) { continue; }

            String pid = parent.gets(pkey);
            if (pid == null) { continue; }

            Gmap newparent = parents.getm(pid);
            if (newparent == null) {
                newparent = new Gmap(parent);
                newparent.put(cname, new Garray());
                parents.put(pid, newparent);
            }
            newparent.geta(cname).add(m);
        }

        Garray tree = new Garray();
        Object[] O = parents.valueArray();
        for (int i = 0; i < O.length; i++) {
            Gmap m = (Gmap)O[i];
            Garray children = (Garray)m.remove(cname);
            m.put(cname, children.consolidateBy(Arrays.copyOfRange(factors, 3, factors.length)));
            tree.add(m);
        }
        return tree;
    }

    @Override
    public String toString() {
        return JsonSerializer.format(this);
    }

    // editable view on the Garray elements, all cast to Gmap
    private class GmapArray implements List<Gmap> {

        @Override
        public int size() {
            _check();
            return Garray.this.size();
        }

        @Override
        public boolean isEmpty() {
            _check();
            return Garray.this.isEmpty();
        }

        @Override
        public boolean contains(Object o) {
            _check();
            return Garray.this.contains(o);
        }

        @Override
        public Iterator<Gmap> iterator() {
            _check();
            return new GmapIterator(this, Garray.this.listIterator());
        }

        @Override
        public Object[] toArray() {
            _check();
            return Garray.this.toArray();
        }

        @Override
        public <T> T[] toArray(T[] a) {
            _check();
            return Garray.this.toArray(a);
        }

        @Override
        public boolean add(Gmap e) {
            _check();
            return Garray.this.add(e);
        }

        @Override
        public boolean remove(Object o) {
            _check();
            return Garray.this.remove(o);
        }

        @Override
        public boolean containsAll(Collection<?> c) {
            _check();
            return Garray.this.containsAll(c);
        }

        @Override
        public boolean addAll(Collection<? extends Gmap> c) {
            _check();
            return Garray.this.addAll(c);
        }

        @Override
        public boolean addAll(int index, Collection<? extends Gmap> c) {
            _check();
            return Garray.this.addAll(index, c);
        }

        @Override
        public boolean removeAll(Collection<?> c) {
            _check();
            return Garray.this.removeAll(c);
        }

        @Override
        public boolean retainAll(Collection<?> c) {
            _check();
            return Garray.this.retainAll(c);
        }

        @Override
        public void clear() {
            _check();
            Garray.this.clear();
        }

        @Override
        public Gmap get(int index) {
            _check();
            return (Gmap)Garray.this.get(index);
        }

        @Override
        public Gmap set(int index, Gmap element) {
            _check();
            return (Gmap)Garray.this.set(index, element);
        }

        @Override
        public void add(int index, Gmap element) {
            _check();
            Garray.this.add(index, element);
        }

        @Override
        public Gmap remove(int index) {
            _check();
            return (Gmap)Garray.this.remove(index);
        }

        @Override
        public int indexOf(Object o) {
            return Garray.this.indexOf(o);
        }

        @Override
        public int lastIndexOf(Object o) {
            _check();
            return Garray.this.lastIndexOf(o);
        }

        @Override
        public ListIterator<Gmap> listIterator() {
            _check();
            return new GmapIterator(this, Garray.this.listIterator());
        }

        @Override
        public ListIterator<Gmap> listIterator(int index) {
            _check();
            return new GmapIterator(this, Garray.this.listIterator(index));
        }

        @Override
        public List<Gmap> subList(int fromIndex, int toIndex) {
            throw new UnsupportedOperationException();
        }

        private void _check() {
            if (!Garray.this.isRecordset) { throw new UnsupportedOperationException("This Garray is not a recordset"); }
        }

    }

    // editable iterator over the Garray elements, all cast to Gmap
    private static class GmapIterator implements ListIterator<Gmap> {
        private GmapArray _array;
        private ListIterator<Object> _i;

        GmapIterator(GmapArray array, ListIterator<Object> i) {
            _array = array;
            _i = i;
        }

        @Override
        public boolean hasNext() {
            _array._check();
            return _i.hasNext();
        }

        @Override
        public Gmap next() {
            _array._check();
            return (Gmap)_i.next();
        }

        @Override
        public void remove() {
            _array._check();
            _i.remove();
        }

        @Override
        public boolean hasPrevious() {
            _array._check();
            return _i.hasPrevious();
        }

        @Override
        public Gmap previous() {
            _array._check();
            return (Gmap)_i.previous();
        }

        @Override
        public int nextIndex() {
            _array._check();
            return _i.nextIndex();
        }

        @Override
        public int previousIndex() {
            _array._check();
            return _i.previousIndex();
        }

        @Override
        public void set(Gmap e) {
            _array._check();
            _i.set(e);
        }

        @Override
        public void add(Gmap e) {
            _array._check();
            _i.add(e);
        }
    }

    private static final long serialVersionUID = -7915622871235895987L;

}
