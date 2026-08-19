package com.genesys.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum MessageType {

    NO_RECORD_EXIST("1", "Record is not exist.", HttpStatus.NOT_FOUND);

    private String code;
    private String message;

    private HttpStatus httpStatus;

    MessageType(String code, String message, HttpStatus httpStatus){
        this.code=code;
        this.message=message;
        this.httpStatus=httpStatus;
    }


}
