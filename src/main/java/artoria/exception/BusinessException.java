package artoria.exception;

import artoria.common.SimpleErrorCode;
import artoria.data.ErrorCode;

/**
 * Business exception.
 * @author Kahle
 */
@Deprecated // TODO: can delete
public class BusinessException extends UncheckedException {
    private static final String DEF_ERROR_CODE = "500";
    private final ErrorCode errorCode;
    private final Object[] arguments;

    public BusinessException(String message, Object... arguments) {
        super(message);
        this.errorCode = new SimpleErrorCode(DEF_ERROR_CODE, message);
        this.arguments = arguments;
    }

    public BusinessException(String message, Throwable cause, Object... arguments) {
        super(message, cause);
        this.errorCode = new SimpleErrorCode(DEF_ERROR_CODE, message);
        this.arguments = arguments;
    }

    public BusinessException(ErrorCode errorCode, Object... arguments) {
        super(errorCode != null ? errorCode.getDescription() : null);
        this.errorCode = errorCode;
        this.arguments = arguments;
    }

    public BusinessException(ErrorCode errorCode, Throwable cause, Object... arguments) {
        super(errorCode != null ? errorCode.getDescription() : null, cause);
        this.errorCode = errorCode;
        this.arguments = arguments;
    }

    public ErrorCode getErrorCode() {

        return errorCode;
    }

    public Object[] getArguments() {

        return arguments;
    }

}
