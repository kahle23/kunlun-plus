package artoria.exception;

import artoria.data.ErrorCode;

public enum SystemErrorCode implements ErrorCode {
    USERNAME_IS_REQUIRED("SYS001", "Username is required. "),
    PASSWORD_IS_REQUIRED("SYS002", "Password is required. "),
    ;

    private final String description;
    private final String code;

    SystemErrorCode(String code, String description) {
        this.code = code;
        this.description = description;
    }

    @Override
    public String getCode() {

        return code;
    }

    @Override
    public String getDescription() {

        return description;
    }

}
