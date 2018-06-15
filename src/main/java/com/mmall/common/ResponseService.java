package com.mmall.common;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import java.io.Serializable;

/**
 * Created by jia on 2018/6/1.
 *
 */
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class ResponseService<T> implements Serializable {

    private Integer status;
    private String msg;
    private T data;

    private ResponseService(Integer status){
        this.status = status;
    }
    private ResponseService(Integer status, String msg){
        this.status = status;
        this.msg = msg;

    }
    private ResponseService(Integer status, T data){
        this.status = status;
        this.data = data;
    }
    private ResponseService(Integer status, String msg, T data){
        this.status = status;
        this.msg = msg;
        this.data = data;
    }
    @JsonIgnore
    public boolean isSuccess(){
        return this.status == ResponseCode.SUCCESS.getCode();
    }

    public Integer getStatus() {
        return status;
    }

    public String getMsg() {
        return msg;
    }

    public T getData() {
        return data;
    }

    public static<T> ResponseService<T> createBySuccess(){
        return new  ResponseService<T>(ResponseCode.SUCCESS.getCode());
    }
    public static<T> ResponseService<T> createBySuccessMessage(String msg ){
        return new  ResponseService<T>(ResponseCode.SUCCESS.getCode(),msg);
    }
    public static<T> ResponseService<T> createBySuccess(T data){
        return new  ResponseService<T>(ResponseCode.SUCCESS.getCode(),data);
    }
    public static<T> ResponseService<T> createBySuccess(String msg,T data){
        return new  ResponseService<T>(ResponseCode.SUCCESS.getCode(),msg,data);
    }

    public static<T> ResponseService<T> createByError(){
        return new ResponseService<T>(ResponseCode.ERROR.getCode(),ResponseCode.ERROR.getDesc());
    }
    public static<T> ResponseService<T> createByErrorMessage(String errorMessage){
        return  new ResponseService<T>(ResponseCode.ERROR.getCode(),errorMessage);
    }
    public static<T> ResponseService<T> createByCodeErrorMessage(int errorCode ,String errorMessage){
        return  new ResponseService<T>(errorCode,errorMessage);
    }
}
