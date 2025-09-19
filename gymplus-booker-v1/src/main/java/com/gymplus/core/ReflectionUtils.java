package com.gymplus.core;

import java.util.HashMap;

public class ReflectionUtils {

    /** converts object of a supported type to any other supported type, plus allows custom default */
    @SuppressWarnings("unchecked")
    public static <T> T convert(Object o, Class<T> to, T dflt) {
        // "to" is either supplied or is inferred from the default (reducing to proper primitive types, if needed)
        if (to == null && dflt != null) {
            to = (Class<T>)dflt.getClass();
            if (!to.isPrimitive()) {
                to = (Class<T>)((to == Boolean.class) ? boolean.class : (to == Integer.class ? int.class : (to == Long.class ? long.class : (to == Double.class ? double.class : to))));
            }
        }

        // if no "to" is supplied - return whatever we have
        if (to == null) {
            try {
                return (T)o;
            } catch (Exception e) {  }
            return null;
        }

        // null value handling
        if (o == null) {
            if (to == double.class) { return (T)(Object)Double.NaN; }
            return dflt != null ? dflt : getDefault(to);
        }

        // if "to" is supplied - we know target type, so we try to cast whenever possible
        Class<?> from = o.getClass();
        if (!from.isPrimitive()) {
            from = (from == Boolean.class) ? boolean.class : (from == Integer.class ? int.class : (from == Long.class ? long.class : (from == Double.class ? double.class : from)));
        }
        if (to.isAssignableFrom(from)) {
            return (T)o; // this also works for Gmap, Garray, Gxml
        }

        // custom conversions to int
        if (to == int.class) {
            if (from == long.class && ((long)o) >= Integer.MIN_VALUE && ((long)o) <= Integer.MAX_VALUE)  {
                return (T)(Object)(int)(long)o;
            } else if (from == double.class && ((double)o) % 1 == 0)  {
                return (T)(Object)(int)(double)o;
            }
        }

        // custom conversions to long
        if (to == long.class) {
            if (from == int.class)  {
                return (T)(Object)(long)(int)o;
            } else if (from == double.class && ((double)o) % 1 == 0)  {
                return (T)(Object)(long)(double)o;
            }
        }
    
        // custom conversions to double
        if (to == double.class) {
            if (from == int.class) {
                return (T)(Object)(double)(int)o;
            }
        }
        
        // custom conversion from various supported source types to string
        if (to == String.class) {
            if (from == boolean.class) {
                return (T)Boolean.toString((boolean)o);
            } else if (from == int.class) {
                return (T)Integer.toString((int)o);
            } else if (from == long.class) {
                return (T)Long.toString((long)o);
            } else if (from == double.class) {
                return (T)Double.toString((double)o);
            }
            return (T)o.toString();
        }

        // custom conversion from Gmap to Garray[1]
        if (to == Garray.class) {
            if (from == Gmap.class) {
                return (T)new Garray((Gmap)o);
            }
        }

        // custom conversion from string to various supported target types
        if (from == String.class) {
            return ReflectionUtils.parseString((String)o, to, dflt);
        }
        return dflt != null ? dflt : getDefault(to);
    }

    /** converts string to common supported data types, returns dflt if cannot convert; if target is Gmap/Garray, source is assumed to be valid JSON string */
    public static <T> T parseString(String from, Class<T> to, T dflt) {
        try {
            return parseStringOrEmpty(from, to, true);
        } catch (Exception e) {
            return dflt;
        }
    }

    /** converts string to common supported data types (with special treatment of empty input for non-string target types); if target is Gmap/Garray, source is assumed to be valid JSON string */
    @SuppressWarnings("unchecked")
    public static <T> T parseStringOrEmpty(String from, Class<T> to, boolean throwOnEmpty) throws Exception {
        Object o = null;
        String empty = "EMPTY";

        if (to == boolean.class) {
            o = ((from == null || from.isEmpty()) ? empty : ReflectionUtils.parseBoolean(from));
        } else if (to == int.class) {
            o = ((from == null || from.isEmpty()) ? empty : Integer.parseInt(from));
        } else if (to == long.class) {
            o = ((from == null || from.isEmpty()) ? empty : Long.parseLong(from));
        } else if (to == double.class) {
            o = ((from == null || from.isEmpty()) ? empty : Double.parseDouble(from));
        } else if (to == String.class) {
            o = from; // "" is a very valid string, so we don't return empty (e.g. to distinguish null strings from empty strings)
        } else if (to == Gmap.class) {
            o = ((from == null || from.isEmpty()) ? empty : JsonSerializer.parseMap(from));
        } else if (to == Garray.class) {
            o = ((from == null || from.isEmpty()) ? empty : JsonSerializer.parseArray(from));
        } else if (to.isEnum()) {
            o = ((from == null || from.isEmpty()) ? empty : ReflectionUtils.parseEnum(from, (Class<? extends Enum<?>>)to));
        } else if (to == Object.class) {
            o = (T)from;
        } else {
            throw new UnsupportedTypeException(to);
        }

        if (o == empty) {
            if (throwOnEmpty) {
                throw new ParseFailureException(from, to);
            } else {
                o = getDefault(to);
            }
        }

        return (T)o;
    }

    /** returns default values of supported data types; mimics C# "default(type)" clause, which doesn't exist in Java */
    @SuppressWarnings("unchecked")
    public static <T> T getDefault(Class<T> clazz) {
        if (clazz == boolean.class) {
            return (T)(Object)false;
        } else if (clazz == int.class) {
            return (T)(Object)0;
        } else if (clazz == long.class) {
            return (T)(Object)0L;
        } else if (clazz == double.class) {
            return (T)(Object)Double.NaN;
        } else if (clazz == String.class) {
            return (T)null;
        } else if (clazz == Gmap.class) {
            return (T)null;
        } else if (clazz == Garray.class) {
            return (T)null;
        } else if (clazz.isEnum()) {
            return (T)null;
        } else if (clazz == Object.class) {
            return (T)null;
        } else if (clazz == void.class) {
            return (T)null;
        } else {
            throw new UnsupportedTypeException(clazz);
        }
    }

    /** a proper parseBoolean, as opposed to Java built-in parseBoolean which has a problem of parseBoolean("yes") = false! */
    // NOTE: in Java, as in Java: parseBoolean("yes") = false; parseInt("whatever") = exception; the below solves Java boolean parsing stupidity
    public static boolean parseBoolean(String from) throws Exception {
        if (StrUtils.equalsIgnoreCase(from, "true")) {
            return true;
        } else if (StrUtils.equalsIgnoreCase(from, "false")) {
            return false;
        }
        throw new ParseFailureException(from, boolean.class);
    }

    /** returns enum value basing on a string representation, generated by relevant enum.toString call (allows string-to-enum deserialization for enums with custom toString implementation) */
    public static <T extends Enum<?>> T parseEnum(String from, Class<? extends Enum<?>> clazz) throws Exception {
        @SuppressWarnings("unchecked")
        T t = (T)_enumValues.get(clazz.getCanonicalName() + "." + from);
        if (t == null) { throw new ParseFailureException(from, clazz); }
        return t;
    }

    // maps string representations of enum values to actual values - allows 2-way serialization of enums with custom toString implementation
    private static HashMap<String, Object> _enumValues = new HashMap<>();

    // custom exception used when input is not parsable
    public static class UnsupportedTypeException extends RuntimeException {
        public UnsupportedTypeException(Class<?> clazz) {
            super("Unsupported reflection type " + clazz);
        }
        private static final long serialVersionUID = 1224293050700806226L;
    }

    // custom exception used to distinguish pass-through exceptions from caught exceptions
    public static class ParseFailureException extends Exception {
        public ParseFailureException(String input, Class<?> clazz) {
            super("Failed to parse '" + input + "' as " + clazz);
        }
        private static final long serialVersionUID = 5447613907772494250L;
    }

}
