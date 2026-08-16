package com.cake.platform.order.service;

import com.cake.platform.common.result.Result;
import com.cake.platform.order.dto.OrderDTO;
import com.cake.platform.order.vo.OrderVO;

import java.util.List;

public interface orderService {
    Result<OrderVO> createOrder(OrderDTO orderDTO);

    Result payOrder(Long orderId);

    Result<List<OrderVO>>  listOrder();

    Result<OrderVO> getOrderDetail(Long orderId);

    Result cancelOrder(Long orderId);

    Result acceptOrder(Long orderId);

    Result deliverOrder(Long orderId);

    Result completeOrder(Long orderId);

    Result<List<OrderVO>> listAdminOrder();
}
