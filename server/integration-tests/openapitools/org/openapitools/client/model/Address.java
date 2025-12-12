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
public class Address  {

    private String street;
    private String city;
    private String state;
    private String zip;

    /**
    * Get street
    * @return street
    **/
    @JsonProperty("street")
    @com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL)
    public String getStreet() {
        return street;
    }

    /**
     * Set street
     **/
    @JsonProperty("street")
    public void setStreet(String street) {
        this.street = street;
    }

    public Address street(String street) {
        this.street = street;
        return this;
    }

    /**
    * Get city
    * @return city
    **/
    @JsonProperty("city")
    @com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL)
    public String getCity() {
        return city;
    }

    /**
     * Set city
     **/
    @JsonProperty("city")
    public void setCity(String city) {
        this.city = city;
    }

    public Address city(String city) {
        this.city = city;
        return this;
    }

    /**
    * Get state
    * @return state
    **/
    @JsonProperty("state")
    @com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL)
    public String getState() {
        return state;
    }

    /**
     * Set state
     **/
    @JsonProperty("state")
    public void setState(String state) {
        this.state = state;
    }

    public Address state(String state) {
        this.state = state;
        return this;
    }

    /**
    * Get zip
    * @return zip
    **/
    @JsonProperty("zip")
    @com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL)
    public String getZip() {
        return zip;
    }

    /**
     * Set zip
     **/
    @JsonProperty("zip")
    public void setZip(String zip) {
        this.zip = zip;
    }

    public Address zip(String zip) {
        this.zip = zip;
        return this;
    }

    /**
     * Create a string representation of this pojo.
     **/
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class Address {\n");

        sb.append("    street: ").append(toIndentedString(street)).append("\n");
        sb.append("    city: ").append(toIndentedString(city)).append("\n");
        sb.append("    state: ").append(toIndentedString(state)).append("\n");
        sb.append("    zip: ").append(toIndentedString(zip)).append("\n");
        
        sb.append("}");
        return sb.toString();
    }

    /**
     * Compares this object to the specified object. The result is
     * {@code true} if and only if the argument is not
     * {@code null} and is a {@code Address} object that
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

        Address model = (Address) obj;

        return java.util.Objects.equals(street, model.street) &&
        java.util.Objects.equals(city, model.city) &&
        java.util.Objects.equals(state, model.state) &&
        java.util.Objects.equals(zip, model.zip);
    }

    /**
     * Returns a hash code for a {@code Address}.
     *
     * @return a hash code value for a {@code Address}.
     **/
    @Override
    public int hashCode() {
        return java.util.Objects.hash(street,
        city,
        state,
        zip);
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
    public static class AddressQueryParam  {

        @jakarta.ws.rs.QueryParam("street")
        private String street;
        @jakarta.ws.rs.QueryParam("city")
        private String city;
        @jakarta.ws.rs.QueryParam("state")
        private String state;
        @jakarta.ws.rs.QueryParam("zip")
        private String zip;

        /**
        * Get street
        * @return street
        **/
        @com.fasterxml.jackson.annotation.JsonProperty("street")
        public String getStreet() {
            return street;
        }

        /**
         * Set street
         **/
        @com.fasterxml.jackson.annotation.JsonProperty("street")
        public void setStreet(String street) {
            this.street = street;
        }

        public AddressQueryParam street(String street) {
            this.street = street;
            return this;
        }

        /**
        * Get city
        * @return city
        **/
        @com.fasterxml.jackson.annotation.JsonProperty("city")
        public String getCity() {
            return city;
        }

        /**
         * Set city
         **/
        @com.fasterxml.jackson.annotation.JsonProperty("city")
        public void setCity(String city) {
            this.city = city;
        }

        public AddressQueryParam city(String city) {
            this.city = city;
            return this;
        }

        /**
        * Get state
        * @return state
        **/
        @com.fasterxml.jackson.annotation.JsonProperty("state")
        public String getState() {
            return state;
        }

        /**
         * Set state
         **/
        @com.fasterxml.jackson.annotation.JsonProperty("state")
        public void setState(String state) {
            this.state = state;
        }

        public AddressQueryParam state(String state) {
            this.state = state;
            return this;
        }

        /**
        * Get zip
        * @return zip
        **/
        @com.fasterxml.jackson.annotation.JsonProperty("zip")
        public String getZip() {
            return zip;
        }

        /**
         * Set zip
         **/
        @com.fasterxml.jackson.annotation.JsonProperty("zip")
        public void setZip(String zip) {
            this.zip = zip;
        }

        public AddressQueryParam zip(String zip) {
            this.zip = zip;
            return this;
        }

        /**
         * Create a string representation of this pojo.
         **/
        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("class AddressQueryParam {\n");

            sb.append("    street: ").append(toIndentedString(street)).append("\n");
            sb.append("    city: ").append(toIndentedString(city)).append("\n");
            sb.append("    state: ").append(toIndentedString(state)).append("\n");
            sb.append("    zip: ").append(toIndentedString(zip)).append("\n");
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
