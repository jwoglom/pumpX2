package com.jwoglom.pumpx2.pump.messages.annotations;

/**
 * Denotes that this request or response message is {@link com.jwoglom.pumpx2.pump.messages.models.ApiVersion} dependent.
 *
 * Instead of invoking the request directly, you should use a class inside
 * {@link com.jwoglom.pumpx2.pump.messages.builders} to create a message of the correct type dependent
 * on the pump's reported API version.
 *
 * Any code relying on the response should be sure to implement it across all API versions.
 */
public @interface ApiVersionDependent {
}
