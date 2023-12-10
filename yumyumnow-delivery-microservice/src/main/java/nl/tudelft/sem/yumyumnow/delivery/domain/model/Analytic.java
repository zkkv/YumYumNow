package nl.tudelft.sem.yumyumnow.delivery.domain.model;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import javax.validation.Valid;

import io.swagger.v3.oas.annotations.media.Schema;


import javax.annotation.Generated;

/**
 * Analytic
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2023-12-05T14:49:08.361274+01:00[Europe/Amsterdam]")
@javax.persistence.Entity
public class Analytic {

  @javax.persistence.Id
  private String id;

  private Delivery delivery;

  private BigDecimal preparationTime;

  private BigDecimal deliveryTime;

  private Integer driverEfficiency;

  @Valid
  private List<String> encounteredIssues;

  public Analytic id(String id) {
    this.id = id;
    return this;
  }

  /**
   * Get id
   * @return id
  */
  
  @Schema(name = "id", example = "7ddf1042-8dfa-11ee-b9d1-0242ac120002", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("id")
  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public Analytic delivery(Delivery delivery) {
    this.delivery = delivery;
    return this;
  }

  /**
   * Get delivery
   * @return delivery
  */
  @Valid 
  @Schema(name = "delivery", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("delivery")
  public Delivery getDelivery() {
    return delivery;
  }

  public void setDelivery(Delivery delivery) {
    this.delivery = delivery;
  }

  public Analytic preparationTime(BigDecimal preparationTime) {
    this.preparationTime = preparationTime;
    return this;
  }

  /**
   * Get preparationTime
   * @return preparationTime
  */
  @Valid 
  @Schema(name = "preparationTime", example = "10", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("preparationTime")
  public BigDecimal getPreparationTime() {
    return preparationTime;
  }

  public void setPreparationTime(BigDecimal preparationTime) {
    this.preparationTime = preparationTime;
  }

  public Analytic deliveryTime(BigDecimal deliveryTime) {
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

  public Analytic driverEfficiency(Integer driverEfficiency) {
    this.driverEfficiency = driverEfficiency;
    return this;
  }

  /**
   * Get driverEfficiency
   * @return driverEfficiency
  */
  
  @Schema(name = "driverEfficiency", example = "10", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("driverEfficiency")
  public Integer getDriverEfficiency() {
    return driverEfficiency;
  }

  public void setDriverEfficiency(Integer driverEfficiency) {
    this.driverEfficiency = driverEfficiency;
  }

  public Analytic encounteredIssues(List<String> encounteredIssues) {
    this.encounteredIssues = encounteredIssues;
    return this;
  }

  public Analytic addEncounteredIssuesItem(String encounteredIssuesItem) {
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
    Analytic analytic = (Analytic) o;
    return Objects.equals(this.id, analytic.id) &&
        Objects.equals(this.delivery, analytic.delivery) &&
        Objects.equals(this.preparationTime, analytic.preparationTime) &&
        Objects.equals(this.deliveryTime, analytic.deliveryTime) &&
        Objects.equals(this.driverEfficiency, analytic.driverEfficiency) &&
        Objects.equals(this.encounteredIssues, analytic.encounteredIssues);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, delivery, preparationTime, deliveryTime, driverEfficiency, encounteredIssues);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Analytic {\n");
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    delivery: ").append(toIndentedString(delivery)).append("\n");
    sb.append("    preparationTime: ").append(toIndentedString(preparationTime)).append("\n");
    sb.append("    deliveryTime: ").append(toIndentedString(deliveryTime)).append("\n");
    sb.append("    driverEfficiency: ").append(toIndentedString(driverEfficiency)).append("\n");
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

