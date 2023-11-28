# AdminApi

All URIs are relative to *http://localhost:8081*

| Method | HTTP request | Description |
|------------- | ------------- | -------------|
| [**deliveryAdminAnalyticsGet**](AdminApi.md#deliveryAdminAnalyticsGet) | **GET** /delivery/admin/analytics | Get delivery system analytics |
| [**deliveryAdminMaxDeliveryZoneGet**](AdminApi.md#deliveryAdminMaxDeliveryZoneGet) | **GET** /delivery/admin/maxDeliveryZone | Get the default maximum delivery zone |
| [**deliveryAdminMaxDeliveryZonePost**](AdminApi.md#deliveryAdminMaxDeliveryZonePost) | **POST** /delivery/admin/maxDeliveryZone | Set the default maximum delivery zone |


<a name="deliveryAdminAnalyticsGet"></a>
# **deliveryAdminAnalyticsGet**
> DeliveryAdminAnalyticsGet200Response deliveryAdminAnalyticsGet(startDate, endDate)

Get delivery system analytics

Get delivery system analytics collected so far.

### Example
```java
// Import classes:
import org.openapitools.client.ApiClient;
import org.openapitools.client.ApiException;
import org.openapitools.client.Configuration;
import org.openapitools.client.models.*;
import org.openapitools.client.api.AdminApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://localhost:8081");

    AdminApi apiInstance = new AdminApi(defaultClient);
    Object startDate = 2018-11-10T13:49:51.141Z; // Object | Start date of analytics.
    Object endDate = 2018-11-10T13:49:51.141Z; // Object | End date of analytics.
    try {
      DeliveryAdminAnalyticsGet200Response result = apiInstance.deliveryAdminAnalyticsGet(startDate, endDate);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling AdminApi#deliveryAdminAnalyticsGet");
      System.err.println("Status code: " + e.getCode());
      System.err.println("Reason: " + e.getResponseBody());
      System.err.println("Response headers: " + e.getResponseHeaders());
      e.printStackTrace();
    }
  }
}
```

### Parameters

| Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **startDate** | [**Object**](.md)| Start date of analytics. | |
| **endDate** | [**Object**](.md)| End date of analytics. | |

### Return type

[**DeliveryAdminAnalyticsGet200Response**](DeliveryAdminAnalyticsGet200Response.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Successful response |  -  |
| **400** | Bad request |  -  |

<a name="deliveryAdminMaxDeliveryZoneGet"></a>
# **deliveryAdminMaxDeliveryZoneGet**
> Object deliveryAdminMaxDeliveryZoneGet()

Get the default maximum delivery zone

Get the default maximum application-wide delivery zone for all vendors. This default value is used if a vendor has not specified the delivery zone.

### Example
```java
// Import classes:
import org.openapitools.client.ApiClient;
import org.openapitools.client.ApiException;
import org.openapitools.client.Configuration;
import org.openapitools.client.models.*;
import org.openapitools.client.api.AdminApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://localhost:8081");

    AdminApi apiInstance = new AdminApi(defaultClient);
    try {
      Object result = apiInstance.deliveryAdminMaxDeliveryZoneGet();
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling AdminApi#deliveryAdminMaxDeliveryZoneGet");
      System.err.println("Status code: " + e.getCode());
      System.err.println("Reason: " + e.getResponseBody());
      System.err.println("Response headers: " + e.getResponseHeaders());
      e.printStackTrace();
    }
  }
}
```

### Parameters
This endpoint does not need any parameter.

### Return type

**Object**

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Successful response |  -  |
| **400** | Bad request |  -  |

<a name="deliveryAdminMaxDeliveryZonePost"></a>
# **deliveryAdminMaxDeliveryZonePost**
> Delivery deliveryAdminMaxDeliveryZonePost(body)

Set the default maximum delivery zone

Set the default maximum application-wide delivery zone for all vendors. This default value is used if a vendor has not specified the delivery zone.

### Example
```java
// Import classes:
import org.openapitools.client.ApiClient;
import org.openapitools.client.ApiException;
import org.openapitools.client.Configuration;
import org.openapitools.client.models.*;
import org.openapitools.client.api.AdminApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://localhost:8081");

    AdminApi apiInstance = new AdminApi(defaultClient);
    Object body = null; // Object | 
    try {
      Delivery result = apiInstance.deliveryAdminMaxDeliveryZonePost(body);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling AdminApi#deliveryAdminMaxDeliveryZonePost");
      System.err.println("Status code: " + e.getCode());
      System.err.println("Reason: " + e.getResponseBody());
      System.err.println("Response headers: " + e.getResponseHeaders());
      e.printStackTrace();
    }
  }
}
```

### Parameters

| Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **body** | **Object**|  | [optional] |

### Return type

[**Delivery**](Delivery.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Successful response |  -  |
| **400** | Bad request |  -  |

