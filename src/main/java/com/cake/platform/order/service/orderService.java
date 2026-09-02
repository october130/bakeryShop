package com.cake.platform.order.service;

import com.cake.platform.common.result.Result;
import com.cake.platform.order.dto.OrderDTO;
import com.cake.platform.order.vo.OrderVO;

import java.util.List;

public interface orderService {
    Result<OrderVO> createOrder(OrderDTO orderDTO);

    Result<OrderVO> payOrder(OrderDTO orderDTO);

    Result<List<OrderVO>> listOrder();

    Result<OrderVO> getOrderDetail(Long orderId);

    Result cancelOrder(Long orderId);

    Result<List<OrderVO>> listAdminOrder();

    Result acceptOrder(Long orderId);

    Result deliverOrder(Long orderId);

    Result completeOrder(Long orderId);

    Result<OrderVO> getOrderAdminDetail(Long orderId);
}
