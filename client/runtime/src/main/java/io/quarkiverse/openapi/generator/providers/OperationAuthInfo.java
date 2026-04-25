package io.quarkiverse.openapi.generator.providers;

import java.util.Objects;
import java.util.Set;

public final class OperationAuthInfo {

    private String operationId = "";
    private String path;
    private String httpMethod;
    private UrlPatternMatcher pathMatcher;
    private Set<String> multiSegmentParams = Set.of();

    public OperationAuthInfo() {
    }

    public void setOperationId(final String operationId) {
        this.operationId = operationId;
    }

    public void setPath(final String path) {
        this.pathMatcher = new UrlPatternMatcher(path, multiSegmentParams);
        this.path = path;
    }

    public void setMultiSegmentParams(final Set<String> params) {
        this.multiSegmentParams = params != null ? params : Set.of();
        // Recreate matcher if path was already set
        if (this.path != null) {
            this.pathMatcher = new UrlPatternMatcher(this.path, this.multiSegmentParams);
        }
    }

    public Set<String> getMultiSegmentParams() {
        return multiSegmentParams;
    }

    public void setHttpMethod(final String method) {
        this.httpMethod = method;
    }

    public String getHttpMethod() {
        return httpMethod;
    }

    public String getOperationId() {
        return operationId;
    }

    public String getPath() {
        return path;
    }

    public boolean matchPath(final String requestPath) {
        if (this.pathMatcher == null) {
            throw new IllegalStateException("PathMatcher hasn't been initialized for operation " + operationId
                    + " set it's path first before trying to match paths.");
        }
        return this.pathMatcher.matches(requestPath);
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        OperationAuthInfo that = (OperationAuthInfo) o;
        return operationId.equals(that.operationId) && path.equals(that.path) && httpMethod.equals(that.httpMethod)
                && multiSegmentParams.equals(that.multiSegmentParams);
    }

    @Override
    public int hashCode() {
        return Objects.hash(operationId, path, httpMethod, multiSegmentParams);
    }

    public static final class Builder {

        private final OperationAuthInfo operationAuthInfo = new OperationAuthInfo();

        public Builder withId(final String operationId) {
            this.operationAuthInfo.operationId = operationId;
            return this;
        }

        public Builder withPath(final String path) {
            this.operationAuthInfo.path = path;
            return this;
        }

        public Builder withMultiSegmentParams(final Set<String> params) {
            this.operationAuthInfo.multiSegmentParams = params != null ? params : Set.of();
            return this;
        }

        public Builder withMethod(final String method) {
            this.operationAuthInfo.httpMethod = method;
            return this;
        }

        public OperationAuthInfo build() {
            if (this.operationAuthInfo.path != null) {
                this.operationAuthInfo.pathMatcher = new UrlPatternMatcher(
                        this.operationAuthInfo.path,
                        this.operationAuthInfo.multiSegmentParams);
            }
            return this.operationAuthInfo;
        }
    }

}
