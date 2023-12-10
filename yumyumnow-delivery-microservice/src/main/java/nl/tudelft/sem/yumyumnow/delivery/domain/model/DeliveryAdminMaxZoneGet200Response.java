package nl.tudelft.sem.yumyumnow.delivery.domain.model;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import java.math.BigDecimal;
import java.util.UUID;
import javax.validation.Valid;

import io.swagger.v3.oas.annotations.media.Schema;


import javax.annotation.Generated;

/**
 * DeliveryAdminMaxZoneGet200Response
 */

@JsonTypeName("_delivery_admin_max_zone_get_200_response")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2023-12-10T16:36:45.696015+01:00[Europe/Amsterdam]")
public class DeliveryAdminMaxZoneGet200Response {

  private UUID adminId;

  private BigDecimal radiusKm;

  public DeliveryAdminMaxZoneGet200Response adminId(UUID adminId) {
    this.adminId = adminId;
    return this;
  }

  /**
   * Get adminId
   * @return adminId
  */
  @Valid 
  @Schema(name = "adminId", example = "7ddf1a10-8dfa-11ee-b9d1-0242ac120002", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("adminId")
  public UUID getAdminId() {
    return adminId;
  }

  public void setAdminId(UUID adminId) {
    this.adminId = adminId;
  }

  public DeliveryAdminMaxZoneGet200Response radiusKm(BigDecimal radiusKm) {
    this.radiusKm = radiusKm;
    return this;
  }

  /**
   * Get radiusKm
   * @return radiusKm
  */
  @Valid 
  @Schema(name = "radiusKm", example = "2", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("radiusKm")
  public BigDecimal getRadiusKm() {
    return radiusKm;
  }

  public void setRadiusKm(BigDecimal radiusKm) {
    this.radiusKm = radiusKm;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    DeliveryAdminMaxZoneGet200Response deliveryAdminMaxZoneGet200Response = (DeliveryAdminMaxZoneGet200Response) o;
    return Objects.equals(this.adminId, deliveryAdminMaxZoneGet200Response.adminId) &&
        Objects.equals(this.radiusKm, deliveryAdminMaxZoneGet200Response.radiusKm);
  }

  @Override
  public int hashCode() {
    return Objects.hash(adminId, radiusKm);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class DeliveryAdminMaxZoneGet200Response {\n");
    sb.append("    adminId: ").append(toIndentedString(adminId)).append("\n");
    sb.append("    radiusKm: ").append(toIndentedString(radiusKm)).append("\n");
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

