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
public class User  {

    private Long id;
    private String username;
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String phone;
    /**
      * User Status
     **/
    private Integer userStatus;

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

    public User id(Long id) {
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

    public User username(String username) {
        this.username = username;
        return this;
    }

    /**
    * Get firstName
    * @return firstName
    **/
    @JsonProperty("firstName")
    @com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL)
    public String getFirstName() {
        return firstName;
    }

    /**
     * Set firstName
     **/
    @JsonProperty("firstName")
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public User firstName(String firstName) {
        this.firstName = firstName;
        return this;
    }

    /**
    * Get lastName
    * @return lastName
    **/
    @JsonProperty("lastName")
    @com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL)
    public String getLastName() {
        return lastName;
    }

    /**
     * Set lastName
     **/
    @JsonProperty("lastName")
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public User lastName(String lastName) {
        this.lastName = lastName;
        return this;
    }

    /**
    * Get email
    * @return email
    **/
    @JsonProperty("email")
    @com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL)
    public String getEmail() {
        return email;
    }

    /**
     * Set email
     **/
    @JsonProperty("email")
    public void setEmail(String email) {
        this.email = email;
    }

    public User email(String email) {
        this.email = email;
        return this;
    }

    /**
    * Get password
    * @return password
    **/
    @JsonProperty("password")
    @com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL)
    public String getPassword() {
        return password;
    }

    /**
     * Set password
     **/
    @JsonProperty("password")
    public void setPassword(String password) {
        this.password = password;
    }

    public User password(String password) {
        this.password = password;
        return this;
    }

    /**
    * Get phone
    * @return phone
    **/
    @JsonProperty("phone")
    @com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL)
    public String getPhone() {
        return phone;
    }

    /**
     * Set phone
     **/
    @JsonProperty("phone")
    public void setPhone(String phone) {
        this.phone = phone;
    }

    public User phone(String phone) {
        this.phone = phone;
        return this;
    }

    /**
    * User Status
    * @return userStatus
    **/
    @JsonProperty("userStatus")
    @com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL)
    public Integer getUserStatus() {
        return userStatus;
    }

    /**
     * Set userStatus
     **/
    @JsonProperty("userStatus")
    public void setUserStatus(Integer userStatus) {
        this.userStatus = userStatus;
    }

    public User userStatus(Integer userStatus) {
        this.userStatus = userStatus;
        return this;
    }

    /**
     * Create a string representation of this pojo.
     **/
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class User {\n");

        sb.append("    id: ").append(toIndentedString(id)).append("\n");
        sb.append("    username: ").append(toIndentedString(username)).append("\n");
        sb.append("    firstName: ").append(toIndentedString(firstName)).append("\n");
        sb.append("    lastName: ").append(toIndentedString(lastName)).append("\n");
        sb.append("    email: ").append(toIndentedString(email)).append("\n");
        sb.append("    password: ").append(toIndentedString(password)).append("\n");
        sb.append("    phone: ").append(toIndentedString(phone)).append("\n");
        sb.append("    userStatus: ").append(toIndentedString(userStatus)).append("\n");
        
        sb.append("}");
        return sb.toString();
    }

    /**
     * Compares this object to the specified object. The result is
     * {@code true} if and only if the argument is not
     * {@code null} and is a {@code User} object that
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

        User model = (User) obj;

        return java.util.Objects.equals(id, model.id) &&
        java.util.Objects.equals(username, model.username) &&
        java.util.Objects.equals(firstName, model.firstName) &&
        java.util.Objects.equals(lastName, model.lastName) &&
        java.util.Objects.equals(email, model.email) &&
        java.util.Objects.equals(password, model.password) &&
        java.util.Objects.equals(phone, model.phone) &&
        java.util.Objects.equals(userStatus, model.userStatus);
    }

    /**
     * Returns a hash code for a {@code User}.
     *
     * @return a hash code value for a {@code User}.
     **/
    @Override
    public int hashCode() {
        return java.util.Objects.hash(id,
        username,
        firstName,
        lastName,
        email,
        password,
        phone,
        userStatus);
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
    public static class UserQueryParam  {

        @jakarta.ws.rs.QueryParam("id")
        private Long id;
        @jakarta.ws.rs.QueryParam("username")
        private String username;
        @jakarta.ws.rs.QueryParam("firstName")
        private String firstName;
        @jakarta.ws.rs.QueryParam("lastName")
        private String lastName;
        @jakarta.ws.rs.QueryParam("email")
        private String email;
        @jakarta.ws.rs.QueryParam("password")
        private String password;
        @jakarta.ws.rs.QueryParam("phone")
        private String phone;
        @jakarta.ws.rs.QueryParam("userStatus")
        private Integer userStatus;

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

        public UserQueryParam id(Long id) {
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

        public UserQueryParam username(String username) {
            this.username = username;
            return this;
        }

        /**
        * Get firstName
        * @return firstName
        **/
        @com.fasterxml.jackson.annotation.JsonProperty("firstName")
        public String getFirstName() {
            return firstName;
        }

        /**
         * Set firstName
         **/
        @com.fasterxml.jackson.annotation.JsonProperty("firstName")
        public void setFirstName(String firstName) {
            this.firstName = firstName;
        }

        public UserQueryParam firstName(String firstName) {
            this.firstName = firstName;
            return this;
        }

        /**
        * Get lastName
        * @return lastName
        **/
        @com.fasterxml.jackson.annotation.JsonProperty("lastName")
        public String getLastName() {
            return lastName;
        }

        /**
         * Set lastName
         **/
        @com.fasterxml.jackson.annotation.JsonProperty("lastName")
        public void setLastName(String lastName) {
            this.lastName = lastName;
        }

        public UserQueryParam lastName(String lastName) {
            this.lastName = lastName;
            return this;
        }

        /**
        * Get email
        * @return email
        **/
        @com.fasterxml.jackson.annotation.JsonProperty("email")
        public String getEmail() {
            return email;
        }

        /**
         * Set email
         **/
        @com.fasterxml.jackson.annotation.JsonProperty("email")
        public void setEmail(String email) {
            this.email = email;
        }

        public UserQueryParam email(String email) {
            this.email = email;
            return this;
        }

        /**
        * Get password
        * @return password
        **/
        @com.fasterxml.jackson.annotation.JsonProperty("password")
        public String getPassword() {
            return password;
        }

        /**
         * Set password
         **/
        @com.fasterxml.jackson.annotation.JsonProperty("password")
        public void setPassword(String password) {
            this.password = password;
        }

        public UserQueryParam password(String password) {
            this.password = password;
            return this;
        }

        /**
        * Get phone
        * @return phone
        **/
        @com.fasterxml.jackson.annotation.JsonProperty("phone")
        public String getPhone() {
            return phone;
        }

        /**
         * Set phone
         **/
        @com.fasterxml.jackson.annotation.JsonProperty("phone")
        public void setPhone(String phone) {
            this.phone = phone;
        }

        public UserQueryParam phone(String phone) {
            this.phone = phone;
            return this;
        }

        /**
        * User Status
        * @return userStatus
        **/
        @com.fasterxml.jackson.annotation.JsonProperty("userStatus")
        public Integer getUserStatus() {
            return userStatus;
        }

        /**
         * Set userStatus
         **/
        @com.fasterxml.jackson.annotation.JsonProperty("userStatus")
        public void setUserStatus(Integer userStatus) {
            this.userStatus = userStatus;
        }

        public UserQueryParam userStatus(Integer userStatus) {
            this.userStatus = userStatus;
            return this;
        }

        /**
         * Create a string representation of this pojo.
         **/
        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("class UserQueryParam {\n");

            sb.append("    id: ").append(toIndentedString(id)).append("\n");
            sb.append("    username: ").append(toIndentedString(username)).append("\n");
            sb.append("    firstName: ").append(toIndentedString(firstName)).append("\n");
            sb.append("    lastName: ").append(toIndentedString(lastName)).append("\n");
            sb.append("    email: ").append(toIndentedString(email)).append("\n");
            sb.append("    password: ").append(toIndentedString(password)).append("\n");
            sb.append("    phone: ").append(toIndentedString(phone)).append("\n");
            sb.append("    userStatus: ").append(toIndentedString(userStatus)).append("\n");
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
