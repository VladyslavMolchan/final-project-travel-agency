package com.epam.finaltask.service;

import com.epam.finaltask.dto.OrderDTO;
import com.epam.finaltask.model.Order;
import com.epam.finaltask.model.Voucher;
import com.epam.finaltask.repository.OrderRepository;
import com.epam.finaltask.repository.VoucherRepository;
import com.epam.finaltask.security.CustomUserDetails;
import com.epam.finaltask.mapper.OrderMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final VoucherRepository voucherRepository;
    private final OrderMapper orderMapper;

    @Override
    public OrderDTO createOrder(OrderDTO orderDTO) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails user = (CustomUserDetails) auth.getPrincipal();

        String username = user.getUsername();
        String email = user.getEmail();

        log.info("Creating new order for user: {} ({})", username, email);

        Voucher voucher = voucherRepository.findById(UUID.fromString(orderDTO.getVoucherId()))
                .orElseThrow(() -> new RuntimeException("Voucher not found"));

        Order order = new Order();
        order.setVoucher(voucher);
        order.setCustomerName(username);
        order.setCustomerEmail(email);
        order.setStatus("CREATED");

        Order saved = orderRepository.save(order);
        log.info("Order {} successfully created with status {}", saved.getId(), saved.getStatus());

        return orderMapper.toOrderDTO(saved);
    }

    @Override
    public List<OrderDTO> getOrdersByEmail(String email) {
        log.debug("Fetching orders for email: {}", email);
        return orderRepository.findByCustomerEmail(email)
                .stream()
                .map(orderMapper::toOrderDTO)
                .toList();
    }

    @Override
    public Optional<OrderDTO> getOrderById(String orderId) {
        log.debug("Fetching order by id {}", orderId);
        return orderRepository.findById(UUID.fromString(orderId))
                .map(orderMapper::toOrderDTO);
    }

    @Override
    public boolean cancelOrder(String orderId) {
        log.info("Cancelling order with id {}", orderId);

        Optional<Order> orderOpt = orderRepository.findById(UUID.fromString(orderId));
        if (orderOpt.isEmpty()) {
            log.warn("Order {} not found for cancellation", orderId);
            return false;
        }
        Order order = orderOpt.get();
        order.setStatus("CANCELLED");
        orderRepository.save(order);

        log.info("Order {} cancelled successfully", orderId);
        return true;
    }

    @Override
    public List<OrderDTO> getCurrentUserOrders() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails user = (CustomUserDetails) auth.getPrincipal();

        String email = user.getEmail();
        log.debug("Fetching current user orders for {}", email);

        return getOrdersByEmail(email);
    }

    @Override
    public Optional<Order> getOrderEntityById(String orderId) {
        log.debug("Fetching order entity by id {}", orderId);
        return orderRepository.findById(UUID.fromString(orderId));
    }
}
