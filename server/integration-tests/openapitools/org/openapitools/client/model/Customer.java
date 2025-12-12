package org.openapitools.client.model;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.openapitools.client.model.Address;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import com.fasterxml.jackson.annotation.JsonProperty;

@com.fasterxml.jackson.annotation.JsonIgnoreProperties(ignoreUnknown = true)
@SuppressWarnings({ "all", "unchecked", "rawtypes", "this-escape" })
public class Customer  {

    private Long id;
    private String username;
    private List<@Valid Address> address;

    /**
    * Get id
    * @return id
    **/
    @JsonProperty("id")
    @com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL)
    public Long getId() {
        return id;
    }

    /**
     * Set id
     **/
    @JsonProperty("id")
    public void setId(Long id) {
        this.id = id;
    }

    public Customer id(Long id) {
        this.id = id;
        return this;
    }

    /**
    * Get username
    * @return username
    **/
    @JsonProperty("username")
    @com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL)
    public String getUsername() {
        return username;
    }

    /**
     * Set username
     **/
    @JsonProperty("username")
    public void setUsername(String username) {
        this.username = username;
    }

    public Customer username(String username) {
        this.username = username;
        return this;
    }

    /**
    * Get address
    * @return address
    **/
    @JsonProperty("address")
    @com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL)
    public List<@Valid Address> getAddress() {
        return address;
    }

    /**
     * Set address
     **/
    @JsonProperty("address")
    public void setAddress(List<@Valid Address> address) {
        this.address = address;
    }

    public Customer address(List<@Valid Address> address) {
        this.address = address;
        return this;
    }
    public Customer addAddressItem(Address addressItem) {
        if (this.address == null){
            address = new ArrayList<>();
        }
        this.address.add(addressItem);
        return this;
    }

    /**
     * Create a string representation of this pojo.
     **/
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class Customer {\n");

        sb.append("    id: ").append(toIndentedString(id)).append("\n");
        sb.append("    username: ").append(toIndentedString(username)).append("\n");
        sb.append("    address: ").append(toIndentedString(address)).append("\n");
        
        sb.append("}");
        return sb.toString();
    }

    /**
     * Compares this object to the specified object. The result is
     * {@code true} if and only if the argument is not
     * {@code null} and is a {@code Customer} object that
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

        Customer model = (Customer) obj;

        return java.util.Objects.equals(id, model.id) &&
        java.util.Objects.equals(username, model.username) &&
        java.util.Objects.equals(address, model.address);
    }

    /**
     * Returns a hash code for a {@code Customer}.
     *
     * @return a hash code value for a {@code Customer}.
     **/
    @Override
    public int hashCode() {
        return java.util.Objects.hash(id,
        username,
        address);
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
    public static class CustomerQueryParam  {

        @jakarta.ws.rs.QueryParam("id")
        private Long id;
        @jakarta.ws.rs.QueryParam("username")
        private String username;
        @jakarta.ws.rs.QueryParam("address")
        private List<@Valid Address> address = null;

        /**
        * Get id
        * @return id
        **/
        @com.fasterxml.jackson.annotation.JsonProperty("id")
        public Long getId() {
            return id;
        }

        /**
         * Set id
         **/
        @com.fasterxml.jackson.annotation.JsonProperty("id")
        public void setId(Long id) {
            this.id = id;
        }

        public CustomerQueryParam id(Long id) {
            this.id = id;
            return this;
        }

        /**
        * Get username
        * @return username
        **/
        @com.fasterxml.jackson.annotation.JsonProperty("username")
        public String getUsername() {
            return username;
        }

        /**
         * Set username
         **/
        @com.fasterxml.jackson.annotation.JsonProperty("username")
        public void setUsername(String username) {
            this.username = username;
        }

        public CustomerQueryParam username(String username) {
            this.username = username;
            return this;
        }

        /**
        * Get address
        * @return address
        **/
        @com.fasterxml.jackson.annotation.JsonProperty("address")
        public List<@Valid Address> getAddress() {
            return address;
        }

        /**
         * Set address
         **/
        @com.fasterxml.jackson.annotation.JsonProperty("address")
        public void setAddress(List<@Valid Address> address) {
            this.address = address;
        }

        public CustomerQueryParam address(List<@Valid Address> address) {
            this.address = address;
            return this;
        }
        public CustomerQueryParam addAddressItem(Address addressItem) {
            this.address.add(addressItem);
            return this;
        }

        /**
         * Create a string representation of this pojo.
         **/
        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("class CustomerQueryParam {\n");

            sb.append("    id: ").append(toIndentedString(id)).append("\n");
            sb.append("    username: ").append(toIndentedString(username)).append("\n");
            sb.append("    address: ").append(toIndentedString(address)).append("\n");
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
