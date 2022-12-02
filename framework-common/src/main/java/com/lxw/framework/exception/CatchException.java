package com.lxw.framework.exception;

import com.google.common.collect.ImmutableMap;
import com.lxw.framework.model.response.CommonCode;
import com.lxw.framework.model.response.ResponseResult;
import com.lxw.framework.model.response.ResultCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.concurrent.TimeoutException;

/**
 * @AUTHOR: yadong
 * @DATE: 2022/8/30 11:13
 * @DESC:
 */
@ControllerAdvice
@Slf4j
public class CatchException {
    //使用Map来异常类型和错误代码映射
    private static ImmutableMap<Class<? extends Throwable>,ResultCode> immutableMap = null;

    private static ImmutableMap.Builder<Class<? extends Throwable>,ResultCode> builder = ImmutableMap.builder();

    static {
//        builder.put(HttpMessageNotReadableException.class,CommonCode.PARAMS_NULL);
//        builder.put(NullPointerException.class,CommonCode.PARAMS_NULL);
//        builder.put(TimeoutException.class,CommonCode.TIME_OUT);
        //..............
    }
    //捕获Exception异常
    @ExceptionHandler(CustomException.class)
    @ResponseBody
    public ResponseResult catchCustomException(CustomException customException){
        ResultCode resultCode = customException.getResultCode();
        return new ResponseResult(resultCode);
    }
    //捕获 CustomException异常
    @ExceptionHandler(Exception.class)
    @ResponseBody
    public ResponseResult catchException(Exception exception){
        log.error(exception.getMessage());

        if(immutableMap == null){
            //把builder中的值赋值immutableMap
            immutableMap = builder.build();
        }
        Class<? extends Exception> exceptionClass = exception.getClass();
        ResultCode resultCode = immutableMap.get(exceptionClass);
        if(resultCode != null ){
            return new ResponseResult(resultCode);
        }
        return new ResponseResult(CommonCode.SERVER_ERROR);
    }
}
