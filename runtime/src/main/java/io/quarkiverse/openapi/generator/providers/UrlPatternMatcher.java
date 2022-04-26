/*
  Original Copyright Headers
  This file has been modified, but copied from
  https://github.com/RestExpress/RestExpress/blob/master/core/src/main/java/org/restexpress/url/UrlPattern.java
 */
/**
 * Copyright 2010, Strategic Gains, Inc.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.quarkiverse.openapi.generator.providers;

import java.util.regex.Pattern;

/**
 * PathPatternMatcher leverages Regex Pattern to represent a parameterized URL. Parameters within the URL are
 * denoted by curly braces '{}' with the parameter name contained within (e.g. '{userid}').
 * <p>
 * <p/>
 * Parameter names must be formed of word characters (e.g. A-Z, a-z, 0-9, '_').
 * <p/>
 * An optional format parameter following a dot ('.') may be added to the end. While it could be named any valid parameter name,
 * RestExpress offers special handling (e.g. within the Request, etc.) if it's named 'format'.
 * <p/>
 * Note that the format specifier allows only word characters and percent-encoded characters.
 * <p>
 * <p/>
 * URL Pattern examples:
 * <ul>
 * <li>/api/search.{format}</li>
 * <li>/api/search/users/{userid}.{format}</li>
 * <li>/api/{version}/search/users/{userid}</li>
 * </ul>
 * <p>
 * RestExpress parses URI paths which is described in the URI Generic Syntax IETF RFC 3986 specification,
 * section 3.3 (http://tools.ietf.org/html/rfc3986#section-3.3). RestExpress parses paths into segments
 * separated by slashes ("/"), the segments of which are composed of unreserved, percent encoded,
 * sub-delimiters, colon (":") or ampersand ("@"), each of which are defined below (from the spec):
 * <p/>
 * pct-encoded = "%" HEXDIG HEXDIG
 * <p/>
 * unreserved = ALPHA / DIGIT / "-" / "." / "_" / "~"<br/>
 * reserved = gen-delims / sub-delims<br/>
 * gen-delims = ":" / "/" / "?" / "#" / "[" / "]" / "@"</br>
 * sub-delims = "!" / "$" / "&" / "'" / "(" / ")" / "*" / "+" / "," / ";" / "=" *
 * <p/>
 * In other words, RestExpress accepts path segments containing: [A-Z] [a-z] [0-9] % - . _ ~ ! $ & ' ( ) * + , ; = : @
 * <p/>
 * RestExpress also accepts square brackets ('[' and ']'), but this is deprecated and not recommended.
 *
 * @author toddf
 * @see <a href="Uniform Resource Identifier (URI): Generic Syntax">http://www.ietf.org/rfc/rfc3986.txt</a>
 * @since Apr 28, 2010
 */
public class UrlPatternMatcher {
    // Finds parameters in the URL pattern string.
    private static final String URL_PARAM_REGEX = "\\{(\\w*?)\\}";

    // Replaces parameter names in the URL pattern string to match parameters in URLs.
    private static final String URL_PARAM_MATCH_REGEX = "\\([%\\\\w-.\\\\~!\\$&'\\\\(\\\\)\\\\*\\\\+,;=:\\\\[\\\\]@]+?\\)";

    // Finds the 'format' portion of the URL pattern string.
    private static final String URL_FORMAT_REGEX = "(?:\\.\\{format\\})$";

    // Replaces the format parameter name in the URL pattern string to match the format specifier in URLs. Appended to the end of the regex string
    // when a URL pattern contains a format parameter.
    private static final String URL_FORMAT_MATCH_REGEX = "(?:\\\\.\\([\\\\w%]+?\\))?";

    // Finds the query string portion within a URL. Appended to the end of the built-up regex string.
    private static final String URL_QUERY_STRING_REGEX = "(?:\\?.*?)?$";

    /**
     * The URL pattern describing the URL layout and any parameters.
     */
    private final String urlPattern;

    /**
     * A compiled regex created from the urlPattern, above.
     */
    private Pattern compiledUrl;

    /**
     * @param pattern
     */
    public UrlPatternMatcher(String pattern) {
        this.urlPattern = pattern;
        String parsedPattern = this.urlPattern.replaceFirst(URL_FORMAT_REGEX, URL_FORMAT_MATCH_REGEX);
        parsedPattern = parsedPattern.replaceAll(URL_PARAM_REGEX, URL_PARAM_MATCH_REGEX);
        this.compiledUrl = Pattern.compile(parsedPattern + URL_QUERY_STRING_REGEX);
    }

    /**
     * Test the given URL against the underlying pattern to determine if it matches, returning a boolean
     * to reflect the outcome.
     *
     * @param url an URL string with or without query string.
     * @return true if the given URL matches the underlying pattern. Otherwise false.
     */
    public boolean matches(String url) {
        return compiledUrl.matcher(url).matches();
    }
}