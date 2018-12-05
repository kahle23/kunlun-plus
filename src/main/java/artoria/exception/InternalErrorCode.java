package artoria.exception;

/**
 * Internal error code.
 * @author Kahle
 */
public enum InternalErrorCode implements ErrorCode {

    /**
     * Internal server error.
     */
    INTERNAL_SERVER_ERROR("INTERNAL_ERROR_001", "Internal server error."),

    /**
     * Internal server busy.
     */
    INTERNAL_SERVER_BUSY("INTERNAL_ERROR_002", "Internal server busy."),

    /**
     * No login.
     */
    NO_LOGIN("INTERNAL_ERROR_003", "No login."),

    /**
     * No permission.
     */
    NO_PERMISSION("INTERNAL_ERROR_004", "No permission."),

    /**
     * No request body.
     */
    NO_REQUEST_BODY("INTERNAL_ERROR_005", "No request body."),

    /**
     * Parameter is required.
     */
    PARAMETER_REQUIRED("INTERNAL_ERROR_006", "Parameter is required."),

    /**
     * Primary key is required.
     */
    PRIMARY_KEY_REQUIRED("INTERNAL_ERROR_007", "Primary key is required."),

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
