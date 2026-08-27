package com.genesys.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum MessageType {

    NO_RECORD_EXIST("1", "Record is not exist.", HttpStatus.NOT_FOUND),
    GENESYS_AUTH_FAILED("2","Genesys token couldn't be fetched", HttpStatus.BAD_GATEWAY),
    GENESYS_UNAVAILABLE("3","Genesys is not reachable at the moment",HttpStatus.SERVICE_UNAVAILABLE),
    INVALID_GENESYS_REGION("4", "Invalid AWS Region", HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_CREDENTIALS("5","Invalid login or password", HttpStatus.UNAUTHORIZED),
    ALREADY_EXIST("5","This data already exists",HttpStatus.CONFLICT);


    private String code;
    private String message;

    private HttpStatus httpStatus;

    MessageType(String code, String message, HttpStatus httpStatus){
        this.code=code;
        this.message=message;
        this.httpStatus=httpStatus;
    }


}
