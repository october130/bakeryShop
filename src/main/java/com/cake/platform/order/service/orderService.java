package com.cake.platform.order.service;

import com.cake.platform.common.result.Result;
import com.cake.platform.order.dto.OrderDTO;
import com.cake.platform.order.vo.OrderVO;

public interface orderService {
    Result<OrderVO> createOrder(OrderDTO orderDTO);

    Result<OrderVO> payOrder(OrderDTO orderDTO);
}
