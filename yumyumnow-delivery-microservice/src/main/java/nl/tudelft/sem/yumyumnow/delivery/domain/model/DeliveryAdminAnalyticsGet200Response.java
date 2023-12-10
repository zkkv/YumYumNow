package nl.tudelft.sem.yumyumnow.delivery.domain.model;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.Valid;

import io.swagger.v3.oas.annotations.media.Schema;


import javax.annotation.Generated;

/**
 * DeliveryAdminAnalyticsGet200Response
 */

@JsonTypeName("_delivery_admin_analytics_get_200_response")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2023-12-05T14:49:08.361274+01:00[Europe/Amsterdam]")
public class DeliveryAdminAnalyticsGet200Response {

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private OffsetDateTime startDate;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private OffsetDateTime endDate;

  private Integer totalDeliveries;

  private Integer successfulDeliveries;

  @Valid
  private List<@Valid Analytic> analytics;

  public DeliveryAdminAnalyticsGet200Response startDate(OffsetDateTime startDate) {
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

  public DeliveryAdminAnalyticsGet200Response endDate(OffsetDateTime endDate) {
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

  public DeliveryAdminAnalyticsGet200Response totalDeliveries(Integer totalDeliveries) {
    this.totalDeliveries = totalDeliveries;
    return this;
  }

  /**
   * Get totalDeliveries
   * @return totalDeliveries
  */
  
  @Schema(name = "totalDeliveries", example = "10", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("totalDeliveries")
  public Integer getTotalDeliveries() {
    return totalDeliveries;
  }

  public void setTotalDeliveries(Integer totalDeliveries) {
    this.totalDeliveries = totalDeliveries;
  }

  public DeliveryAdminAnalyticsGet200Response successfulDeliveries(Integer successfulDeliveries) {
    this.successfulDeliveries = successfulDeliveries;
    return this;
  }

  /**
   * Get successfulDeliveries
   * @return successfulDeliveries
  */
  
  @Schema(name = "successfulDeliveries", example = "5", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("successfulDeliveries")
  public Integer getSuccessfulDeliveries() {
    return successfulDeliveries;
  }

  public void setSuccessfulDeliveries(Integer successfulDeliveries) {
    this.successfulDeliveries = successfulDeliveries;
  }

  public DeliveryAdminAnalyticsGet200Response analytics(List<@Valid Analytic> analytics) {
    this.analytics = analytics;
    return this;
  }

  public DeliveryAdminAnalyticsGet200Response addAnalyticsItem(Analytic analyticsItem) {
    if (this.analytics == null) {
      this.analytics = new ArrayList<>();
    }
    this.analytics.add(analyticsItem);
    return this;
  }

  /**
   * Get analytics
   * @return analytics
  */
  @Valid 
  @Schema(name = "analytics", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("analytics")
  public List<@Valid Analytic> getAnalytics() {
    return analytics;
  }

  public void setAnalytics(List<@Valid Analytic> analytics) {
    this.analytics = analytics;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    DeliveryAdminAnalyticsGet200Response deliveryAdminAnalyticsGet200Response = (DeliveryAdminAnalyticsGet200Response) o;
    return Objects.equals(this.startDate, deliveryAdminAnalyticsGet200Response.startDate) &&
        Objects.equals(this.endDate, deliveryAdminAnalyticsGet200Response.endDate) &&
        Objects.equals(this.totalDeliveries, deliveryAdminAnalyticsGet200Response.totalDeliveries) &&
        Objects.equals(this.successfulDeliveries, deliveryAdminAnalyticsGet200Response.successfulDeliveries) &&
        Objects.equals(this.analytics, deliveryAdminAnalyticsGet200Response.analytics);
  }

  @Override
  public int hashCode() {
    return Objects.hash(startDate, endDate, totalDeliveries, successfulDeliveries, analytics);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class DeliveryAdminAnalyticsGet200Response {\n");
    sb.append("    startDate: ").append(toIndentedString(startDate)).append("\n");
    sb.append("    endDate: ").append(toIndentedString(endDate)).append("\n");
    sb.append("    totalDeliveries: ").append(toIndentedString(totalDeliveries)).append("\n");
    sb.append("    successfulDeliveries: ").append(toIndentedString(successfulDeliveries)).append("\n");
    sb.append("    analytics: ").append(toIndentedString(analytics)).append("\n");
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

