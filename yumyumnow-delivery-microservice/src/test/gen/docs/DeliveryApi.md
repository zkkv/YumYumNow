# DeliveryApi

All URIs are relative to *http://localhost:8081*

| Method | HTTP request | Description |
|------------- | ------------- | -------------|
| [**deliveryGetAvailableInRadiusGet**](DeliveryApi.md#deliveryGetAvailableInRadiusGet) | **GET** /delivery/getAvailableInRadius | Get available deliveries in radius (for couriers) |
| [**deliveryIdAssignPost**](DeliveryApi.md#deliveryIdAssignPost) | **POST** /delivery/{id}/assign | A courier accept the delivery |
| [**deliveryIdGet**](DeliveryApi.md#deliveryIdGet) | **GET** /delivery/{id} | Get a delivery |
| [**deliveryIdOrderRatingPost**](DeliveryApi.md#deliveryIdOrderRatingPost) | **POST** /delivery/{id}/orderRating | Specify the order rating |
| [**deliveryIdUpdateDeliveryTimePost**](DeliveryApi.md#deliveryIdUpdateDeliveryTimePost) | **POST** /delivery/{id}/updateDeliveryTime | Update the delivery time |
| [**deliveryIdUpdateLocationPost**](DeliveryApi.md#deliveryIdUpdateLocationPost) | **POST** /delivery/{id}/updateLocation | Update the location of the delivery |
| [**deliveryIdUpdateStatusPost**](DeliveryApi.md#deliveryIdUpdateStatusPost) | **POST** /delivery/{id}/updateStatus | Update the status of a delivery |
| [**deliveryPost**](DeliveryApi.md#deliveryPost) | **POST** /delivery | Create a delivery |


<a name="deliveryGetAvailableInRadiusGet"></a>
# **deliveryGetAvailableInRadiusGet**
> List&lt;Delivery&gt; deliveryGetAvailableInRadiusGet(radius, courierId)

Get available deliveries in radius (for couriers)

Get available deliveries in radius (for couriers). If a vendor has chosen to only deliver using its own couriers, then deliveries from that vendor will only be returned if the courier is affiliated with that vendor.

### Example
```java
// Import classes:
import org.openapitools.client.ApiClient;
import org.openapitools.client.ApiException;
import org.openapitools.client.Configuration;
import org.openapitools.client.models.*;
import org.openapitools.client.api.DeliveryApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://localhost:8081");

    DeliveryApi apiInstance = new DeliveryApi(defaultClient);
    Object radius = null; // Object | The maximum distance in kilometers
    Object courierId = null; // Object | The courier ID
    try {
      List<Delivery> result = apiInstance.deliveryGetAvailableInRadiusGet(radius, courierId);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling DeliveryApi#deliveryGetAvailableInRadiusGet");
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
| **radius** | [**Object**](.md)| The maximum distance in kilometers | |
| **courierId** | [**Object**](.md)| The courier ID | |

### Return type

[**List&lt;Delivery&gt;**](Delivery.md)

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

<a name="deliveryIdAssignPost"></a>
# **deliveryIdAssignPost**
> Delivery deliveryIdAssignPost(id, courier)

A courier accept the delivery

The delivery is assigned to a courier.

### Example
```java
// Import classes:
import org.openapitools.client.ApiClient;
import org.openapitools.client.ApiException;
import org.openapitools.client.Configuration;
import org.openapitools.client.models.*;
import org.openapitools.client.api.DeliveryApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://localhost:8081");

    DeliveryApi apiInstance = new DeliveryApi(defaultClient);
    Object id = null; // Object | UUID of the delivery
    Courier courier = new Courier(); // Courier | 
    try {
      Delivery result = apiInstance.deliveryIdAssignPost(id, courier);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling DeliveryApi#deliveryIdAssignPost");
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
| **id** | [**Object**](.md)| UUID of the delivery | |
| **courier** | [**Courier**](Courier.md)|  | [optional] |

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

<a name="deliveryIdGet"></a>
# **deliveryIdGet**
> Delivery deliveryIdGet(id)

Get a delivery

Get delivery details based on id

### Example
```java
// Import classes:
import org.openapitools.client.ApiClient;
import org.openapitools.client.ApiException;
import org.openapitools.client.Configuration;
import org.openapitools.client.models.*;
import org.openapitools.client.api.DeliveryApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://localhost:8081");

    DeliveryApi apiInstance = new DeliveryApi(defaultClient);
    Object id = null; // Object | UUID of the delivery
    try {
      Delivery result = apiInstance.deliveryIdGet(id);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling DeliveryApi#deliveryIdGet");
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
| **id** | [**Object**](.md)| UUID of the delivery | |

### Return type

[**Delivery**](Delivery.md)

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

<a name="deliveryIdOrderRatingPost"></a>
# **deliveryIdOrderRatingPost**
> Delivery deliveryIdOrderRatingPost(id, body)

Specify the order rating

Customers can rate the order on scale from 1 to 5.

### Example
```java
// Import classes:
import org.openapitools.client.ApiClient;
import org.openapitools.client.ApiException;
import org.openapitools.client.Configuration;
import org.openapitools.client.models.*;
import org.openapitools.client.api.DeliveryApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://localhost:8081");

    DeliveryApi apiInstance = new DeliveryApi(defaultClient);
    Object id = null; // Object | UUID of the delivery
    Object body = null; // Object | 
    try {
      Delivery result = apiInstance.deliveryIdOrderRatingPost(id, body);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling DeliveryApi#deliveryIdOrderRatingPost");
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
| **id** | [**Object**](.md)| UUID of the delivery | |
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

<a name="deliveryIdUpdateDeliveryTimePost"></a>
# **deliveryIdUpdateDeliveryTimePost**
> Delivery deliveryIdUpdateDeliveryTimePost(id, body)

Update the delivery time

Update estimatedDeliveryTime of an existing delivery (for couriers)

### Example
```java
// Import classes:
import org.openapitools.client.ApiClient;
import org.openapitools.client.ApiException;
import org.openapitools.client.Configuration;
import org.openapitools.client.models.*;
import org.openapitools.client.api.DeliveryApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://localhost:8081");

    DeliveryApi apiInstance = new DeliveryApi(defaultClient);
    Object id = null; // Object | UUID of the delivery
    Object body = null; // Object | 
    try {
      Delivery result = apiInstance.deliveryIdUpdateDeliveryTimePost(id, body);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling DeliveryApi#deliveryIdUpdateDeliveryTimePost");
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
| **id** | [**Object**](.md)| UUID of the delivery | |
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

<a name="deliveryIdUpdateLocationPost"></a>
# **deliveryIdUpdateLocationPost**
> Delivery deliveryIdUpdateLocationPost(id, location)

Update the location of the delivery

Update the current location of the delivery (for couriers)

### Example
```java
// Import classes:
import org.openapitools.client.ApiClient;
import org.openapitools.client.ApiException;
import org.openapitools.client.Configuration;
import org.openapitools.client.models.*;
import org.openapitools.client.api.DeliveryApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://localhost:8081");

    DeliveryApi apiInstance = new DeliveryApi(defaultClient);
    Object id = null; // Object | UUID of the delivery
    Location location = new Location(); // Location | 
    try {
      Delivery result = apiInstance.deliveryIdUpdateLocationPost(id, location);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling DeliveryApi#deliveryIdUpdateLocationPost");
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
| **id** | [**Object**](.md)| UUID of the delivery | |
| **location** | [**Location**](Location.md)|  | [optional] |

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

<a name="deliveryIdUpdateStatusPost"></a>
# **deliveryIdUpdateStatusPost**
> Delivery deliveryIdUpdateStatusPost(id, body)

Update the status of a delivery

Update the status of a delivery. Vendors can set the status to ACCEPTED, REJECTED, PREPARING, GIVEN_TO_COURIER. Couriers can set the status to IN_TRANSIT, DELIVERED.

### Example
```java
// Import classes:
import org.openapitools.client.ApiClient;
import org.openapitools.client.ApiException;
import org.openapitools.client.Configuration;
import org.openapitools.client.models.*;
import org.openapitools.client.api.DeliveryApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://localhost:8081");

    DeliveryApi apiInstance = new DeliveryApi(defaultClient);
    Object id = null; // Object | UUID of the delivery
    Object body = null; // Object | 
    try {
      Delivery result = apiInstance.deliveryIdUpdateStatusPost(id, body);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling DeliveryApi#deliveryIdUpdateStatusPost");
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
| **id** | [**Object**](.md)| UUID of the delivery | |
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

<a name="deliveryPost"></a>
# **deliveryPost**
> Delivery deliveryPost(order)

Create a delivery

Create a delivery based on order data

### Example
```java
// Import classes:
import org.openapitools.client.ApiClient;
import org.openapitools.client.ApiException;
import org.openapitools.client.Configuration;
import org.openapitools.client.models.*;
import org.openapitools.client.api.DeliveryApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://localhost:8081");

    DeliveryApi apiInstance = new DeliveryApi(defaultClient);
    Order order = new Order(); // Order | 
    try {
      Delivery result = apiInstance.deliveryPost(order);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling DeliveryApi#deliveryPost");
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
| **order** | [**Order**](Order.md)|  | [optional] |

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

