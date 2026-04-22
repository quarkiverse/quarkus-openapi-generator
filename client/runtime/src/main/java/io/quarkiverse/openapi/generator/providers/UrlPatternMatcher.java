/*
  Original Copyright Headers
  This file include a substantially simplified version of
  https://github.com/wilkincheung/URI-Template-Pattern-Matcher/blob/master/src/main/java/com/prodigi/service/UriTemplateValidator.java
 */
/**
The MIT License (MIT)
Copyright (c) 2015 Wilkin Cheung

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
*/
package io.quarkiverse.openapi.generator.providers;

import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UrlPatternMatcher {

    // For each pattern {keyName} replaces it with ([^/]*) or (.*)
    private static final Pattern LEVEL_ONE_PATTERN = Pattern.compile("\\{([^/]+?)\\}");
    private static final String URL_QUERY_STRING_REGEX = "(?:\\?.*?)?$";

    private final Pattern pattern;

    /**
     * Creates a URL pattern matcher with per-parameter segment control.
     *
     * @param uriTemplate The URI template with {param} placeholders
     * @param multiSegmentParams Set of parameter names that can match across slashes.
     *        Parameters in this set use (.*) regex.
     *        All other parameters use ([^/]*) for security.
     */
    public UrlPatternMatcher(String uriTemplate, Set<String> multiSegmentParams) {
        Set<String> multiSegParams = multiSegmentParams != null ? multiSegmentParams : Set.of();

        StringBuilder patternBuilder = new StringBuilder();
        Matcher m = LEVEL_ONE_PATTERN.matcher(uriTemplate);
        int end = 0;

        while (m.find()) {
            // Extract parameter name from {paramName}
            String paramName = m.group(1);

            // Choose regex based on whether this param is multi-segment
            String replacement;
            if (multiSegParams.contains(paramName)) {
                replacement = "(.*)"; // Multi-segment: matches across slashes (greedy)
            } else {
                replacement = "([^/]*)"; // Single-segment: secure default
            }

            patternBuilder.append(Pattern.quote(uriTemplate.substring(end, m.start())))
                    .append(replacement);
            end = m.end();
        }

        patternBuilder.append(Pattern.quote(uriTemplate.substring(end)));
        this.pattern = Pattern.compile(patternBuilder + URL_QUERY_STRING_REGEX);
    }

    /**
     * Backwards compatibility: existing callers get secure single-segment behavior.
     */
    public UrlPatternMatcher(String uriTemplate) {
        this(uriTemplate, Set.of());
    }

    /**
     * Test the given URL against the underlying pattern to determine if it matches, returning a boolean
     * to reflect the outcome.
     *
     * @param url an URL string with or without query string.
     * @return true if the given URL matches the underlying pattern. Otherwise false.
     */
    public boolean matches(String url) {
        return pattern.matcher(url).matches();
    }

    public String toString() {
        return pattern.toString();
    }
}
