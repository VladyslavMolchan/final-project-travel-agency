package com.epam.finaltask.controller;

import com.epam.finaltask.dto.OrderDTO;
import com.epam.finaltask.dto.VoucherDTO;
import com.epam.finaltask.model.Order;
import com.epam.finaltask.service.OrderService;
import com.epam.finaltask.service.VoucherService;
import com.epam.finaltask.service.PdfService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.List;

@Controller
@RequestMapping("/order")
@RequiredArgsConstructor
@Slf4j
public class OrderController {

    private final VoucherService voucherService;
    private final OrderService orderService;
    private final PdfService pdfService;


    @GetMapping("/{voucherId}")
    public String showOrderForm(@PathVariable String voucherId, Model model) {
        log.info("Displaying order form for voucherId={}", voucherId);

        VoucherDTO voucher = voucherService.findById(voucherId);
        model.addAttribute("voucher", voucher);

        OrderDTO dto = new OrderDTO();
        dto.setVoucherId(voucherId);
        model.addAttribute("order", dto);

        return "user/order-form";
    }


    @PostMapping
    public String submitOrder(@ModelAttribute("order") OrderDTO orderDTO, Model model) {
        log.info("Submitting order for voucherId={}", orderDTO.getVoucherId());

        OrderDTO created = orderService.createOrder(orderDTO);
        VoucherDTO voucher = voucherService.findById(orderDTO.getVoucherId());

        log.info("Order submitted successfully: orderId={}", created.getId());


        created.setVoucherTitle(voucher.getTitle());
        created.setVoucherDescription(voucher.getDescription());
        created.setVoucherPrice(voucher.getPrice());
        created.setVoucherTourType(voucher.getTourType());
        created.setVoucherTransferType(voucher.getTransferType());
        created.setVoucherHotelType(voucher.getHotelType());
        created.setVoucherArrivalDate(voucher.getArrivalDate());
        created.setVoucherEvictionDate(voucher.getEvictionDate());

        model.addAttribute("order", created);
        model.addAttribute("voucher", voucher);

        return "user/order-success";
    }


    @GetMapping("/history")
    public String orderHistory(Model model) {
        log.info("Fetching current user's order history");

        List<OrderDTO> orders = orderService.getCurrentUserOrders();

        log.info("Found {} orders for current user", orders.size());

        // Для кожного замовлення підвантажуємо інформацію про ваучер
        for (OrderDTO order : orders) {
            VoucherDTO voucher = voucherService.findById(order.getVoucherId());
            order.setVoucherTitle(voucher.getTitle());
            order.setVoucherDescription(voucher.getDescription());
            order.setVoucherPrice(voucher.getPrice());
            order.setVoucherTourType(voucher.getTourType());
            order.setVoucherTransferType(voucher.getTransferType());
            order.setVoucherHotelType(voucher.getHotelType());
            order.setVoucherArrivalDate(voucher.getArrivalDate());
            order.setVoucherEvictionDate(voucher.getEvictionDate());
        }

        model.addAttribute("orders", orders);
        return "user/order-history";
    }


    @GetMapping("/view/{orderId}")
    public String viewOrder(@PathVariable String orderId, Model model) {
        log.info("Viewing order with ID={}", orderId);

        return orderService.getOrderById(orderId)
                .map(order -> {
                    VoucherDTO voucher = voucherService.findById(order.getVoucherId());

                    order.setVoucherTitle(voucher.getTitle());
                    order.setVoucherDescription(voucher.getDescription());
                    order.setVoucherPrice(voucher.getPrice());
                    order.setVoucherTourType(voucher.getTourType());
                    order.setVoucherTransferType(voucher.getTransferType());
                    order.setVoucherHotelType(voucher.getHotelType());
                    order.setVoucherArrivalDate(voucher.getArrivalDate());
                    order.setVoucherEvictionDate(voucher.getEvictionDate());

                    model.addAttribute("order", order);
                    return "user/order-view";
                })
                .orElseGet(() -> {
                    log.warn("Order with ID={} not found", orderId);
                    return "redirect:/order/history?error=Order not found";
                });
    }


    @PostMapping("/cancel/{orderId}")
    public String cancelOrder(@PathVariable String orderId,
                              RedirectAttributes redirectAttributes) {
        log.info("Attempting to cancel order with ID={}", orderId);

        boolean cancelled = orderService.cancelOrder(orderId);

        if (cancelled) {
            log.info("Order ID={} cancelled successfully", orderId);
            redirectAttributes.addFlashAttribute("success", "✅ Order cancelled successfully");
        } else {
            log.warn("Failed to cancel order ID={}", orderId);
            redirectAttributes.addFlashAttribute("error", "⚠️ Unable to cancel order");
        }

        return "redirect:/order/history";
    }


    @GetMapping("/receipt/{orderId}")
    public void downloadReceipt(@PathVariable String orderId, HttpServletResponse response) throws IOException {
        log.info("Generating PDF receipt for order ID={}", orderId);

        Order order = orderService.getOrderEntityById(orderId)
                .orElseThrow(() -> {
                    log.error("Order not found with ID={}", orderId);
                    return new RuntimeException("Order not found");
                });

        try {
            byte[] pdfBytes = pdfService.generateOrderReceipt(order);
            response.setContentType("application/pdf");
            response.setHeader("Content-Disposition", "attachment; filename=order_" + orderId + ".pdf");
            response.getOutputStream().write(pdfBytes);

            log.info("PDF receipt for order ID={} generated and sent", orderId);

        } catch (Exception e) {
            log.error("Error generating PDF for order ID={}", orderId, e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Could not generate PDF");
        }
    }
}
