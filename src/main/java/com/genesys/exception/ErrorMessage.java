package com.genesys.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ErrorMessage {

    private MessageType messageType;
    private String ofStatic;

    public String prepareErrorMessage(){
        StringBuilder builder = new StringBuilder();
        builder.append(messageType.getMessage());
        if(ofStatic!=null && !ofStatic.isEmpty()){
            builder.append(ofStatic);
        }
        return builder.toString();
    }

}
