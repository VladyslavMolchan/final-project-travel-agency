package com.epam.finaltask.service;

import com.epam.finaltask.dto.OrderDTO;
import com.epam.finaltask.model.Order;

import java.util.List;
import java.util.Optional;

public interface OrderService {
    OrderDTO createOrder(OrderDTO orderDTO);
    List<OrderDTO> getOrdersByEmail(String email);
    Optional<OrderDTO> getOrderById(String orderId);
    boolean cancelOrder(String orderId);
    List<OrderDTO> getCurrentUserOrders();
    Optional<Order> getOrderEntityById(String orderId);
}

