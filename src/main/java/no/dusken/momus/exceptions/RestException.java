package no.dusken.momus.exceptions;

public class RestException extends RuntimeException {
    private String message;
    private int status;

    /**
     *
     * @param message Message to show the user
     * @param status One of HttpServletResponse
     */
    public RestException(String message, int status) {
        this.message = message;
        this.status = status;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public int getStatus() {
        return status;
    }
}
