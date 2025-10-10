package com.epam.finaltask.service;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.*;

import com.epam.finaltask.dto.OrderDTO;
import com.epam.finaltask.mapper.OrderMapper;
import com.epam.finaltask.model.Order;
import com.epam.finaltask.model.Voucher;
import com.epam.finaltask.repository.OrderRepository;
import com.epam.finaltask.repository.VoucherRepository;
import com.epam.finaltask.security.CustomUserDetails;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

public class OrderServiceImplTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private VoucherRepository voucherRepository;

    @Mock
    private OrderMapper orderMapper;

    @InjectMocks
    private OrderServiceImpl orderService;

    @Mock
    private Authentication authentication;

    @Mock
    private SecurityContext securityContext;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);

        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);

        CustomUserDetails userDetails = mock(CustomUserDetails.class);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUsername()).thenReturn("testUser");
        when(userDetails.getEmail()).thenReturn("testuser@example.com");
    }

    @Test
    public void testCreateOrder_Success() {
        OrderDTO inputDto = new OrderDTO();
        String voucherId = UUID.randomUUID().toString();
        inputDto.setVoucherId(voucherId);

        Voucher voucher = new Voucher();
        UUID voucherUUID = UUID.fromString(voucherId);
        when(voucherRepository.findById(voucherUUID)).thenReturn(Optional.of(voucher));

        Order savedOrder = new Order();
        savedOrder.setId(UUID.randomUUID());
        savedOrder.setStatus("CREATED");

        when(orderRepository.save(any(Order.class))).thenReturn(savedOrder);

        OrderDTO outputDto = new OrderDTO();
        when(orderMapper.toOrderDTO(savedOrder)).thenReturn(outputDto);

        OrderDTO result = orderService.createOrder(inputDto);

        assertNotNull(result);
        verify(voucherRepository).findById(voucherUUID);
        verify(orderRepository).save(any(Order.class));
        verify(orderMapper).toOrderDTO(savedOrder);
    }

    @Test
    public void testCreateOrder_VoucherNotFound() {
        OrderDTO inputDto = new OrderDTO();
        String voucherId = UUID.randomUUID().toString();
        inputDto.setVoucherId(voucherId);

        UUID voucherUUID = UUID.fromString(voucherId);
        when(voucherRepository.findById(voucherUUID)).thenReturn(Optional.empty());

        RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
            orderService.createOrder(inputDto);
        });

        assertEquals("Voucher not found", thrown.getMessage());
    }

    @Test
    public void testGetOrdersByEmail() {
        String email = "test@example.com";

        Order order1 = new Order();
        Order order2 = new Order();
        List<Order> orders = Arrays.asList(order1, order2);

        when(orderRepository.findByCustomerEmail(email)).thenReturn(orders);

        OrderDTO dto1 = new OrderDTO();
        OrderDTO dto2 = new OrderDTO();
        when(orderMapper.toOrderDTO(order1)).thenReturn(dto1);
        when(orderMapper.toOrderDTO(order2)).thenReturn(dto2);

        List<OrderDTO> result = orderService.getOrdersByEmail(email);

        assertEquals(2, result.size());
        assertTrue(result.contains(dto1));
        assertTrue(result.contains(dto2));
    }

    @Test
    public void testGetOrderById_Found() {
        String orderId = UUID.randomUUID().toString();
        UUID uuid = UUID.fromString(orderId);

        Order order = new Order();
        when(orderRepository.findById(uuid)).thenReturn(Optional.of(order));

        OrderDTO dto = new OrderDTO();
        when(orderMapper.toOrderDTO(order)).thenReturn(dto);

        Optional<OrderDTO> result = orderService.getOrderById(orderId);

        assertTrue(result.isPresent());
        assertEquals(dto, result.get());
    }

    @Test
    public void testGetOrderById_NotFound() {
        String orderId = UUID.randomUUID().toString();
        UUID uuid = UUID.fromString(orderId);

        when(orderRepository.findById(uuid)).thenReturn(Optional.empty());

        Optional<OrderDTO> result = orderService.getOrderById(orderId);

        assertTrue(result.isEmpty());
    }

    @Test
    public void testCancelOrder_Success() {
        String orderId = UUID.randomUUID().toString();
        UUID uuid = UUID.fromString(orderId);

        Order order = new Order();
        when(orderRepository.findById(uuid)).thenReturn(Optional.of(order));

        boolean result = orderService.cancelOrder(orderId);

        assertTrue(result);
        assertEquals("CANCELLED", order.getStatus());
        verify(orderRepository).save(order);
    }

    @Test
    public void testCancelOrder_NotFound() {
        String orderId = UUID.randomUUID().toString();
        UUID uuid = UUID.fromString(orderId);

        when(orderRepository.findById(uuid)).thenReturn(Optional.empty());

        boolean result = orderService.cancelOrder(orderId);

        assertFalse(result);
        verify(orderRepository, never()).save(any());
    }

    @Test
    public void testGetCurrentUserOrders() {
        String email = "testuser@example.com";

        Order order = new Order();
        List<Order> orders = Collections.singletonList(order);

        when(orderRepository.findByCustomerEmail(email)).thenReturn(orders);

        OrderDTO dto = new OrderDTO();
        when(orderMapper.toOrderDTO(order)).thenReturn(dto);

        List<OrderDTO> result = orderService.getCurrentUserOrders();

        assertEquals(1, result.size());
        assertEquals(dto, result.get(0));
    }
}
