package artoria.exception;

/**
 * Internal error code.
 * @author Kahle
 */
public enum InternalErrorCode implements ErrorCode {

    /**
     * Internal server error.
     */
    INTERNAL_SERVER_ERROR("ERR0001", "Internal server error."),

    /**
     * Internal server busy.
     */
    INTERNAL_SERVER_BUSY("ERR0002", "Internal server busy."),

    /**
     * No login.
     */
    NO_LOGIN("ERR0003", "No login."),

    /**
     * No permission.
     */
    NO_PERMISSION("ERR0004", "No permission."),

    /**
     * No request body.
     */
    NO_REQUEST_BODY("ERR0005", "No request body."),

    /**
     * Parameter is required.
     */
    PARAMETER_REQUIRED("ERR0006", "Parameter is required."),

    /**
     * Primary key is required.
     */
    PRIMARY_KEY_REQUIRED("ERR0007", "Primary key is required."),

    ;

    private String code;
    private String description;

    InternalErrorCode(String code, String description) {
        this.code = code;
        this.description = description;
    }

    @Override
    public String getCode() {

        return this.code;
    }

    @Override
    public String getDescription() {

        return this.description;
    }

}
