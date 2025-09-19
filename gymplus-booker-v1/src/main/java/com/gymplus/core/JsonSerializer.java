package com.gymplus.core;

import java.lang.reflect.Array;
import java.util.Map;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** JSON serialization services */
public class JsonSerializer {

    @ConfigurableParameter("EXPERIMENTAL: emits JSON-compliant output (NaN/Infinity becomes null, etc.)")
    private static boolean UseNewCompliantJson = true;
    
    /** formats map object into its JSON representation (fully JSON-compliant) */
    public static String format(Gmap map) {
        return Json.format(map);
    }

    /** formats array object into its JSON representation (fully JSON-compliant) */
    public static String format(Garray array) {
        return Json.format(array);
    }

    /** INTERNAL: formats any object according to JSON-compliance */
    public static String formatObject(Object o) {
        return Json.format(o);
    }
    
    /** parses a JSON string to a map object */
    public static Gmap parseMap(String s) throws Exception {
        return Json.parse(s, Gmap.class);
    }

    /** parses a JSON string to a array object */
    public static Garray parseArray(String s) throws Exception {
        return Json.parse(s, Garray.class);
    }

    private static class Json {

        // NOTES:
        // - Single quoted strings and comments, although invalid by JSON spec, are fully supported for convenience (performance loss is ~10% so no big deal)
        // - It seems that a literal "aaa'bbb'ccc" will be split by tokenizer, but it isn't, because of greedy regex default (it matches the larger double-quoted part first)
        // - double.NaN will be brought as null in a valid JSON, and then e.g. inserted to DB as NULL (DB have no way to convert it to NaN), and then, upon return from DB
        //   this value will be skipped (as all null values), i.e. the resulting map will be different than the input, i.e. roundtrip rule is violated; no solution so far. 
        //
        public static <T> T parse(String input, Class<T> clazz) throws Exception {
            if (StrUtils.isEmpty(input)) { return null; }
            input = _reSingleComments.matcher(input).replaceAll("");
            input = _reMultilineComments.matcher(input).replaceAll("");
            input = input.replace("\\'", "\uFDD1");  // temporarily unescape single quotes (using non-char unicode)
            input = input.replace("\\\"", "\uFDD2"); // temporarily unescape double quotes (using non-char unicode)

            // tokenize input string
            Matcher tokens = _reTokenizer.matcher(input);
            if (!tokens.find()) { throw new I2AException("Invalid JSON"); }

            int pos = -1;
            Object result = null;
            try {
                Object obj = null;
                String key = null;
                result = tokens.group(0).charAt(0) == '[' ? new Garray() : new Gmap();
                Stack<Object> stack = new Stack<>();
                stack.push(result);

                while (tokens.find()) {
                    pos = tokens.start();
                    String token = tokens.group(0);
                    switch (token.charAt(0)) {
                        case '[':
                            obj = stack.peek();
                            stack.push(_add(obj, key, new Garray()));
                            key = null;
                            break;

                        case ']':
                            stack.pop();
                            break;

                        case '{':
                            obj = stack.peek();
                            stack.push(_add(obj, key, new Gmap()));
                            key = null;
                            break;

                        case '}':
                            stack.pop();
                            break;

                        default:
                            obj = stack.peek();
                            if (key == null) {
                                if (obj instanceof Gmap) {
                                    key = _unescape(token); // use as key for next value seen.
                                    break;
                                }
                            }

                            // parse value
                            Object value = null;
                            if ("undefined".equals(token)) {
                                throw new I2AException("'undefined' is not defined in JSON spec");
                            } else if ("null".equals(token)) {
                                value = null;
                            } else if ("true".equals(token)) {
                                value = true;
                            } else if ("false".equals(token)) {
                                value = false;
                            } else if (token.matches("^[+-]?\\d+$")) {
                                try {
                                    value = Integer.parseInt(token);
                                } catch (NumberFormatException nfe) {
                                    value = Long.parseLong(token);
                                }
                            } else if (token.matches("^[-+]?[0-9]*\\.?\\d+([eE][-+]?\\d+)?$") || token.matches("^NaN|[+-]?Infinity$")) {
                                try { value = Double.parseDouble(token); } catch (Exception e) { value = Double.NaN; }
                            } else {
                                value = _unescape(token);
                            }
                            _add(obj, key, value);
                            key = null;
                            break;
                    }
                }
                if (stack.size() != 0) {
                    throw new I2AException("Expected " + ((stack.peek() instanceof Garray) ? "]" : "}"));
                }
            } catch (Exception e) {
                throw new I2AException(e, "Failed to parse JSON at: %s...", (pos < 0 ? "(unknown)" : input.substring(pos, Math.min(pos + 32, input.length()))));
            }

            @SuppressWarnings("unchecked")
            T t = (T)result;
            return t;
        }

        public static String format(Object value) {
            if (value == null) { return null; }
            return _format(new StringBuilder(), value).toString();
        }

        // NOTES:
        // - this is a permissive formatter, it allows non-compliant primitives (they will be toString()-ed)
        // - it serializes NaN and +/-Infinity (JSON spec requires to serialize them as null, but since everyone
        //   realizes it was a JSON spec mistake, and browsers tend to accept NaN values - why losing this stuff?)
        //
        private static StringBuilder _format(StringBuilder sb, Object value) {
            if (value == null) {
                return sb.append("null");
            } else if (value instanceof String || value.getClass().isEnum()) {
                return sb.append(_escape(value.toString()));
            } else if (value instanceof Double) {
                if (UseNewCompliantJson) {
                    // JSON spec requires to serialize NaN and Infinity as 'null' (even though JavaScript supports them, compliant JSON parsers fail to parse it, so we must comply with this idiocy)
                    if (Double.isNaN((double)value) || Double.isInfinite((double)value)) { return sb.append("null"); }
                }
                return sb.append(StrUtils.dtos((double)value, 5)); // round doubles to 5 digits, to avoid DB string overflows
            } else if (value instanceof Gmap) {
                sb.append("{");
                boolean first = true;
                for (Map.Entry<String, Object> kv : ((Gmap)value).entrySet()) {
                    if (first) { first = false; } else { sb.append(","); }
                    sb.append(_escape(kv.getKey()));
                    sb.append(":");
                    _format(sb, kv.getValue());
                }
                sb.append("}");
                return sb;
            } else if (value instanceof Garray) {
                sb.append("[");
                boolean first = true;
                for (Object o : (Garray)value) {
                    if (first) { first = false; } else { sb.append(","); }
                    _format(sb, o);
                }
                sb.append("]");
                return sb;
            } else if (value.getClass().isArray()) {
                // NOTE: supporting primitive arrays violates "roundtrip" rule of the serializer (because they'll be deserialized back into Garray),
                // but this violation seems to be acceptable considering its safety and the benefit of ability to serialize primitive arrays
                sb.append("[");
                for (int i = 0; i < Array.getLength(value); i++) {
                    if (i > 0) { sb.append(","); }
                    _format(sb, Array.get(value, i));
                }
                sb.append("]");
                return sb;
            } else {
                return sb.append(value.toString());
            }
        }

        // adds the value to a map (with key) or array (to the end)
        private static Object _add(Object o, String key, Object value) {
            if (o instanceof Gmap) {
                ((Gmap)o).put(key, value);
            } else {
                ((Garray)o).add(value);
            }
            return value;
        }

        // quotes and escapes string
        //
        // NOTES:
        // 1. Pure JSON only requires to escape " and \, but HTML also requires to escape other special chars (\b\f\n\r\t). Since HTML blocks (e.g. notes) are passed
        //    via the same JSON serializer infrastructure - we escape HTML characters as well (all standard JSON implementations also do so, for the same reasons).
        //
        // 2. / is escaped by most implementations (including apache-commons), but Gson/Guava says "Removing slash for now since it causes some incompatibilities"
        //    (Gson code: Escaper.java). We stick to Google's notation, because (a) they know better; (b) there's no practical browser problem with non-escaping /.
        //
        private static String _escape(String s) {
            if (s == null) { return null; }
            StringBuilder sb = new StringBuilder(s.length() + 16); // reasonable extra for encoding (it's perf thing only, SB will enlarge itself if needed)
            sb.append("\"");
            for (int i = 0; i < s.length(); i++) {
                char c = s.charAt(i);
                switch (c) {
                    case '\b': sb.append("\\b"); break;
                    case '\f': sb.append("\\f"); break;
                    case '\n': sb.append("\\n"); break;
                    case '\r': sb.append("\\r"); break;
                    case '\t': sb.append("\\t"); break;
                    case '\\': sb.append("\\\\"); break;
                    case '\"': sb.append("\\\""); break;
                    default: sb.append(c); break;
                }
            }
            sb.append("\"");
            return sb.toString();
        }

        // unquotes and unescapes string 
        // NOTE: this is not 100% by the book because uXXXX should also be supported but we don't care because JSON allows actual Unicode characters to be in the string instead (see also notes in _escape method)
        private static String _unescape(String s) {
            if (s == null || s.length() < 2) { return s; }
            StringBuilder sb = new StringBuilder(s.length());
            int start = 0;
            int end = s.length() - 1;
            char c0 = s.charAt(start);
            char c1 = s.charAt(end);
            if ((c0 == '"' && c1 == '"') || (c0 == '\'' && c1 == '\'')) {
                start++;
                end--;
            }
            boolean escape = false;
            for (int i = start; i <= end; i++) {
                char c = s.charAt(i);
                switch (c) {
                    case '\\':
                        if (!escape) {
                            escape = true;
                            continue;
                        }
                        sb.append('\\');
                        break;

                    case '\uFDD1': sb.append('\''); break;
                    case '\uFDD2': sb.append('\"'); break;
                    case 'b': sb.append(escape ? '\b' : 'b'); break;
                    case 'f': sb.append(escape ? '\f' : 'f'); break;
                    case 'n': sb.append(escape ? '\n' : 'n'); break;
                    case 'r': sb.append(escape ? '\r' : 'r'); break;
                    case 't': sb.append(escape ? '\t' : 't'); break;
                    default: sb.append(c); break;
                }
                escape = false;
            }
            return sb.toString();
        }

        private static Pattern _reTokenizer = Pattern.compile("[\\{\\}\\[\\]]|'[^']*'|\"[^\"]*\"|[\\w\\+\\-\\.]+");
        private static Pattern _reSingleComments = Pattern.compile("/\\/\\/.*[\\r\\n]+");
        private static Pattern _reMultilineComments = Pattern.compile("(?m)\\/\\*.*\\*\\/");

    }
}
