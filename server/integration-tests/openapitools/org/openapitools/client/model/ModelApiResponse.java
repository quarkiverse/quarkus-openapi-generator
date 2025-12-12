package org.openapitools.client.model;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import com.fasterxml.jackson.annotation.JsonProperty;

@com.fasterxml.jackson.annotation.JsonIgnoreProperties(ignoreUnknown = true)
@SuppressWarnings({ "all", "unchecked", "rawtypes", "this-escape" })
public class ModelApiResponse  {

    private Integer code;
    private String type;
    private String message;

    /**
    * Get code
    * @return code
    **/
    @JsonProperty("code")
    @com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL)
    public Integer getCode() {
        return code;
    }

    /**
     * Set code
     **/
    @JsonProperty("code")
    public void setCode(Integer code) {
        this.code = code;
    }

    public ModelApiResponse code(Integer code) {
        this.code = code;
        return this;
    }

    /**
    * Get type
    * @return type
    **/
    @JsonProperty("type")
    @com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL)
    public String getType() {
        return type;
    }

    /**
     * Set type
     **/
    @JsonProperty("type")
    public void setType(String type) {
        this.type = type;
    }

    public ModelApiResponse type(String type) {
        this.type = type;
        return this;
    }

    /**
    * Get message
    * @return message
    **/
    @JsonProperty("message")
    @com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL)
    public String getMessage() {
        return message;
    }

    /**
     * Set message
     **/
    @JsonProperty("message")
    public void setMessage(String message) {
        this.message = message;
    }

    public ModelApiResponse message(String message) {
        this.message = message;
        return this;
    }

    /**
     * Create a string representation of this pojo.
     **/
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class ModelApiResponse {\n");

        sb.append("    code: ").append(toIndentedString(code)).append("\n");
        sb.append("    type: ").append(toIndentedString(type)).append("\n");
        sb.append("    message: ").append(toIndentedString(message)).append("\n");
        
        sb.append("}");
        return sb.toString();
    }

    /**
     * Compares this object to the specified object. The result is
     * {@code true} if and only if the argument is not
     * {@code null} and is a {@code ModelApiResponse} object that
     * contains the same values as this object.
     *
     * @param   obj   the object to compare with.
     * @return  {@code true} if the objects are the same;
     *          {@code false} otherwise.
     **/
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        ModelApiResponse model = (ModelApiResponse) obj;

        return java.util.Objects.equals(code, model.code) &&
        java.util.Objects.equals(type, model.type) &&
        java.util.Objects.equals(message, model.message);
    }

    /**
     * Returns a hash code for a {@code ModelApiResponse}.
     *
     * @return a hash code value for a {@code ModelApiResponse}.
     **/
    @Override
    public int hashCode() {
        return java.util.Objects.hash(code,
        type,
        message);
    }

    /**
     * Convert the given object to string with each line indented by 4 spaces
     * (except the first line).
     */
    private static String toIndentedString(Object o) {
        if (o == null) {
            return "null";
        }
        return o.toString().replace("\n", "\n    ");
    }

    @com.fasterxml.jackson.annotation.JsonIgnoreProperties(ignoreUnknown = true)
@SuppressWarnings({ "all", "unchecked", "rawtypes", "this-escape" })
    public static class ModelApiResponseQueryParam  {

        @jakarta.ws.rs.QueryParam("code")
        private Integer code;
        @jakarta.ws.rs.QueryParam("type")
        private String type;
        @jakarta.ws.rs.QueryParam("message")
        private String message;

        /**
        * Get code
        * @return code
        **/
        @com.fasterxml.jackson.annotation.JsonProperty("code")
        public Integer getCode() {
            return code;
        }

        /**
         * Set code
         **/
        @com.fasterxml.jackson.annotation.JsonProperty("code")
        public void setCode(Integer code) {
            this.code = code;
        }

        public ModelApiResponseQueryParam code(Integer code) {
            this.code = code;
            return this;
        }

        /**
        * Get type
        * @return type
        **/
        @com.fasterxml.jackson.annotation.JsonProperty("type")
        public String getType() {
            return type;
        }

        /**
         * Set type
         **/
        @com.fasterxml.jackson.annotation.JsonProperty("type")
        public void setType(String type) {
            this.type = type;
        }

        public ModelApiResponseQueryParam type(String type) {
            this.type = type;
            return this;
        }

        /**
        * Get message
        * @return message
        **/
        @com.fasterxml.jackson.annotation.JsonProperty("message")
        public String getMessage() {
            return message;
        }

        /**
         * Set message
         **/
        @com.fasterxml.jackson.annotation.JsonProperty("message")
        public void setMessage(String message) {
            this.message = message;
        }

        public ModelApiResponseQueryParam message(String message) {
            this.message = message;
            return this;
        }

        /**
         * Create a string representation of this pojo.
         **/
        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("class ModelApiResponseQueryParam {\n");

            sb.append("    code: ").append(toIndentedString(code)).append("\n");
            sb.append("    type: ").append(toIndentedString(type)).append("\n");
            sb.append("    message: ").append(toIndentedString(message)).append("\n");
            sb.append("}");
            return sb.toString();
        }

        /**
         * Convert the given object to string with each line indented by 4 spaces
         * (except the first line).
         */
        private static String toIndentedString(Object o) {
            if (o == null) {
                return "null";
            }
            return o.toString().replace("\n", "\n    ");
        }
    }}
