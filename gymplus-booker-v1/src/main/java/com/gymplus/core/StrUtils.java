package com.gymplus.core;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLDecoder;
import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.zip.CRC32;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * StrUtils
 */
public class StrUtils {

    @ConfigurableParameter("EXPERIMENTAL: trims XML from whitespaces on both sides (make standard behavior once proven)")
    public static boolean UseNewXmlTrimming = true;

    private static final Map<String, String> patterns = new LinkedHashMap<>();

    static {
        patterns.put("userNameURLPattern", "(?<=username=)");
        patterns.put("userNameBodyPattern", "(?<=\"username\":\\s?\")");
        patterns.put("emailAddressURLPattern", "(?<=emailAddress=)");
        patterns.put("emailAddressBodyPattern", "(?<=\"emailAddress\":\\s?\")");
        patterns.put("emailValueBodyPattern", "(?<=value\":\\s?\")");
        patterns.put("papiAccountNumberPattern", "(?i)(?<=_PAPI_)");
        patterns.put("displayNameBodyPattern", "(?<=\"displayName\":\\s?\")");
        patterns.put("loginIdBodyPattern", "(?<=\"loginId\":\\s?\")");
        patterns.put("idBodyPattern", "(?<=\"id\":\\s?\")");
        patterns.put("emailGraphURLPattern", "(?<=eq\\s')");
    }

    private static final Pattern piDataPattern = Pattern.compile(
            "(" + patterns.values().stream().collect(Collectors.joining("|")) + ")" + ".*?(?=@)"
    );

    public static String hideSensitiveInformation(String input) throws UnsupportedEncodingException {
        if (input == null) { return ""; }
        input = URLDecoder.decode(input, "UTF-8");
        String obfuscatedText = obfuscateSensitiveData(input);
        obfuscatedText = encodeSensitiveData(piDataPattern, obfuscatedText);
        return obfuscatedText;
    }

    private static String obfuscateSensitiveData(String input) {
        return input
                .replaceAll("password=([^&]+)", "password=****")
                .replaceAll("newPassword=([^&]+)", "newPassword=****")
                .replaceAll("oldPassword=([^&]+)", "oldPassword=****")
                .replaceAll("password\":\\s*\"([^\"]+)", "password\":\"****")
                .replaceAll("tempPassword\":\\s*\"([^\"]+)", "tempPassword\":\"****")
                .replaceAll("perm_token\":\\s*\"([^\"]+)", "perm_token\":\"****")
                .replaceAll("access_token\":\\s*\"([^\"]+)", "access_token\":\"****")
                .replaceAll("refresh_token\":\\s*\"([^\"]+)", "refresh_token\":\"****")
                .replaceAll("postalCode\":\\s*\"([^\"]+)", "postalCode\":\"****")
                .replaceAll("secret\":\\s*\"([^\"]+)", "secret\":\"****")
                ;
    }

    /** Converts UNDER_SCORE string representation into its camelCase */
    public static String toCamelCase(String s) {
        if (s.isEmpty()) { return ""; }

        String[] parts = s.split("_");

        // don't camelCase already camelCase-ed string (so it won't become all lowercase)
        if (parts.length == 1 && Character.isLowerCase(s.charAt(0))) { return s; }

        StringBuilder sb = new StringBuilder();
        for (String part : parts) { sb.append(toProperCase(part)); }
        return sb.substring(0, 1).toLowerCase() + sb.substring(1);
    }

    /** Converts string to proper case (first letter capital, others are lower case) */
    public static String toProperCase(String s) {
        return s.substring(0, 1).toUpperCase() + s.substring(1).toLowerCase();
    }

    /** Converts camelCase string to UNDER_SCORE case */
    public static String toUnderscoreCase(String s) {
        // TODO: check if this expression is correct in 100% cases
        return s.replaceAll("([a-z])([A-Z])", "$1_$2").toUpperCase();
    }

    /** Encodes bytes array to its hexadecimal string representation */
    public static String toHexString(byte[] bytes) {
        int l = bytes.length;
        char[] result = new char[l << 1];
        for (int i = 0, j = 0; i < l; i++) {
            result[j++] = _digits[(0xF0 & bytes[i]) >>> 4];
            result[j++] = _digits[0x0F & bytes[i]];
        }

        return new String(result);
    }

    /** Decodes hexadecimal string representation into bytes array */
    public static byte[] fromHexString(String s) {
        char[] chars = s.toCharArray();
        int len = chars.length;

        if ((len & 0x01) != 0) {
            throw new GspRuntimeException("Odd number of characters.");
        }

        byte[] bytes = new byte[len >> 1];
        for (int i = 0, j = 0; j < len; i++) {
            int f = _toDigit(chars[j], j) << 4;
            j++;
            f = f | _toDigit(chars[j], j);
            j++;
            bytes[i] = (byte)(f & 0xFF);
        }
        return bytes;
    }

    /** Converts PascalCase or camelCase string to a proper sentence */
    public static String toSentence(String s) {
        if (s == null || s.length() == 0) { return s; }
        s = s.replaceAll("([a-z])([A-Z])", "$1 $2");
        return s.substring(0, 1).toUpperCase() + s.substring(1).toLowerCase();
    }

    /** joins collection of objects into string using a delimiter */
    public static <T> String join(Collection<T> objects, String delimiter) {
        return StrUtils.join(objects, delimiter, null);
    }
    
    /** joins collection of objects into string using a delimiter, and a mapper function to extract value to use from the collection items */
    public static <T> String join(Collection<T> objects, String delimiter, Function<? super T, ?> mapper) {
        if (objects == null) { return null; }
        StringBuilder builder = new StringBuilder();
        Iterator<T> iter = objects.iterator();
        while (iter.hasNext()) {
            builder.append(mapper == null ? iter.next() : mapper.apply(iter.next()));
            if (!iter.hasNext()) { break; }
            builder.append(delimiter);
        }
        return builder.toString();
    }

    /** joins array of objects into string using a delimiter */
    public static <T> String join(T[] objects, String delimiter) {
        return StrUtils.join(objects, delimiter, null);
    }
    
    /** joins array of objects into string using a delimiter, and a mapper function to extract value to use from the collection items */
    public static <T> String join(T[] objects, String delimiter, Function<? super T, ?> mapper) {
        if (objects == null) { return null; }
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < objects.length; i++) {
            builder.append(mapper == null ? objects[i] : mapper.apply(objects[i]));
            if (i == objects.length - 1) { break; }
            builder.append(delimiter);
        }
        return builder.toString();
    }

    private static int _toDigit(char ch, int index) {
        int digit = Character.digit(ch, 16);
        if (digit == -1) {
            throw new GspRuntimeException("Illegal hexadecimal charcter " + ch + " at index " + index);
        }
        return digit;
    }

    private static final char[] _digits = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};

    /** Tests if a given String is null or empty */
    public static boolean isEmpty(String str) {
        return str == null || str.isEmpty();
    }

    /** Tests if any of the given Strings is null or empty */
    public static boolean isAnyEmpty(String... s) {
        for (int i = 0; i < s.length; i++) {
            if (StrUtils.isEmpty(s[i])) { return true; }
        }
        return false;
    }

    /** Tests if all of the given Strings are null or empty */
    public static boolean isAllEmpty(String... s) {
        for (int i = 0; i < s.length; i++) {
            if (!StrUtils.isEmpty(s[i])) { return false; }
        }
        return true;
    }

    /** Tests two strings for case-sensitive content equality, taking null into account (NOTE: two null strings are equal, null string is not equal to empty string) */
    public static boolean equals(String str1, String str2) {
        if (str1 == null || str2 == null) { return str1 == str2; } //NOPMD - use of == is correct
        return str1.equals(str2);
    }

    /** Tests two strings for case-insensitive content equality, taking null into account (NOTE: two null strings are equal, null string is not equal to empty string) */
    public static boolean equalsIgnoreCase(String str1, String str2) {
        if (str1 == null) { return str2 == null; }
        if (str2 == null) { return str1 == null; }
        return str1.equalsIgnoreCase(str2);
    }

    /** Tests a string for case-sensitive content equality to any of the supplied other strings, taking null into account (NOTE: two null strings are equal, null string is not equal to empty string) */
    public static boolean equalsAny(String str, String... s) {
        for (int i = 0; i < s.length; i++) { if (StrUtils.equals(str, s[i])) { return true; } }
        return false;
    }

    /** Tests a string for case-insensitive content equality to any of the supplied other strings, taking null into account (NOTE: two null strings are equal, null string is not equal to empty string) */
    public static boolean equalsAnyIgnoreCase(String str, String... s) {
        for (int i = 0; i < s.length; i++) { if (StrUtils.equalsIgnoreCase(str, s[i])) { return true; } }
        return false;
    }

    /** Tests if a string with comma-separated list of values (and NO SPACES!), contains the given string */
    public static boolean containsInList(String liststr, String s) {
        // NOTE: under the restrictions that we have no spaces - String.contains is 20 times faster than splitting, trimming and searching array; and 100 times faster than regex
        if (StrUtils.isAnyEmpty(liststr, s)) { return false; }
        return ("," + liststr + ",").contains("," + s + ","); 
    }
    
    /** returns leftmost part of the string, up to len (excluding) */
    public static String left(String s, int len) {
        if (s == null) { return null; }
        if (s.length() <= len) { return s; }
        return s.substring(0, len);
    }

    /** returns first non-null and non-empty string */
    public static String nvl(String... s) {
        for (int i = 0; i < s.length; i++) { if (!StrUtils.isEmpty(s[i])) { return s[i]; } }
        return "";
    }

    /** escape XML special characters, unless XML string is passed as input */
    public static String escapeXml(String s) {
        // TODO: proper XML validation?
        // TODO: what about other invalid/special XML chars? E.g. 0x11. Should we remove them? Encode them? 
        String trimmed = StrUtils.isEmpty(s) ? s : (UseNewXmlTrimming ? s.replaceAll("(^\\s+)|(\\s+$)", "") : s.replaceFirst("\\s+$", ""));
        if (StrUtils.isEmpty(trimmed) || (trimmed.charAt(0) == '<' && trimmed.charAt(trimmed.length() - 1) == '>')) { return s; }
        StringBuilder result = new StringBuilder();
        StringCharacterIterator iterator = new StringCharacterIterator(s);
        char character = iterator.current();
        while (character != CharacterIterator.DONE) {
            if (character == '<') {
                result.append("&lt;");
            } else if (character == '>') {
                result.append("&gt;");
            } else if (character == '\"') {
                result.append("&quot;");
            } else if (character == '\'') {
                result.append("&#039;");
            } else if (character == '&') {
                result.append("&amp;");
            } else {
                result.append(character);
            }
            character = iterator.next();
        }
        return result.toString();
    }

    /** converts double to a string with specified number fractional digits (at least 1 digit will exist to make the result a double string, and not int/long) */
    public static String dtos(double d, int scale) {
        // NOTES:
        // 1. Doing this is surprisingly hard in Java and all obvious methods of doing this fail in some cases (see below SO threads for details)
        // 2. This is the fastest (tested) and the most correct way of doing it (http://stackoverflow.com/a/25645952/59770, and this is also how Apache's Precision.round works)
        // 3. Using BigDecimal requires special custom handling of NaN and Infinite
        // 4. BigDecimal prints trailing zeros up to 'scale', since we want to remove them, we could use BigDecimal.stripTrailingZeros(), but:
        //    a. in Java7, there's a bug which still prints 0.00000 (fixed in Java8: http://stackoverflow.com/a/21385699/59770)
        //    b. stripTrailingZeros() removes the first zero as well, effectively causing X.0 value to become int, when doing serialize->deserialize
        //    c. custom removing trailing zeros done below solves all the (a) and (b) problems and also works much faster because it's simpler (BigDecimal has to remove zeros from number, not from string, so it's more complicated)
        //
        if (Double.isNaN(d) || Double.isInfinite(d)) { return Double.toString(d); }
        
        String s = new BigDecimal(Double.toString(d)).setScale(scale, BigDecimal.ROUND_HALF_UP).toPlainString();
        if (s.indexOf('.') < 0) { 
            return s + ".0";
        } else {  
            StringBuilder sb = new StringBuilder(s);
            int len = sb.length();
            for (int i = sb.length() - 1; i > 2; i--) {
                if (sb.charAt(i) == '0' && sb.charAt(i - 1) != '.') { 
                    len = i; 
                } else {
                    break;
                }
            }
            sb.setLength(len);
            return sb.toString();
        }
    }

    private static String encodeSensitiveData(Pattern regexPattern, String input){
        Matcher matcher = regexPattern.matcher(input);
        while (matcher.find()) {
            String matchedValue = matcher.group();
            input = input.replaceAll(matchedValue, getCRC32ValueOfString(matchedValue));
        }
        return input;
    }

    public static String getCRC32ValueOfString(String input) {
        CRC32 crc32OfInput = new CRC32();
        crc32OfInput.update(input.toLowerCase().getBytes(UTF_8));
        return String.valueOf(crc32OfInput.getValue());
    }
        
}
