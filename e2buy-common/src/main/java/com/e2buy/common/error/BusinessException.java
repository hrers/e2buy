package com.e2buy.common.error;
import com.e2buy.common.enums.IEnum;

public class BusinessException extends RuntimeException {
    private static final long serialVersionUID = 8036252753160004917L;

    private String code;
    public BusinessException() {
        super();
    }

    public BusinessException(IEnum iEnum) {
        super(iEnum.getDesc());
        this.code = iEnum.getCode();
    }

    public BusinessException(IEnum iEnum, String details) {
        super(iEnum.getDesc());
        this.code = iEnum.getCode();
    }

    public BusinessException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public BusinessException(String message, Throwable cause) {
        super(message, cause);
    }

    public BusinessException(String message) {
        super(message);
    }

    public BusinessException(Throwable cause) {
        super(cause);
    }

    public String getCode() {
        return code;
    }
}
