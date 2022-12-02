package com.lxw.framework.exception;

import com.lxw.framework.model.response.ResultCode;

/**
 * @AUTHOR: yadong
 * @DATE: 2022/8/30 12:11
 * @DESC:
 */
public class CastException {
    public static void cast(ResultCode resultCode){
        throw new  CustomException(resultCode);
    }
}
