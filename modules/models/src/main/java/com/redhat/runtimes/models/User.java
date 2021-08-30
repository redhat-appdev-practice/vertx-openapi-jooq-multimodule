package com.redhat.runtimes.models;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.openapitools.jackson.nullable.JsonNullable;
import java.io.Serializable;
import javax.validation.Valid;
import javax.validation.constraints.*;

/**
 * User information
 */
@ApiModel(description = "User information")
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2021-08-30T08:53:45.674233-04:00[America/New_York]")
public class User  implements Serializable {
  private static final long serialVersionUID = 1L;

  @JsonProperty("family_name")
  private String familyName;

  @JsonProperty("given_name")
  private String givenName;

  @JsonProperty("name")
  private String name;

  @JsonProperty("preferred_username")
  private String preferredUsername;

  public User familyName(String familyName) {
    this.familyName = familyName;
    return this;
  }

  /**
   * Get familyName
   * @return familyName
  */
  @ApiModelProperty(value = "")


  public String getFamilyName() {
    return familyName;
  }

  public void setFamilyName(String familyName) {
    this.familyName = familyName;
  }

  public User givenName(String givenName) {
    this.givenName = givenName;
    return this;
  }

  /**
   * Get givenName
   * @return givenName
  */
  @ApiModelProperty(value = "")


  public String getGivenName() {
    return givenName;
  }

  public void setGivenName(String givenName) {
    this.givenName = givenName;
  }

  public User name(String name) {
    this.name = name;
    return this;
  }

  /**
   * Get name
   * @return name
  */
  @ApiModelProperty(value = "")


  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public User preferredUsername(String preferredUsername) {
    this.preferredUsername = preferredUsername;
    return this;
  }

  /**
   * Get preferredUsername
   * @return preferredUsername
  */
  @ApiModelProperty(value = "")


  public String getPreferredUsername() {
    return preferredUsername;
  }

  public void setPreferredUsername(String preferredUsername) {
    this.preferredUsername = preferredUsername;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    User user = (User) o;
    return Objects.equals(this.familyName, user.familyName) &&
        Objects.equals(this.givenName, user.givenName) &&
        Objects.equals(this.name, user.name) &&
        Objects.equals(this.preferredUsername, user.preferredUsername);
  }

  @Override
  public int hashCode() {
    return Objects.hash(familyName, givenName, name, preferredUsername);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class User {\n");
    
    sb.append("    familyName: ").append(toIndentedString(familyName)).append("\n");
    sb.append("    givenName: ").append(toIndentedString(givenName)).append("\n");
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
    sb.append("    preferredUsername: ").append(toIndentedString(preferredUsername)).append("\n");
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

