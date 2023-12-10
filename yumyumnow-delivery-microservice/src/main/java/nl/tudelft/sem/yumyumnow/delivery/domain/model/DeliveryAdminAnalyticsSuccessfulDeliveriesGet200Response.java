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
 * DeliveryAdminAnalyticsSuccessfulDeliveriesGet200Response
 */

@JsonTypeName("_delivery_admin_analytics_successful_deliveries_get_200_response")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2023-12-10T16:36:45.696015+01:00[Europe/Amsterdam]")
public class DeliveryAdminAnalyticsSuccessfulDeliveriesGet200Response {

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private OffsetDateTime startDate;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private OffsetDateTime endDate;

  private BigDecimal successfulDeliveries;

  public DeliveryAdminAnalyticsSuccessfulDeliveriesGet200Response startDate(OffsetDateTime startDate) {
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

  public DeliveryAdminAnalyticsSuccessfulDeliveriesGet200Response endDate(OffsetDateTime endDate) {
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

  public DeliveryAdminAnalyticsSuccessfulDeliveriesGet200Response successfulDeliveries(BigDecimal successfulDeliveries) {
    this.successfulDeliveries = successfulDeliveries;
    return this;
  }

  /**
   * Get successfulDeliveries
   * @return successfulDeliveries
  */
  @Valid 
  @Schema(name = "successfulDeliveries", example = "10", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("successfulDeliveries")
  public BigDecimal getSuccessfulDeliveries() {
    return successfulDeliveries;
  }

  public void setSuccessfulDeliveries(BigDecimal successfulDeliveries) {
    this.successfulDeliveries = successfulDeliveries;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    DeliveryAdminAnalyticsSuccessfulDeliveriesGet200Response deliveryAdminAnalyticsSuccessfulDeliveriesGet200Response = (DeliveryAdminAnalyticsSuccessfulDeliveriesGet200Response) o;
    return Objects.equals(this.startDate, deliveryAdminAnalyticsSuccessfulDeliveriesGet200Response.startDate) &&
        Objects.equals(this.endDate, deliveryAdminAnalyticsSuccessfulDeliveriesGet200Response.endDate) &&
        Objects.equals(this.successfulDeliveries, deliveryAdminAnalyticsSuccessfulDeliveriesGet200Response.successfulDeliveries);
  }

  @Override
  public int hashCode() {
    return Objects.hash(startDate, endDate, successfulDeliveries);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class DeliveryAdminAnalyticsSuccessfulDeliveriesGet200Response {\n");
    sb.append("    startDate: ").append(toIndentedString(startDate)).append("\n");
    sb.append("    endDate: ").append(toIndentedString(endDate)).append("\n");
    sb.append("    successfulDeliveries: ").append(toIndentedString(successfulDeliveries)).append("\n");
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

