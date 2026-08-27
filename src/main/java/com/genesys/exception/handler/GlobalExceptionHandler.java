package com.genesys.exception.handler;

import com.genesys.exception.BaseException;
import com.genesys.exception.MessageType;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConversionException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.util.*;

@RestControllerAdvice
public class GlobalExceptionHandler {

    //--If JSON can't be parsed
    @ExceptionHandler(value = HttpMessageConversionException.class)
    public ResponseEntity<ApiError> handleInvalidJson(
            HttpMessageConversionException exception, WebRequest request
    ){
        return ResponseEntity.badRequest().body(createApiError("Json can't be parsed or body is missing!",request, HttpStatus.BAD_REQUEST));
    }


    //--@MissingHeader exceptions
    @ExceptionHandler(value = {MissingRequestHeaderException.class})
    public ResponseEntity<ApiError> handleMissingRequestHeaderException(MissingRequestHeaderException exception, WebRequest request){

        return ResponseEntity.badRequest().body(createApiError(exception.getMessage(),request,HttpStatus.BAD_REQUEST));

    }

    //--@Runtime Exceptions
    @ExceptionHandler(BaseException.class)
    public ResponseEntity<ApiError> handleBaseException(BaseException exception,
                                                        WebRequest request){
        MessageType messageType = exception.getErrorMessage().getMessageType();
        HttpStatus status = messageType.getHttpStatus();
        return ResponseEntity.status(status).body(createApiError(exception.getMessage(),request,status));
    }

    //--@Valid check
    @ExceptionHandler(value = {MethodArgumentNotValidException.class})
    public ResponseEntity<ApiError> handleMethodArgumentNotValidException(MethodArgumentNotValidException exception,WebRequest request){
        Map<String, List<String>> errorsMap = new HashMap<>();
        for(ObjectError objError : exception.getBindingResult().getAllErrors()){

            String fieldName = ((FieldError)objError).getField();
            if(errorsMap.containsKey(fieldName)){
                errorsMap.put(fieldName,addToList(errorsMap.get(fieldName),objError.getDefaultMessage()));
            }
            else{
                errorsMap.put(fieldName,addToList(new ArrayList<>(),objError.getDefaultMessage()));
            }
        }
        return ResponseEntity.badRequest().body(createApiError(errorsMap,request,HttpStatus.BAD_REQUEST));
    }



    private List<String> addToList(List<String> list, String message){
        list.add(message);
        return list;
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ApiError> handleMethodNotSupported(
            HttpRequestMethodNotSupportedException exception, WebRequest request
    ) {
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED)
                .body(createApiError(exception.getMessage(), request, HttpStatus.METHOD_NOT_ALLOWED));
    }

    @ExceptionHandler(AuthorizationDeniedException.class)
    public ResponseEntity<ApiError> handleAuthorizationDenied(
            AuthorizationDeniedException exception, WebRequest request
    ) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(createApiError("Bu işlem için yetkin yok.", request, HttpStatus.FORBIDDEN));
    }


    public <E> ApiError<E> createApiError(E message, WebRequest request, HttpStatus status){

        ApiError<E> apiError = new ApiError<>();
        apiError.setStatus(status.value());
        Exception<E> exception = new Exception<>();
        exception.setCreateTime(new Date());
        //exception.setHostName(ApiKeyAuthFilter.getHostName());
        exception.setPath(request.getDescription(false).substring(4));
        exception.setMessage(message);
        apiError.setException(exception);

        return apiError;
    }
}
