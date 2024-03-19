package io.particle.device.control;

public class RequestError extends RuntimeException {
    public RequestError(String message) {
        super(message);
    }

    public RequestError(String message, Throwable cause) {
        super(message, cause);
    }
}
