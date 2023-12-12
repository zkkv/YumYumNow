package nl.tudelft.sem.yumyumnow.delivery.domain.model.dto;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import java.util.UUID;
import javax.validation.Valid;

import io.swagger.v3.oas.annotations.media.Schema;


import javax.annotation.Generated;

/**
 * DeliveryVendorIdCustomCouriersPutRequest
 */

@JsonTypeName("_delivery_vendor__id__customCouriers_put_request")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2023-12-12T16:32:44.343382+01:00[Europe/Amsterdam]")
public class DeliveryVendorIdCustomCouriersPutRequest {

  private UUID vendorId;

  private Boolean allowsOnlyOwnCouriers;

  public DeliveryVendorIdCustomCouriersPutRequest vendorId(UUID vendorId) {
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

  public DeliveryVendorIdCustomCouriersPutRequest allowsOnlyOwnCouriers(Boolean allowsOnlyOwnCouriers) {
    this.allowsOnlyOwnCouriers = allowsOnlyOwnCouriers;
    return this;
  }

  /**
   * Get allowsOnlyOwnCouriers
   * @return allowsOnlyOwnCouriers
  */
  
  @Schema(name = "allowsOnlyOwnCouriers", example = "true", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("allowsOnlyOwnCouriers")
  public Boolean getAllowsOnlyOwnCouriers() {
    return allowsOnlyOwnCouriers;
  }

  public void setAllowsOnlyOwnCouriers(Boolean allowsOnlyOwnCouriers) {
    this.allowsOnlyOwnCouriers = allowsOnlyOwnCouriers;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    DeliveryVendorIdCustomCouriersPutRequest deliveryVendorIdCustomCouriersPutRequest = (DeliveryVendorIdCustomCouriersPutRequest) o;
    return Objects.equals(this.vendorId, deliveryVendorIdCustomCouriersPutRequest.vendorId) &&
        Objects.equals(this.allowsOnlyOwnCouriers, deliveryVendorIdCustomCouriersPutRequest.allowsOnlyOwnCouriers);
  }

  @Override
  public int hashCode() {
    return Objects.hash(vendorId, allowsOnlyOwnCouriers);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class DeliveryVendorIdCustomCouriersPutRequest {\n");
    sb.append("    vendorId: ").append(toIndentedString(vendorId)).append("\n");
    sb.append("    allowsOnlyOwnCouriers: ").append(toIndentedString(allowsOnlyOwnCouriers)).append("\n");
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

