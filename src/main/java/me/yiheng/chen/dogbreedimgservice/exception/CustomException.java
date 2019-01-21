package me.yiheng.chen.dogbreedimgservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * @author Yiheng Chen
 * @date 20/1/19 1:07 PM
 */
@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class CustomException extends Exception {
    private String errrorMessage;
    private String errorCode;
    private Exception e;

    public CustomException(String errorMessage) {
        this(errorMessage, "0000");
    }

    public CustomException(String errorMessage, String errorCode) {
        super(errorMessage);
        this.errrorMessage = errorMessage;
        this.errorCode = errorCode;
    }

    public CustomException(Exception e, String errorMessage) {
        this(e, errorMessage, "0000");
    }

    public CustomException(Exception e, String errorMessage, String errorCode) {
        super(errorMessage, e);
        this.e = e;
        this.errrorMessage = errorMessage;
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return this.errorCode;
    }

    public String getErrrorMessage() {
        return this.errrorMessage;
    }
}