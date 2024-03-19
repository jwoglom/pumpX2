package io.particle.device.control;

/**
 * An interface used to implement {@link BleRequestChannel} callbacks.
 */
public interface BleRequestChannelCallback {
    /**
     * The callback invoked when the channel is opened.
     */
    void onChannelOpen();
    /**
     * The callback invoked to send data to the device.
     *
     * @param data The packet data.
     */
    void onChannelWrite(byte[] data);
    /**
     * The callback invoked when a response is received.
     *
     * @param requestId The request ID.
     * @param result The response result.
     * @param data The response data.
     */
    void onRequestResponse(int requestId, int result, byte[] data);
    /**
     * The callback invoked when a request error occurs.
     *
     * @param requestId The request ID.
     * @param error The request error.
     */
    void onRequestError(int requestId, RequestError error);
}
