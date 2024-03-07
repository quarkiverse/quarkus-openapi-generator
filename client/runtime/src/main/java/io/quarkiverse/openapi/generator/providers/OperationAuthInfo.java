package io.quarkiverse.openapi.generator.providers;

import java.util.Objects;

public final class OperationAuthInfo {

    private String operationId = "";
    private String path;
    private String httpMethod;
    private UrlPatternMatcher pathMatcher;

    public OperationAuthInfo() {
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
        return operationId.equals(that.operationId) && path.equals(that.path) && httpMethod.equals(that.httpMethod);
    }

    @Override
    public int hashCode() {
        return Objects.hash(operationId, path, httpMethod);
    }

    public static final class Builder {

        private final OperationAuthInfo operationAuthInfo = new OperationAuthInfo();

        public Builder withId(final String operationId) {
            this.operationAuthInfo.operationId = operationId;
            return this;
        }

        public Builder withPath(final String path) {
            this.operationAuthInfo.pathMatcher = new UrlPatternMatcher(path);
            this.operationAuthInfo.path = path;
            return this;
        }

        public Builder withMethod(final String method) {
            this.operationAuthInfo.httpMethod = method;
            return this;
        }

        public OperationAuthInfo build() {
            return this.operationAuthInfo;
        }
    }

}
