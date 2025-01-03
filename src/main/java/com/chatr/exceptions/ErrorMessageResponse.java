package com.chatr.exceptions;



import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ErrorMessageResponse {

    public ErrorMessageResponse(String error){
        this.error =error;
    }

    
    private String error;


    private String message = "";


}
