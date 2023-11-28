

# Delivery


## Properties

| Name | Type | Description | Notes |
|------------ | ------------- | ------------- | -------------|
|**id** | **Object** |  |  [optional] |
|**orderId** | **Object** |  |  [optional] |
|**courierId** | **Object** |  |  [optional] |
|**vendorId** | **Object** |  |  [optional] |
|**status** | [**StatusEnum**](#StatusEnum) |  |  [optional] |
|**estimatedDeliveryTime** | **Object** |  |  [optional] |
|**estimatedPreparationFinishTime** | **Object** |  |  [optional] |
|**currentLocation** | [**Location**](Location.md) |  |  [optional] |



## Enum: StatusEnum

| Name | Value |
|---- | -----|
| PENDING | &quot;PENDING&quot; |
| ACCEPTED | &quot;ACCEPTED&quot; |
| REJECTED | &quot;REJECTED&quot; |
| PREPARING | &quot;PREPARING&quot; |
| GIVEN_TO_COURIER | &quot;GIVEN_TO_COURIER&quot; |
| IN_TRANSIT | &quot;IN_TRANSIT&quot; |
| DELIVERED | &quot;DELIVERED&quot; |



