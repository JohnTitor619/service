package com.lxw.framework.exception;

import com.lxw.framework.model.response.ResultCode;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @AUTHOR: yadong
 * @DATE: 2022/8/30 11:09
 * @DESC:
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomException extends  RuntimeException{

    private ResultCode resultCode;
}
