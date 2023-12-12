package nl.tudelft.sem.yumyumnow.delivery.domain.model.dto;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import java.util.UUID;
import javax.validation.Valid;

import io.swagger.v3.oas.annotations.media.Schema;
import nl.tudelft.sem.yumyumnow.delivery.domain.model.Location;


import javax.annotation.Generated;

/**
 * DeliveryIdLocationPutRequest
 */

@JsonTypeName("_delivery__id__location_put_request")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2023-12-12T16:32:44.343382+01:00[Europe/Amsterdam]")
public class DeliveryIdLocationPutRequest {

  private UUID courierId;

  private Location location;

  public DeliveryIdLocationPutRequest courierId(UUID courierId) {
    this.courierId = courierId;
    return this;
  }

  /**
   * Get courierId
   * @return courierId
  */
  @Valid 
  @Schema(name = "courierId", example = "7ddf1a10-8dfa-11ee-b9d1-0242ac120002", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("courierId")
  public UUID getCourierId() {
    return courierId;
  }

  public void setCourierId(UUID courierId) {
    this.courierId = courierId;
  }

  public DeliveryIdLocationPutRequest location(Location location) {
    this.location = location;
    return this;
  }

  /**
   * Get location
   * @return location
  */
  @Valid 
  @Schema(name = "location", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("location")
  public Location getLocation() {
    return location;
  }

  public void setLocation(Location location) {
    this.location = location;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    DeliveryIdLocationPutRequest deliveryIdLocationPutRequest = (DeliveryIdLocationPutRequest) o;
    return Objects.equals(this.courierId, deliveryIdLocationPutRequest.courierId) &&
        Objects.equals(this.location, deliveryIdLocationPutRequest.location);
  }

  @Override
  public int hashCode() {
    return Objects.hash(courierId, location);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class DeliveryIdLocationPutRequest {\n");
    sb.append("    courierId: ").append(toIndentedString(courierId)).append("\n");
    sb.append("    location: ").append(toIndentedString(location)).append("\n");
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

