package com.multi_tenant.demo.Exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@ControllerAdvice
public class CustomExceptionHandler
{
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public ResponseEntity<?> MethodArgumentNotValidExceptionFunction(MethodArgumentNotValidException e)
    {
        Map<String,Object> res = new HashMap<>();
        res.put("msg","Validation Error");
        res.put("Status","400");
        List<Object> list = new ArrayList<>();
        e.getFieldErrors().forEach((error)-> {
            Map map = new HashMap<>();
            map.put("field",error.getField());
            map.put("message",error.getDefaultMessage());
            map.put("Object",error.getObjectName());
            list.add(map);
        });
        res.put("data",list);

        return new ResponseEntity<>(res, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<?> handleNullPointerException(NullPointerException e)
    {
        Map<String,Object> res = new HashMap<>();
        res.put("Error","NullPointer Exception occurred");
        res.put("Status","400");
        res.put("Message", e.getMessage());
        Throwable cause = e.getCause();
        if (cause != null) {
            res.put("caused by", cause.getMessage());
        }

        return new ResponseEntity<>(res, HttpStatus.BAD_REQUEST);
    }
}
