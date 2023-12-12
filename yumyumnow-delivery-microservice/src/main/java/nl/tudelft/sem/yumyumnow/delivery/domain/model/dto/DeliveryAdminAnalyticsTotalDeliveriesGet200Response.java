package nl.tudelft.sem.yumyumnow.delivery.domain.model.dto;

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
 * DeliveryAdminAnalyticsTotalDeliveriesGet200Response
 */

@JsonTypeName("_delivery_admin_analytics_total_deliveries_get_200_response")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2023-12-12T16:32:44.343382+01:00[Europe/Amsterdam]")
public class DeliveryAdminAnalyticsTotalDeliveriesGet200Response {

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private OffsetDateTime startDate;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private OffsetDateTime endDate;

  private BigDecimal totalDeliveries;

  public DeliveryAdminAnalyticsTotalDeliveriesGet200Response startDate(OffsetDateTime startDate) {
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

  public DeliveryAdminAnalyticsTotalDeliveriesGet200Response endDate(OffsetDateTime endDate) {
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

  public DeliveryAdminAnalyticsTotalDeliveriesGet200Response totalDeliveries(BigDecimal totalDeliveries) {
    this.totalDeliveries = totalDeliveries;
    return this;
  }

  /**
   * Get totalDeliveries
   * @return totalDeliveries
  */
  @Valid 
  @Schema(name = "totalDeliveries", example = "10", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("totalDeliveries")
  public BigDecimal getTotalDeliveries() {
    return totalDeliveries;
  }

  public void setTotalDeliveries(BigDecimal totalDeliveries) {
    this.totalDeliveries = totalDeliveries;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    DeliveryAdminAnalyticsTotalDeliveriesGet200Response deliveryAdminAnalyticsTotalDeliveriesGet200Response = (DeliveryAdminAnalyticsTotalDeliveriesGet200Response) o;
    return Objects.equals(this.startDate, deliveryAdminAnalyticsTotalDeliveriesGet200Response.startDate) &&
        Objects.equals(this.endDate, deliveryAdminAnalyticsTotalDeliveriesGet200Response.endDate) &&
        Objects.equals(this.totalDeliveries, deliveryAdminAnalyticsTotalDeliveriesGet200Response.totalDeliveries);
  }

  @Override
  public int hashCode() {
    return Objects.hash(startDate, endDate, totalDeliveries);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class DeliveryAdminAnalyticsTotalDeliveriesGet200Response {\n");
    sb.append("    startDate: ").append(toIndentedString(startDate)).append("\n");
    sb.append("    endDate: ").append(toIndentedString(endDate)).append("\n");
    sb.append("    totalDeliveries: ").append(toIndentedString(totalDeliveries)).append("\n");
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

