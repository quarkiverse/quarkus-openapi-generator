package io.quarkiverse.openapi.generator.providers;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.Locale;

import jakarta.ws.rs.ext.ParamConverter;
import jakarta.ws.rs.ext.ParamConverterProvider;
import jakarta.ws.rs.ext.Provider;

import io.quarkiverse.openapi.generator.annotations.EncodedPathParam;
import io.quarkiverse.openapi.generator.annotations.MultiSegmentPathParam;

/**
 * JAX-RS param converter provider used by generated REST clients for path parameters.
 * <p>
 * It percent-encodes reserved characters for path parameter values, while preserving already-encoded
 * percent triplets such as {@code %2F}. That allows callers to pass either raw paths like
 * {@code mygroup/myproject/backend} or already-encoded values like {@code mygroup%2Fmyproject%2Fbackend}
 * without producing invalid double-encoded URLs.
 */
@Provider
public class PathParamEncodingParamConverterProvider implements ParamConverterProvider {

    private static final ParamConverter<String> STRING_PATH_PARAM_CONVERTER = new ParamConverter<>() {
        @Override
        public String fromString(String value) {
            return value;
        }

        @Override
        public String toString(String value) {
            return encodePathParamValuePreservingEscapes(value);
        }
    };

    private static final ParamConverter<String> MULTI_SEGMENT_PATH_PARAM_CONVERTER = new ParamConverter<>() {
        @Override
        public String fromString(String value) {
            return value;
        }

        @Override
        public String toString(String value) {
            return encodePathParamValuePreservingEscapesPreservingSlashes(value);
        }
    };

    @Override
    @SuppressWarnings("unchecked")
    public <T> ParamConverter<T> getConverter(Class<T> rawType, Type genericType, Annotation[] annotations) {
        if (rawType != String.class) {
            return null;
        }
        if (hasMultiSegmentPathParam(annotations)) {
            return (ParamConverter<T>) MULTI_SEGMENT_PATH_PARAM_CONVERTER;
        }
        if (hasEncodedPathParam(annotations)) {
            return (ParamConverter<T>) STRING_PATH_PARAM_CONVERTER;
        }
        return null;
    }

    /**
     * Encodes a path-parameter value using UTF-8 percent encoding, preserving existing escape sequences.
     * <p>
     * This treats {@code /} as data and encodes it to {@code %2F}, which is what generated clients need for
     * path-parameter values that may span multiple raw segments.
     *
     * @param value the raw or already-encoded path-parameter value
     * @return the encoded path-parameter value, or {@code null} if the input was {@code null}
     */
    static String encodePathParamValuePreservingEscapes(String value) {
        if (value == null) {
            return null;
        }

        StringBuilder encoded = new StringBuilder(value.length());
        for (int i = 0; i < value.length();) {
            char ch = value.charAt(i);
            if (ch == '%' && i + 2 < value.length() && isHexDigit(value.charAt(i + 1)) && isHexDigit(value.charAt(i + 2))) {
                encoded.append(ch).append(value.charAt(i + 1)).append(value.charAt(i + 2));
                i += 3;
                continue;
            }

            int codePoint = value.codePointAt(i);
            if (isUnreserved(codePoint)) {
                encoded.appendCodePoint(codePoint);
            } else {
                byte[] bytes = new String(Character.toChars(codePoint)).getBytes(StandardCharsets.UTF_8);
                for (byte b : bytes) {
                    encoded.append('%');
                    int unsigned = b & 0xFF;
                    if (unsigned < 0x10) {
                        encoded.append('0');
                    }
                    encoded.append(Integer.toHexString(unsigned).toUpperCase(Locale.ROOT));
                }
            }
            i += Character.charCount(codePoint);
        }

        return encoded.toString();
    }

    /**
     * Encodes a multi-segment path-parameter value, preserving both existing escape sequences and raw slashes.
     * <p>
     * This is used for path parameters that are explicitly marked as spanning multiple URL segments.
     */
    static String encodePathParamValuePreservingEscapesPreservingSlashes(String value) {
        if (value == null) {
            return null;
        }

        StringBuilder encoded = new StringBuilder(value.length());
        for (int i = 0; i < value.length();) {
            char ch = value.charAt(i);
            if (ch == '/') {
                encoded.append(ch);
                i++;
                continue;
            }
            if (ch == '%' && i + 2 < value.length() && isHexDigit(value.charAt(i + 1)) && isHexDigit(value.charAt(i + 2))) {
                encoded.append(ch).append(value.charAt(i + 1)).append(value.charAt(i + 2));
                i += 3;
                continue;
            }

            int codePoint = value.codePointAt(i);
            if (isUnreserved(codePoint)) {
                encoded.appendCodePoint(codePoint);
            } else {
                byte[] bytes = new String(Character.toChars(codePoint)).getBytes(StandardCharsets.UTF_8);
                for (byte b : bytes) {
                    encoded.append('%');
                    int unsigned = b & 0xFF;
                    if (unsigned < 0x10) {
                        encoded.append('0');
                    }
                    encoded.append(Integer.toHexString(unsigned).toUpperCase(Locale.ROOT));
                }
            }
            i += Character.charCount(codePoint);
        }

        return encoded.toString();
    }

    /**
     * Returns {@code true} when the parameter was generated as a path parameter that should be encoded.
     */
    private static boolean hasEncodedPathParam(Annotation[] annotations) {
        if (annotations == null) {
            return false;
        }
        for (Annotation annotation : annotations) {
            if (annotation.annotationType() == EncodedPathParam.class) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns {@code true} when the parameter was generated as a multi-segment path parameter.
     */
    private static boolean hasMultiSegmentPathParam(Annotation[] annotations) {
        if (annotations == null) {
            return false;
        }
        for (Annotation annotation : annotations) {
            if (annotation.annotationType() == MultiSegmentPathParam.class) {
                return true;
            }
        }
        return false;
    }

    /**
     * RFC 3986 unreserved characters can remain unchanged in a path segment.
     */
    private static boolean isUnreserved(int codePoint) {
        return codePoint >= 'a' && codePoint <= 'z'
                || codePoint >= 'A' && codePoint <= 'Z'
                || codePoint >= '0' && codePoint <= '9'
                || codePoint == '-' || codePoint == '.' || codePoint == '_' || codePoint == '~';
    }

    /**
     * Checks whether a character is a hexadecimal digit.
     */
    private static boolean isHexDigit(char ch) {
        return (ch >= '0' && ch <= '9')
                || (ch >= 'a' && ch <= 'f')
                || (ch >= 'A' && ch <= 'F');
    }
}
