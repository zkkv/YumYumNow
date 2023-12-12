package nl.tudelft.sem.yumyumnow.delivery.domain.model.dto;

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
 * DeliveryAdminAnalyticsIssuesGet200Response
 */

@JsonTypeName("_delivery_admin_analytics_issues_get_200_response")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2023-12-12T16:32:44.343382+01:00[Europe/Amsterdam]")
public class DeliveryAdminAnalyticsIssuesGet200Response {

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private OffsetDateTime startDate;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private OffsetDateTime endDate;

  @Valid
  private List<String> encounteredIssues;

  public DeliveryAdminAnalyticsIssuesGet200Response startDate(OffsetDateTime startDate) {
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

  public DeliveryAdminAnalyticsIssuesGet200Response endDate(OffsetDateTime endDate) {
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

  public DeliveryAdminAnalyticsIssuesGet200Response encounteredIssues(List<String> encounteredIssues) {
    this.encounteredIssues = encounteredIssues;
    return this;
  }

  public DeliveryAdminAnalyticsIssuesGet200Response addEncounteredIssuesItem(String encounteredIssuesItem) {
    if (this.encounteredIssues == null) {
      this.encounteredIssues = new ArrayList<>();
    }
    this.encounteredIssues.add(encounteredIssuesItem);
    return this;
  }

  /**
   * Get encounteredIssues
   * @return encounteredIssues
  */
  
  @Schema(name = "encounteredIssues", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("encounteredIssues")
  public List<String> getEncounteredIssues() {
    return encounteredIssues;
  }

  public void setEncounteredIssues(List<String> encounteredIssues) {
    this.encounteredIssues = encounteredIssues;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    DeliveryAdminAnalyticsIssuesGet200Response deliveryAdminAnalyticsIssuesGet200Response = (DeliveryAdminAnalyticsIssuesGet200Response) o;
    return Objects.equals(this.startDate, deliveryAdminAnalyticsIssuesGet200Response.startDate) &&
        Objects.equals(this.endDate, deliveryAdminAnalyticsIssuesGet200Response.endDate) &&
        Objects.equals(this.encounteredIssues, deliveryAdminAnalyticsIssuesGet200Response.encounteredIssues);
  }

  @Override
  public int hashCode() {
    return Objects.hash(startDate, endDate, encounteredIssues);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class DeliveryAdminAnalyticsIssuesGet200Response {\n");
    sb.append("    startDate: ").append(toIndentedString(startDate)).append("\n");
    sb.append("    endDate: ").append(toIndentedString(endDate)).append("\n");
    sb.append("    encounteredIssues: ").append(toIndentedString(encounteredIssues)).append("\n");
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

