package com.lxw.framework.domain.order.response;

import com.lxw.framework.domain.order.Orders;
import com.lxw.framework.model.response.ResponseResult;
import com.lxw.framework.model.response.ResultCode;
import lombok.Data;
import lombok.ToString;

/**
 * Created by mrt on 2018/3/26.
 */
@Data
@ToString
public class CreateOrderResult extends ResponseResult {
    private Orders xcOrders;
    public CreateOrderResult(ResultCode resultCode, Orders xcOrders) {
        super(resultCode);
        this.xcOrders = xcOrders;
    }


}
