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
 * DeliveryVendorIdMaxZonePutRequest
 */

@JsonTypeName("_delivery_vendor__id__max_zone_put_request")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2023-12-10T16:36:45.696015+01:00[Europe/Amsterdam]")
public class DeliveryVendorIdMaxZonePutRequest {

  private UUID vendorId;

  private BigDecimal radiusKm;

  public DeliveryVendorIdMaxZonePutRequest vendorId(UUID vendorId) {
    this.vendorId = vendorId;
    return this;
  }

  /**
   * Get vendorId
   * @return vendorId
  */
  @Valid 
  @Schema(name = "vendorId", example = "7ddf1a10-8dfa-11ee-b9d1-0242ac120002", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("vendorId")
  public UUID getVendorId() {
    return vendorId;
  }

  public void setVendorId(UUID vendorId) {
    this.vendorId = vendorId;
  }

  public DeliveryVendorIdMaxZonePutRequest radiusKm(BigDecimal radiusKm) {
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
    DeliveryVendorIdMaxZonePutRequest deliveryVendorIdMaxZonePutRequest = (DeliveryVendorIdMaxZonePutRequest) o;
    return Objects.equals(this.vendorId, deliveryVendorIdMaxZonePutRequest.vendorId) &&
        Objects.equals(this.radiusKm, deliveryVendorIdMaxZonePutRequest.radiusKm);
  }

  @Override
  public int hashCode() {
    return Objects.hash(vendorId, radiusKm);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class DeliveryVendorIdMaxZonePutRequest {\n");
    sb.append("    vendorId: ").append(toIndentedString(vendorId)).append("\n");
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

