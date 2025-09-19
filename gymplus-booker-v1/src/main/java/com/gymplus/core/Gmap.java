package com.gymplus.core;

import java.util.*;

/** convenience wrapper over LinkedHashMap<String, Object> */
public class Gmap extends LinkedHashMap<String, Object> implements Cloneable {

    // TODO:
    // - there is a bug in toString() when values are null - debug and fix
    // - there is a bug in toString() when nesting is more than 1 - "newLines" param is not propagated to innner levels
    //

    public Gmap() {
        super();
    }

    public Gmap(Object... namesValues) {
        super();
        append(namesValues);
    }

    public Gmap(String[] names, Object[] values) {
        super();
        if (names.length != values.length) {
            throw new GspRuntimeException("Number of names is different than count of values");
        }
        for (int i = 0; i < names.length; i++) {
            put(names[i], values[i]);
        }
    }

    public Gmap(Map<String, Object> map) {
        super(map == null ? _empty : map);
    }

    public String[] keyArray() {
        // can't use regular toArray<T>(), see here: http://stackoverflow.com/a/395038/59770
        return this.keySet().toArray(new String[this.size()]);
    }

    public Object[] valueArray() {
        return this.values().toArray();
    }

    @Override
    public Gmap clone() { //NOPMD - CloneNotSupportedException is not compatible with throws clause in HashMap<String,Object>.clone()
        return (Gmap)super.clone();
    }

    public Gmap append(Object... namesValues) {
        int len = namesValues.length;
        if (len % 2 != 0) { throw new GspRuntimeException("Uneven number of names and values"); }
        for (int i = 0; i < len / 2; i++) {
            put((String)namesValues[i * 2], namesValues[i * 2 + 1]);
        }
        return this;
    }

    public Gmap appendAll(Map<? extends String, ? extends Object> m) {
        putAll(m);
        return this;
    }

    public Gmap select(String... keys) {
        Gmap map = new Gmap();
        for (String key : keys) {
            if (!containsKey(key)) { continue; }
            map.put(key, get(key));
        }
        return map;
    }

    public Gmap sort() {
        return sort(new Comparator<String>() {
            public int compare(String s1, String s2) {
                if (s1 == null && s2 == null) { return 0; }
                if (s1 == null) { return -1; }
                if (s2 == null) { return 1; }
                return s1.compareTo(s2);
            }
        });
    }
    
    public Gmap sort(final Comparator<String> comparator) {
        ArrayList<Map.Entry<String, Object>> list = new ArrayList<>(entrySet());
        Collections.sort(list, new Comparator<Map.Entry<String, Object>>() {
            public int compare(Map.Entry<String, Object> o1, Map.Entry<String, Object> o2) {
                return comparator.compare(o1.getKey(), o2.getKey());
            }
        });

        clear();
        for (Map.Entry<String, Object> entry : list) {
            put(entry.getKey(), entry.getValue());
        }
        return this;
    }
    
    public Gmap removeAll(String... keys) {
        for (int i = 0; i < keys.length; i++) { remove(keys[i]); }
        return this;
    }

    public Gmap removeNullKeys() {
        Iterator<Map.Entry<String, Object>> i = this.entrySet().iterator();
        while (i.hasNext()) {
            Map.Entry<String, Object> entry = i.next();
            if (entry.getKey() == null) { i.remove(); }
        }
        return this;
    }
    
    public Gmap removeNullValues() {
        Iterator<Map.Entry<String, Object>> i = this.entrySet().iterator();
        while (i.hasNext()) {
            Map.Entry<String, Object> entry = i.next();
            if (entry.getValue() == null) { i.remove(); }
        }
        return this;
    }

    public void putAll(Map<? extends String, ? extends Object> m) {
        if (m != null) { super.putAll(m); }
    }

    public Gmap except(String... keys) {
        Gmap map = new Gmap(this);
        map.removeAll(keys);
        return map;
    }
    
    ////////////////////////////////////////////////////////////////////////////////////////
    // CUSTOM/SCALAR VALUE GETTERS
    //

    //
    // NOTE: [BK] 2017-11-03
    //
    // - Java7 compiler allowed to have this (in other words, Java7 compiler assumed value returned from untyped 'get' - to be determined at runtime):
    //
    //     public <T> T get(String key) { return get(key, null, null); }
    //     String s = map.get("name");              // works
    //     tx.call("STORED_PROC", map.get("name")); // works
    //
    // - Java8 compiler still allows to have this, but in case of (Object... params), it assumes the target type will be Object[] and not Object...,
    //   i.e. since it cannot determine the cast during compile time, then in runtime, out of 2 equally possible runtime options ((??) -> Object... vs. (??) -> Object[]),
    //   it prefers the (??) -> Object[], resulting in runtime exception like "cannot cast from String to Object[]".
    //
    //     public <T> T get(String key) { return get(key, null, null); }
    //     String s = map.get("name");              // works
    //     tx.call("STORED_PROC", map.get("name")); // compiles but fails in runtime because it can't cast from String to Object[]
    //
    //   (Java bug - which is non-bug) https://bugs.openjdk.java.net/browse/JDK-8072919
    //   (SO discussions) https://stackoverflow.com/questions/40737967/broken-cast-on-compiling-generic-method-with-jdk-8
    //
    //   This behavior can be fooled by adding several overloads (tx.call(), tx.call(Object o), tx.call(Object o, Object... params)), but such a hack only works
    //   for our own methods, and we still have a problem with String.format, etc., so it's not a safe hack.
    //
    //   Therefore, unfortunately, we had to switch from a single "runtime" 'get' method to an explicit set of gets/geti/getm/geta/etc. methods which are just shortcuts
    //   to a regular explicit cast from Object get() to the target type (there are 1500 such places in the code, so adding e.g. (String) cast to all of them is too  ugly,
    //   so decided to add these shortcuts for smaller code and better readability.
    //
    public Object get(String key) { 
        return get(key, null, null);
    }
    
    public String gets(String key) {
        return (String)get(key, null, null);
    }

    public boolean getb(String key) {
        return (boolean)get(key, null, null);
    }
    
    public int geti(String key) {
        return (int)get(key, null, null);
    }

    public double getd(String key) {
        return (double)get(key, null, null);
    }
    
    public Gmap getm(String key) {
        return (Gmap)get(key, null, null);
    }
    
    public Garray geta(String key) {
        return (Garray)get(key, null, null);
    }

    public <T> T get(String key, T dflt) {
        return get(key, null, dflt);
    }

    public <T> T get(String key, Class<T> to) {
        return get(key, to, null);
    }

    public <T> T get(String key, Class<T> to, T dflt) {
        // try get value even if key doesn't contain "/" 
        // NOTE: empty/null key is supported by design of Java HashMap, so we support it too; e.g. m.put(null, value) should result in: m.get(null) --> value
        if (StrUtils.isEmpty(key) || containsKey(key)) {
            return _get(key, to, dflt);
        }

        // if not found, split by "/" and recursively scan hierarchy, until key is found
        // (the below eventually results in Gmap "child" and optional "childkey" within the "child")
        // child             -> cannot happen here, covered earlier
        // child[i]          -> child[i], null
        // child/childkey    -> child, childkey 
        // child[i]/childkey -> child[i], childkey
        // child[i]/[j]      -> child[i], childkey ( = "j")
        //
        // TODO: support for Gmap get propagation within arrays, e.g. child[i]/[j]/grandchild (need to implement recursive lookup in Garray.get(i, path..)
        //
        Object child = null;
        String childkey = null;
        if (key.charAt(0) == '/') { key = key.substring(1); }

        boolean istree = key.contains("/");
        if (istree) {
            String[] k = key.split("/", 2);
            key = k[0];
            childkey = k[1];
            if (childkey.length() > 0 && childkey.charAt(0) == '[') {
                k = childkey.split("\\[|\\]", 2);
                childkey = k[1];
            }
        }

        boolean isindexed = key.contains("[");
        if (!istree && !isindexed) { return dflt; }

        if (isindexed) {
            String[] k = key.split("\\[|\\]");
            if (k.length == 2) {
                Garray array = _get(k[0], Garray.class, null);
                if (array != null) {
                    int i = Integer.parseInt(k[1]);
                    child = array.get(i);
                    if (!(child instanceof Gmap) && !(child instanceof Garray)) { child = array.get(i, to, dflt); }
                }
            }
        } else {
            child = _get(key, Gmap.class, null);
        }

        if (child != null) {
            if (childkey != null) {
                if (child instanceof Gmap) {
                    return ((Gmap)child).get(childkey, to, dflt);
                } else if (child instanceof Garray) {
                    return ((Garray)child).get(Integer.parseInt(childkey), to, dflt);
                }
            } else {
                @SuppressWarnings("unchecked")
                T t = (T)child;
                return t;
            }
        }
        return dflt;
    }

    private <T> T _get(String key, Class<T> to, T dflt) {
        return ReflectionUtils.convert(super.get(key), to, dflt);
    }

    @Override
    public String toString() {
        return JsonSerializer.format(this);
    }

    ////////////////////////////////////////////////////////////////////////////////////////
    // TRANSFORMATIONS
    //

    /** creates new map where all keys with given prefix are grouped under given new key (i.e. creates map inside map), non-prefixed keys are copied as is */
    public Gmap groupBy(String... prefixesAndKeys) throws Exception {
        // TODO: this is probably not the fastest implementation - think if worth improving
        // TODO: maybe the right thing to do - is to move this logic to DbTransaction, where the grouped-by map will be created in the first place
        //
        int len = prefixesAndKeys.length;
        if (len % 2 != 0) { throw new I2AException("Uneven number of prefixes and groupBy keys"); }

        Gmap[] groups = new Gmap[len / 2];
        for (int i = 0; i < groups.length; i++) {
            groups[i] = new Gmap();
        }

        Gmap map = new Gmap();
        for (Map.Entry<String, Object> entry : this.entrySet()) {
            boolean prefixed = false;
            String key = entry.getKey();
            for (int i = 0; i < groups.length; i++) {
                String prefix = prefixesAndKeys[i * 2];
                if (key.startsWith(prefix)) {
                    groups[i].put(key.substring(prefix.length()), entry.getValue());
                    prefixed = true;
                    break;
                }
            }
            if (!prefixed) { map.put(key, entry.getValue()); }
        }
        for (int i = 0; i < groups.length; i++) {
            Gmap group = groups[i];
            if (group.size() > 0) { map.put(prefixesAndKeys[i * 2 + 1], group); }
        }
        return map;
    }

    private static Gmap _empty = new Gmap();
    private static final long serialVersionUID = 8414151624648894248L;

}
