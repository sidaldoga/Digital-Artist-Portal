package network;

import java.io.Serializable;

public class Response implements Serializable {
    private static final long serialVersionUID = 1L;

    private final Status status;
    private final String message;
    private final Object payload;

    public Response(Status status, String message, Object payload) {
        this.status = status;
        this.message = message;
        this.payload = payload;
    }

    public static Response ok(Object payload) {
        return new Response(Status.OK, null, payload);
    }

    public static Response ok() {
        return new Response(Status.OK, null, null);
    }

    public static Response error(Status status, String message) {
        return new Response(status, message, null);
    }

    public Status getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public Object getPayload() {
        return payload;
    }

    @Override
    public String toString() {
        return status + (message != null ? " " + message : "")
                + (payload != null ? " " + payload : "");
    }
}
