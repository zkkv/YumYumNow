package nl.tudelft.sem.yumyumnow.delivery.domain.model;

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
 * DeliveryIdTimePutRequest
 */

@JsonTypeName("_delivery__id__time_put_request")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2023-12-05T14:49:08.361274+01:00[Europe/Amsterdam]")
public class DeliveryIdTimePutRequest {

  private UUID userId;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private OffsetDateTime estimatedNewDeliveryTime;

  public DeliveryIdTimePutRequest userId(UUID userId) {
    this.userId = userId;
    return this;
  }

  /**
   * Get userId
   * @return userId
  */
  @Valid 
  @Schema(name = "userId", example = "7ddf1a10-8dfa-11ee-b9d1-0242ac120002", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("userId")
  public UUID getUserId() {
    return userId;
  }

  public void setUserId(UUID userId) {
    this.userId = userId;
  }

  public DeliveryIdTimePutRequest estimatedNewDeliveryTime(OffsetDateTime estimatedNewDeliveryTime) {
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
    DeliveryIdTimePutRequest deliveryIdTimePutRequest = (DeliveryIdTimePutRequest) o;
    return Objects.equals(this.userId, deliveryIdTimePutRequest.userId) &&
        Objects.equals(this.estimatedNewDeliveryTime, deliveryIdTimePutRequest.estimatedNewDeliveryTime);
  }

  @Override
  public int hashCode() {
    return Objects.hash(userId, estimatedNewDeliveryTime);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class DeliveryIdTimePutRequest {\n");
    sb.append("    userId: ").append(toIndentedString(userId)).append("\n");
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

