package org.openapitools.client.model;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import com.fasterxml.jackson.annotation.JsonProperty;

@com.fasterxml.jackson.annotation.JsonIgnoreProperties(ignoreUnknown = true)
@SuppressWarnings({ "all", "unchecked", "rawtypes", "this-escape" })
public class Order  {

    private Long id;
    private Long petId;
    private Integer quantity;
    private Date shipDate;

    public enum StatusEnum {
        PLACED(String.valueOf("placed")), APPROVED(String.valueOf("approved")), DELIVERED(String.valueOf("delivered"));

        // caching enum access
        private static final java.util.EnumSet<StatusEnum> values = java.util.EnumSet.allOf(StatusEnum.class);

        String value;

        StatusEnum (String v) {
            value = v;
        }

        @com.fasterxml.jackson.annotation.JsonValue
        public String value() {
            return value;
        }

        @Override
        public String toString() {
            return String.valueOf(value);
        }

        @com.fasterxml.jackson.annotation.JsonCreator
        public static StatusEnum fromString(String v) {
            for (StatusEnum b : values) {
                if (String.valueOf(b.value).equalsIgnoreCase(v)) {
                    return b;
                }
            }
            throw new IllegalArgumentException("Unexpected value '" + v + "'");
        }
    }
    /**
      * Order Status
     **/
    private StatusEnum status;
    private Boolean complete;

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

    public Order id(Long id) {
        this.id = id;
        return this;
    }

    /**
    * Get petId
    * @return petId
    **/
    @JsonProperty("petId")
    @com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL)
    public Long getPetId() {
        return petId;
    }

    /**
     * Set petId
     **/
    @JsonProperty("petId")
    public void setPetId(Long petId) {
        this.petId = petId;
    }

    public Order petId(Long petId) {
        this.petId = petId;
        return this;
    }

    /**
    * Get quantity
    * @return quantity
    **/
    @JsonProperty("quantity")
    @com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL)
    public Integer getQuantity() {
        return quantity;
    }

    /**
     * Set quantity
     **/
    @JsonProperty("quantity")
    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Order quantity(Integer quantity) {
        this.quantity = quantity;
        return this;
    }

    /**
    * Get shipDate
    * @return shipDate
    **/
    @JsonProperty("shipDate")
    @com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL)
    public Date getShipDate() {
        return shipDate;
    }

    /**
     * Set shipDate
     **/
    @JsonProperty("shipDate")
    public void setShipDate(Date shipDate) {
        this.shipDate = shipDate;
    }

    public Order shipDate(Date shipDate) {
        this.shipDate = shipDate;
        return this;
    }

    /**
    * Order Status
    * @return status
    **/
    @JsonProperty("status")
    @com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL)
    public StatusEnum getStatus() {
        return status;
    }

    /**
     * Set status
     **/
    @JsonProperty("status")
    public void setStatus(StatusEnum status) {
        this.status = status;
    }

    public Order status(StatusEnum status) {
        this.status = status;
        return this;
    }

    /**
    * Get complete
    * @return complete
    **/
    @JsonProperty("complete")
    @com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL)
    public Boolean getComplete() {
        return complete;
    }

    /**
     * Set complete
     **/
    @JsonProperty("complete")
    public void setComplete(Boolean complete) {
        this.complete = complete;
    }

    public Order complete(Boolean complete) {
        this.complete = complete;
        return this;
    }

    /**
     * Create a string representation of this pojo.
     **/
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class Order {\n");

        sb.append("    id: ").append(toIndentedString(id)).append("\n");
        sb.append("    petId: ").append(toIndentedString(petId)).append("\n");
        sb.append("    quantity: ").append(toIndentedString(quantity)).append("\n");
        sb.append("    shipDate: ").append(toIndentedString(shipDate)).append("\n");
        sb.append("    status: ").append(toIndentedString(status)).append("\n");
        sb.append("    complete: ").append(toIndentedString(complete)).append("\n");
        
        sb.append("}");
        return sb.toString();
    }

    /**
     * Compares this object to the specified object. The result is
     * {@code true} if and only if the argument is not
     * {@code null} and is a {@code Order} object that
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

        Order model = (Order) obj;

        return java.util.Objects.equals(id, model.id) &&
        java.util.Objects.equals(petId, model.petId) &&
        java.util.Objects.equals(quantity, model.quantity) &&
        java.util.Objects.equals(shipDate, model.shipDate) &&
        java.util.Objects.equals(status, model.status) &&
        java.util.Objects.equals(complete, model.complete);
    }

    /**
     * Returns a hash code for a {@code Order}.
     *
     * @return a hash code value for a {@code Order}.
     **/
    @Override
    public int hashCode() {
        return java.util.Objects.hash(id,
        petId,
        quantity,
        shipDate,
        status,
        complete);
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
    public static class OrderQueryParam  {

        @jakarta.ws.rs.QueryParam("id")
        private Long id;
        @jakarta.ws.rs.QueryParam("petId")
        private Long petId;
        @jakarta.ws.rs.QueryParam("quantity")
        private Integer quantity;
        @jakarta.ws.rs.QueryParam("shipDate")
        private Date shipDate;

    public enum StatusEnum {
        PLACED(String.valueOf("placed")), APPROVED(String.valueOf("approved")), DELIVERED(String.valueOf("delivered"));

        // caching enum access
        private static final java.util.EnumSet<StatusEnum> values = java.util.EnumSet.allOf(StatusEnum.class);

        String value;

        StatusEnum (String v) {
            value = v;
        }

        @com.fasterxml.jackson.annotation.JsonValue
        public String value() {
            return value;
        }

        @Override
        public String toString() {
            return String.valueOf(value);
        }

        @com.fasterxml.jackson.annotation.JsonCreator
        public static StatusEnum fromString(String v) {
            for (StatusEnum b : values) {
                if (String.valueOf(b.value).equalsIgnoreCase(v)) {
                    return b;
                }
            }
            throw new IllegalArgumentException("Unexpected value '" + v + "'");
        }
    }
        private StatusEnum status;
        @jakarta.ws.rs.QueryParam("complete")
        private Boolean complete;

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

        public OrderQueryParam id(Long id) {
            this.id = id;
            return this;
        }

        /**
        * Get petId
        * @return petId
        **/
        @com.fasterxml.jackson.annotation.JsonProperty("petId")
        public Long getPetId() {
            return petId;
        }

        /**
         * Set petId
         **/
        @com.fasterxml.jackson.annotation.JsonProperty("petId")
        public void setPetId(Long petId) {
            this.petId = petId;
        }

        public OrderQueryParam petId(Long petId) {
            this.petId = petId;
            return this;
        }

        /**
        * Get quantity
        * @return quantity
        **/
        @com.fasterxml.jackson.annotation.JsonProperty("quantity")
        public Integer getQuantity() {
            return quantity;
        }

        /**
         * Set quantity
         **/
        @com.fasterxml.jackson.annotation.JsonProperty("quantity")
        public void setQuantity(Integer quantity) {
            this.quantity = quantity;
        }

        public OrderQueryParam quantity(Integer quantity) {
            this.quantity = quantity;
            return this;
        }

        /**
        * Get shipDate
        * @return shipDate
        **/
        @com.fasterxml.jackson.annotation.JsonProperty("shipDate")
        public Date getShipDate() {
            return shipDate;
        }

        /**
         * Set shipDate
         **/
        @com.fasterxml.jackson.annotation.JsonProperty("shipDate")
        public void setShipDate(Date shipDate) {
            this.shipDate = shipDate;
        }

        public OrderQueryParam shipDate(Date shipDate) {
            this.shipDate = shipDate;
            return this;
        }

        /**
        * Order Status
        * @return status
        **/
        @com.fasterxml.jackson.annotation.JsonProperty("status")
        public StatusEnum getStatus() {
            return status;
        }

        /**
         * Set status
         **/
        @com.fasterxml.jackson.annotation.JsonProperty("status")
        public void setStatus(StatusEnum status) {
            this.status = status;
        }

        public OrderQueryParam status(StatusEnum status) {
            this.status = status;
            return this;
        }

        /**
        * Get complete
        * @return complete
        **/
        @com.fasterxml.jackson.annotation.JsonProperty("complete")
        public Boolean getComplete() {
            return complete;
        }

        /**
         * Set complete
         **/
        @com.fasterxml.jackson.annotation.JsonProperty("complete")
        public void setComplete(Boolean complete) {
            this.complete = complete;
        }

        public OrderQueryParam complete(Boolean complete) {
            this.complete = complete;
            return this;
        }

        /**
         * Create a string representation of this pojo.
         **/
        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("class OrderQueryParam {\n");

            sb.append("    id: ").append(toIndentedString(id)).append("\n");
            sb.append("    petId: ").append(toIndentedString(petId)).append("\n");
            sb.append("    quantity: ").append(toIndentedString(quantity)).append("\n");
            sb.append("    shipDate: ").append(toIndentedString(shipDate)).append("\n");
            sb.append("    status: ").append(toIndentedString(status)).append("\n");
            sb.append("    complete: ").append(toIndentedString(complete)).append("\n");
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
