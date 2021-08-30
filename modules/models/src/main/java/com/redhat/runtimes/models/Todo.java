package com.redhat.runtimes.models;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.time.LocalDateTime;
import java.util.UUID;
import org.openapitools.jackson.nullable.JsonNullable;
import java.io.Serializable;
import javax.validation.Valid;
import javax.validation.constraints.*;

/**
 * A Todo list item
 */
@ApiModel(description = "A Todo list item")
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2021-08-30T08:53:45.674233-04:00[America/New_York]")
@javax.persistence.Entity
@javax.persistence.Table(name = "todos")
public class Todo  implements Serializable {
  private static final long serialVersionUID = 1L;

  @JsonProperty("id")
  @javax.persistence.Id
  @javax.persistence.GeneratedValue(generator = "UUID")
  @javax.persistence.Column(name = "id", updatable = false, nullable = false)
  private UUID id;

  @JsonProperty("title")
  private String title;

  @JsonProperty("description")
  @javax.persistence.Column(columnDefinition = "TEXT")
  private String description;

  @JsonProperty("created")
  @javax.persistence.Column(name = "created", updatable = false, nullable = false)
  private LocalDateTime created;

  @JsonProperty("dueDate")
  private LocalDateTime dueDate;

  @JsonProperty("complete")
  private Boolean complete;

  @JsonProperty("author")
  private String author;

  public Todo id(UUID id) {
    this.id = id;
    return this;
  }

  /**
   * Get id
   * @return id
  */
  @ApiModelProperty(value = "")

  @Valid

  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public Todo title(String title) {
    this.title = title;
    return this;
  }

  /**
   * Get title
   * @return title
  */
  @ApiModelProperty(required = true, value = "")
  @NotNull


  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public Todo description(String description) {
    this.description = description;
    return this;
  }

  /**
   * Get description
   * @return description
  */
  @ApiModelProperty(value = "")


  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public Todo created(LocalDateTime created) {
    this.created = created;
    return this;
  }

  /**
   * Get created
   * @return created
  */
  @ApiModelProperty(readOnly = true, value = "")

  @Valid

  public LocalDateTime getCreated() {
    return created;
  }

  public void setCreated(LocalDateTime created) {
    this.created = created;
  }

  public Todo dueDate(LocalDateTime dueDate) {
    this.dueDate = dueDate;
    return this;
  }

  /**
   * Get dueDate
   * @return dueDate
  */
  @ApiModelProperty(value = "")

  @Valid

  public LocalDateTime getDueDate() {
    return dueDate;
  }

  public void setDueDate(LocalDateTime dueDate) {
    this.dueDate = dueDate;
  }

  public Todo complete(Boolean complete) {
    this.complete = complete;
    return this;
  }

  /**
   * Get complete
   * @return complete
  */
  @ApiModelProperty(value = "")


  public Boolean getComplete() {
    return complete;
  }

  public void setComplete(Boolean complete) {
    this.complete = complete;
  }

  public Todo author(String author) {
    this.author = author;
    return this;
  }

  /**
   * Get author
   * @return author
  */
  @ApiModelProperty(readOnly = true, value = "")


  public String getAuthor() {
    return author;
  }

  public void setAuthor(String author) {
    this.author = author;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Todo todo = (Todo) o;
    return Objects.equals(this.id, todo.id) &&
        Objects.equals(this.title, todo.title) &&
        Objects.equals(this.description, todo.description) &&
        Objects.equals(this.created, todo.created) &&
        Objects.equals(this.dueDate, todo.dueDate) &&
        Objects.equals(this.complete, todo.complete) &&
        Objects.equals(this.author, todo.author);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, title, description, created, dueDate, complete, author);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Todo {\n");
    
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    title: ").append(toIndentedString(title)).append("\n");
    sb.append("    description: ").append(toIndentedString(description)).append("\n");
    sb.append("    created: ").append(toIndentedString(created)).append("\n");
    sb.append("    dueDate: ").append(toIndentedString(dueDate)).append("\n");
    sb.append("    complete: ").append(toIndentedString(complete)).append("\n");
    sb.append("    author: ").append(toIndentedString(author)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private String toIndentedString(Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }
}

