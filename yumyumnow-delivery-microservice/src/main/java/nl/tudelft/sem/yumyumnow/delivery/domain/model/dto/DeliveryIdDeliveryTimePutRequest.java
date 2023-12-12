package nl.tudelft.sem.yumyumnow.delivery.domain.model.dto;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import java.time.OffsetDateTime;
import java.util.UUID;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.Valid;

import io.swagger.v3.oas.annotations.media.Schema;


import javax.annotation.Generated;

/**
 * DeliveryIdDeliveryTimePutRequest
 */

@JsonTypeName("_delivery__id__deliveryTime_put_request")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2023-12-12T16:32:44.343382+01:00[Europe/Amsterdam]")
public class DeliveryIdDeliveryTimePutRequest {

  private UUID courierId;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private OffsetDateTime estimatedNewDeliveryTime;

  public DeliveryIdDeliveryTimePutRequest courierId(UUID courierId) {
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

  public DeliveryIdDeliveryTimePutRequest estimatedNewDeliveryTime(OffsetDateTime estimatedNewDeliveryTime) {
    this.estimatedNewDeliveryTime = estimatedNewDeliveryTime;
    return this;
  }

  /**
   * Get estimatedNewDeliveryTime
   * @return estimatedNewDeliveryTime
  */
  @Valid 
  @Schema(name = "estimatedNewDeliveryTime", example = "2018-11-10T13:49:51.141Z", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("estimatedNewDeliveryTime")
  public OffsetDateTime getEstimatedNewDeliveryTime() {
    return estimatedNewDeliveryTime;
  }

  public void setEstimatedNewDeliveryTime(OffsetDateTime estimatedNewDeliveryTime) {
    this.estimatedNewDeliveryTime = estimatedNewDeliveryTime;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    DeliveryIdDeliveryTimePutRequest deliveryIdDeliveryTimePutRequest = (DeliveryIdDeliveryTimePutRequest) o;
    return Objects.equals(this.courierId, deliveryIdDeliveryTimePutRequest.courierId) &&
        Objects.equals(this.estimatedNewDeliveryTime, deliveryIdDeliveryTimePutRequest.estimatedNewDeliveryTime);
  }

  @Override
  public int hashCode() {
    return Objects.hash(courierId, estimatedNewDeliveryTime);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class DeliveryIdDeliveryTimePutRequest {\n");
    sb.append("    courierId: ").append(toIndentedString(courierId)).append("\n");
    sb.append("    estimatedNewDeliveryTime: ").append(toIndentedString(estimatedNewDeliveryTime)).append("\n");
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

