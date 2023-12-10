package nl.tudelft.sem.yumyumnow.delivery.domain.model;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import java.time.OffsetDateTime;
import java.util.UUID;

import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.Valid;

import io.swagger.v3.oas.annotations.media.Schema;


import javax.annotation.Generated;

/**
 * Delivery
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2023-12-10T16:36:45.696015+01:00[Europe/Amsterdam]")
public class Delivery {

  private UUID id;

  private UUID orderId;

  private UUID courierId;

  private UUID vendorId;

  /**
   * Gets or Sets status
   */
  public enum StatusEnum {
    PENDING("PENDING"),
    
    ACCEPTED("ACCEPTED"),
    
    REJECTED("REJECTED"),
    
    PREPARING("PREPARING"),
    
    GIVEN_TO_COURIER("GIVEN_TO_COURIER"),
    
    IN_TRANSIT("IN_TRANSIT"),
    
    DELIVERED("DELIVERED");

    private String value;

    StatusEnum(String value) {
      this.value = value;
    }

    @JsonValue
    public String getValue() {
      return value;
    }

    @Override
    public String toString() {
      return String.valueOf(value);
    }

    @JsonCreator
    public static StatusEnum fromValue(String value) {
      for (StatusEnum b : StatusEnum.values()) {
        if (b.value.equals(value)) {
          return b;
        }
      }
      throw new IllegalArgumentException("Unexpected value '" + value + "'");
    }
  }

  private StatusEnum status;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private OffsetDateTime estimatedDeliveryTime;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private OffsetDateTime estimatedPreparationFinishTime;

  private Location currentLocation;

  public Delivery id(UUID id) {
    this.id = id;
    return this;
  }

  /**
   * Get id
   * @return id
  */
  @Valid 
  @Schema(name = "id", example = "7ddf1b64-8dfa-11ee-b9d1-0242ac120002", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("id")
  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public Delivery orderId(UUID orderId) {
    this.orderId = orderId;
    return this;
  }

  /**
   * Get orderId
   * @return orderId
  */
  @Valid 
  @Schema(name = "orderId", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("orderId")
  public UUID getOrderId() {
    return orderId;
  }

  public void setOrderId(UUID orderId) {
    this.orderId = orderId;
  }

  public Delivery courierId(UUID courierId) {
    this.courierId = courierId;
    return this;
  }

  /**
   * Get courierId
   * @return courierId
  */
  @Valid 
  @Schema(name = "courierId", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("courierId")
  public UUID getCourierId() {
    return courierId;
  }

  public void setCourierId(UUID courierId) {
    this.courierId = courierId;
  }

  public Delivery vendorId(UUID vendorId) {
    this.vendorId = vendorId;
    return this;
  }

  /**
   * Get vendorId
   * @return vendorId
  */
  @Valid 
  @Schema(name = "vendorId", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("vendorId")
  public UUID getVendorId() {
    return vendorId;
  }

  public void setVendorId(UUID vendorId) {
    this.vendorId = vendorId;
  }

  public Delivery status(StatusEnum status) {
    this.status = status;
    return this;
  }

  /**
   * Get status
   * @return status
  */
  
  @Schema(name = "status", example = "PREPARING", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("status")
  public StatusEnum getStatus() {
    return status;
  }

  public void setStatus(StatusEnum status) {
    this.status = status;
  }

  public Delivery estimatedDeliveryTime(OffsetDateTime estimatedDeliveryTime) {
    this.estimatedDeliveryTime = estimatedDeliveryTime;
    return this;
  }

  /**
   * Get estimatedDeliveryTime
   * @return estimatedDeliveryTime
  */
  @Valid 
  @Schema(name = "estimatedDeliveryTime", example = "2018-11-10T13:49:51.141Z", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("estimatedDeliveryTime")
  public OffsetDateTime getEstimatedDeliveryTime() {
    return estimatedDeliveryTime;
  }

  public void setEstimatedDeliveryTime(OffsetDateTime estimatedDeliveryTime) {
    this.estimatedDeliveryTime = estimatedDeliveryTime;
  }

  public Delivery estimatedPreparationFinishTime(OffsetDateTime estimatedPreparationFinishTime) {
    this.estimatedPreparationFinishTime = estimatedPreparationFinishTime;
    return this;
  }

  /**
   * Get estimatedPreparationFinishTime
   * @return estimatedPreparationFinishTime
  */
  @Valid 
  @Schema(name = "estimatedPreparationFinishTime", example = "2018-11-10T13:47:51.141Z", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("estimatedPreparationFinishTime")
  public OffsetDateTime getEstimatedPreparationFinishTime() {
    return estimatedPreparationFinishTime;
  }

  public void setEstimatedPreparationFinishTime(OffsetDateTime estimatedPreparationFinishTime) {
    this.estimatedPreparationFinishTime = estimatedPreparationFinishTime;
  }

  public Delivery currentLocation(Location currentLocation) {
    this.currentLocation = currentLocation;
    return this;
  }

  /**
   * Get currentLocation
   * @return currentLocation
  */
  @Valid 
  @Schema(name = "currentLocation", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("currentLocation")
  public Location getCurrentLocation() {
    return currentLocation;
  }

  public void setCurrentLocation(Location currentLocation) {
    this.currentLocation = currentLocation;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Delivery delivery = (Delivery) o;
    return Objects.equals(this.id, delivery.id) &&
        Objects.equals(this.orderId, delivery.orderId) &&
        Objects.equals(this.courierId, delivery.courierId) &&
        Objects.equals(this.vendorId, delivery.vendorId) &&
        Objects.equals(this.status, delivery.status) &&
        Objects.equals(this.estimatedDeliveryTime, delivery.estimatedDeliveryTime) &&
        Objects.equals(this.estimatedPreparationFinishTime, delivery.estimatedPreparationFinishTime) &&
        Objects.equals(this.currentLocation, delivery.currentLocation);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, orderId, courierId, vendorId, status, estimatedDeliveryTime, estimatedPreparationFinishTime, currentLocation);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Delivery {\n");
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    orderId: ").append(toIndentedString(orderId)).append("\n");
    sb.append("    courierId: ").append(toIndentedString(courierId)).append("\n");
    sb.append("    vendorId: ").append(toIndentedString(vendorId)).append("\n");
    sb.append("    status: ").append(toIndentedString(status)).append("\n");
    sb.append("    estimatedDeliveryTime: ").append(toIndentedString(estimatedDeliveryTime)).append("\n");
    sb.append("    estimatedPreparationFinishTime: ").append(toIndentedString(estimatedPreparationFinishTime)).append("\n");
    sb.append("    currentLocation: ").append(toIndentedString(currentLocation)).append("\n");
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

