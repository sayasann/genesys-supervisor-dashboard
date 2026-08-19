package com.genesys.exception;

import lombok.Getter;

@Getter
public class BaseException  extends RuntimeException{


    private ErrorMessage errorMessage;

    public BaseException(ErrorMessage errorMessage){
        super(errorMessage.prepareErrorMessage());
        this.errorMessage=errorMessage;
    }


}

