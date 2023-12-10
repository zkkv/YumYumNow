package nl.tudelft.sem.yumyumnow.delivery.domain.model;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.Valid;

import io.swagger.v3.oas.annotations.media.Schema;


import javax.annotation.Generated;

/**
 * DeliveryAdminAnalyticsDeliveryTimeGet200Response
 */

@JsonTypeName("_delivery_admin_analytics_delivery_time_get_200_response")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2023-12-10T16:36:45.696015+01:00[Europe/Amsterdam]")
public class DeliveryAdminAnalyticsDeliveryTimeGet200Response {

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private OffsetDateTime startDate;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private OffsetDateTime endDate;

  private BigDecimal deliveryTime;

  public DeliveryAdminAnalyticsDeliveryTimeGet200Response startDate(OffsetDateTime startDate) {
    this.startDate = startDate;
    return this;
  }

  /**
   * Get startDate
   * @return startDate
  */
  @Valid 
  @Schema(name = "startDate", example = "2018-11-10T13:49:51.141Z", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("startDate")
  public OffsetDateTime getStartDate() {
    return startDate;
  }

  public void setStartDate(OffsetDateTime startDate) {
    this.startDate = startDate;
  }

  public DeliveryAdminAnalyticsDeliveryTimeGet200Response endDate(OffsetDateTime endDate) {
    this.endDate = endDate;
    return this;
  }

  /**
   * Get endDate
   * @return endDate
  */
  @Valid 
  @Schema(name = "endDate", example = "2018-11-10T13:49:51.141Z", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("endDate")
  public OffsetDateTime getEndDate() {
    return endDate;
  }

  public void setEndDate(OffsetDateTime endDate) {
    this.endDate = endDate;
  }

  public DeliveryAdminAnalyticsDeliveryTimeGet200Response deliveryTime(BigDecimal deliveryTime) {
    this.deliveryTime = deliveryTime;
    return this;
  }

  /**
   * Get deliveryTime
   * @return deliveryTime
  */
  @Valid 
  @Schema(name = "deliveryTime", example = "10", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("deliveryTime")
  public BigDecimal getDeliveryTime() {
    return deliveryTime;
  }

  public void setDeliveryTime(BigDecimal deliveryTime) {
    this.deliveryTime = deliveryTime;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    DeliveryAdminAnalyticsDeliveryTimeGet200Response deliveryAdminAnalyticsDeliveryTimeGet200Response = (DeliveryAdminAnalyticsDeliveryTimeGet200Response) o;
    return Objects.equals(this.startDate, deliveryAdminAnalyticsDeliveryTimeGet200Response.startDate) &&
        Objects.equals(this.endDate, deliveryAdminAnalyticsDeliveryTimeGet200Response.endDate) &&
        Objects.equals(this.deliveryTime, deliveryAdminAnalyticsDeliveryTimeGet200Response.deliveryTime);
  }

  @Override
  public int hashCode() {
    return Objects.hash(startDate, endDate, deliveryTime);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class DeliveryAdminAnalyticsDeliveryTimeGet200Response {\n");
    sb.append("    startDate: ").append(toIndentedString(startDate)).append("\n");
    sb.append("    endDate: ").append(toIndentedString(endDate)).append("\n");
    sb.append("    deliveryTime: ").append(toIndentedString(deliveryTime)).append("\n");
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

