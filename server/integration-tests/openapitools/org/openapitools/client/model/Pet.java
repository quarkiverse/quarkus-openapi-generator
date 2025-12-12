package org.openapitools.client.model;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.openapitools.client.model.Category;
import org.openapitools.client.model.Tag;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import com.fasterxml.jackson.annotation.JsonProperty;

@com.fasterxml.jackson.annotation.JsonIgnoreProperties(ignoreUnknown = true)
@SuppressWarnings({ "all", "unchecked", "rawtypes", "this-escape" })
public class Pet  {

    private Long id;
    private String name;
    private Category category;
    private List<String> photoUrls = new ArrayList<>();
    private List<@Valid Tag> tags;

    public enum StatusEnum {
        AVAILABLE(String.valueOf("available")), PENDING(String.valueOf("pending")), SOLD(String.valueOf("sold"));

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
      * pet status in the store
     **/
    private StatusEnum status;

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

    public Pet id(Long id) {
        this.id = id;
        return this;
    }

    /**
    * Get name
    * @return name
    **/
    @JsonProperty("name")
    public String getName() {
        return name;
    }

    /**
     * Set name
     **/
    @JsonProperty("name")
    public void setName(String name) {
        this.name = name;
    }

    public Pet name(String name) {
        this.name = name;
        return this;
    }

    /**
    * Get category
    * @return category
    **/
    @JsonProperty("category")
    @com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL)
    public Category getCategory() {
        return category;
    }

    /**
     * Set category
     **/
    @JsonProperty("category")
    public void setCategory(Category category) {
        this.category = category;
    }

    public Pet category(Category category) {
        this.category = category;
        return this;
    }

    /**
    * Get photoUrls
    * @return photoUrls
    **/
    @JsonProperty("photoUrls")
    public List<String> getPhotoUrls() {
        return photoUrls;
    }

    /**
     * Set photoUrls
     **/
    @JsonProperty("photoUrls")
    public void setPhotoUrls(List<String> photoUrls) {
        this.photoUrls = photoUrls;
    }

    public Pet photoUrls(List<String> photoUrls) {
        this.photoUrls = photoUrls;
        return this;
    }
    public Pet addPhotoUrlsItem(String photoUrlsItem) {
        if (this.photoUrls == null){
            photoUrls = new ArrayList<>();
        }
        this.photoUrls.add(photoUrlsItem);
        return this;
    }

    /**
    * Get tags
    * @return tags
    **/
    @JsonProperty("tags")
    @com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL)
    public List<@Valid Tag> getTags() {
        return tags;
    }

    /**
     * Set tags
     **/
    @JsonProperty("tags")
    public void setTags(List<@Valid Tag> tags) {
        this.tags = tags;
    }

    public Pet tags(List<@Valid Tag> tags) {
        this.tags = tags;
        return this;
    }
    public Pet addTagsItem(Tag tagsItem) {
        if (this.tags == null){
            tags = new ArrayList<>();
        }
        this.tags.add(tagsItem);
        return this;
    }

    /**
    * pet status in the store
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

    public Pet status(StatusEnum status) {
        this.status = status;
        return this;
    }

    /**
     * Create a string representation of this pojo.
     **/
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class Pet {\n");

        sb.append("    id: ").append(toIndentedString(id)).append("\n");
        sb.append("    name: ").append(toIndentedString(name)).append("\n");
        sb.append("    category: ").append(toIndentedString(category)).append("\n");
        sb.append("    photoUrls: ").append(toIndentedString(photoUrls)).append("\n");
        sb.append("    tags: ").append(toIndentedString(tags)).append("\n");
        sb.append("    status: ").append(toIndentedString(status)).append("\n");
        
        sb.append("}");
        return sb.toString();
    }

    /**
     * Compares this object to the specified object. The result is
     * {@code true} if and only if the argument is not
     * {@code null} and is a {@code Pet} object that
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

        Pet model = (Pet) obj;

        return java.util.Objects.equals(id, model.id) &&
        java.util.Objects.equals(name, model.name) &&
        java.util.Objects.equals(category, model.category) &&
        java.util.Objects.equals(photoUrls, model.photoUrls) &&
        java.util.Objects.equals(tags, model.tags) &&
        java.util.Objects.equals(status, model.status);
    }

    /**
     * Returns a hash code for a {@code Pet}.
     *
     * @return a hash code value for a {@code Pet}.
     **/
    @Override
    public int hashCode() {
        return java.util.Objects.hash(id,
        name,
        category,
        photoUrls,
        tags,
        status);
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
    public static class PetQueryParam  {

        @jakarta.ws.rs.QueryParam("id")
        private Long id;
        @jakarta.ws.rs.QueryParam("name")
        private String name;
        @jakarta.ws.rs.QueryParam("category")
        private Category category;
        @jakarta.ws.rs.QueryParam("photoUrls")
        private List<String> photoUrls = new ArrayList<>();
        @jakarta.ws.rs.QueryParam("tags")
        private List<@Valid Tag> tags = null;

    public enum StatusEnum {
        AVAILABLE(String.valueOf("available")), PENDING(String.valueOf("pending")), SOLD(String.valueOf("sold"));

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

        public PetQueryParam id(Long id) {
            this.id = id;
            return this;
        }

        /**
        * Get name
        * @return name
        **/
        @com.fasterxml.jackson.annotation.JsonProperty("name")
        public String getName() {
            return name;
        }

        /**
         * Set name
         **/
        @com.fasterxml.jackson.annotation.JsonProperty("name")
        public void setName(String name) {
            this.name = name;
        }

        public PetQueryParam name(String name) {
            this.name = name;
            return this;
        }

        /**
        * Get category
        * @return category
        **/
        @com.fasterxml.jackson.annotation.JsonProperty("category")
        public Category getCategory() {
            return category;
        }

        /**
         * Set category
         **/
        @com.fasterxml.jackson.annotation.JsonProperty("category")
        public void setCategory(Category category) {
            this.category = category;
        }

        public PetQueryParam category(Category category) {
            this.category = category;
            return this;
        }

        /**
        * Get photoUrls
        * @return photoUrls
        **/
        @com.fasterxml.jackson.annotation.JsonProperty("photoUrls")
        public List<String> getPhotoUrls() {
            return photoUrls;
        }

        /**
         * Set photoUrls
         **/
        @com.fasterxml.jackson.annotation.JsonProperty("photoUrls")
        public void setPhotoUrls(List<String> photoUrls) {
            this.photoUrls = photoUrls;
        }

        public PetQueryParam photoUrls(List<String> photoUrls) {
            this.photoUrls = photoUrls;
            return this;
        }
        public PetQueryParam addPhotoUrlsItem(String photoUrlsItem) {
            this.photoUrls.add(photoUrlsItem);
            return this;
        }

        /**
        * Get tags
        * @return tags
        **/
        @com.fasterxml.jackson.annotation.JsonProperty("tags")
        public List<@Valid Tag> getTags() {
            return tags;
        }

        /**
         * Set tags
         **/
        @com.fasterxml.jackson.annotation.JsonProperty("tags")
        public void setTags(List<@Valid Tag> tags) {
            this.tags = tags;
        }

        public PetQueryParam tags(List<@Valid Tag> tags) {
            this.tags = tags;
            return this;
        }
        public PetQueryParam addTagsItem(Tag tagsItem) {
            this.tags.add(tagsItem);
            return this;
        }

        /**
        * pet status in the store
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

        public PetQueryParam status(StatusEnum status) {
            this.status = status;
            return this;
        }

        /**
         * Create a string representation of this pojo.
         **/
        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("class PetQueryParam {\n");

            sb.append("    id: ").append(toIndentedString(id)).append("\n");
            sb.append("    name: ").append(toIndentedString(name)).append("\n");
            sb.append("    category: ").append(toIndentedString(category)).append("\n");
            sb.append("    photoUrls: ").append(toIndentedString(photoUrls)).append("\n");
            sb.append("    tags: ").append(toIndentedString(tags)).append("\n");
            sb.append("    status: ").append(toIndentedString(status)).append("\n");
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
