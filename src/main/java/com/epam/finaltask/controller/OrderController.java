package com.epam.finaltask.controller;


import com.epam.finaltask.dto.OrderDTO;
import com.epam.finaltask.dto.VoucherDTO;
import com.epam.finaltask.service.OrderService;
import com.epam.finaltask.service.VoucherService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.List;

@Controller
@RequestMapping("/order")
public class OrderController {

    private final VoucherService voucherService;
    private final OrderService orderService;

    public OrderController(VoucherService voucherService, OrderService orderService) {
        this.voucherService = voucherService;
        this.orderService = orderService;
    }


    @GetMapping("/{voucherId}")
    public String showOrderForm(@PathVariable String voucherId, Model model) {
        VoucherDTO voucher = voucherService.findById(voucherId);
        model.addAttribute("voucher", voucher);

        OrderDTO dto = new OrderDTO();
        dto.setVoucherId(voucherId);
        model.addAttribute("order", dto);

        return "user/order-form";
    }


    @PostMapping
    public String submitOrder(@ModelAttribute("order") OrderDTO orderDTO, Model model) {
        OrderDTO created = orderService.createOrder(orderDTO);
        VoucherDTO voucher = voucherService.findById(orderDTO.getVoucherId());

        model.addAttribute("order", created);
        model.addAttribute("voucher", voucher);

        return "user/order-success";
    }


    @GetMapping("/history")
    public String orderHistory(Model model) {
        List<OrderDTO> orders = orderService.getCurrentUserOrders();
        model.addAttribute("orders", orders);
        return "user/order-history";
    }


    @GetMapping("/view/{orderId}")
    public String viewOrder(@PathVariable String orderId, Model model) {
        return orderService.getOrderById(orderId)
                .map(order -> {
                    model.addAttribute("order", order);
                    return "user/order-view";
                })
                .orElse("redirect:/order/history?error=Order not found");
    }

    @PostMapping("/cancel/{orderId}")
    public String cancelOrder(@PathVariable String orderId,

                              RedirectAttributes redirectAttributes) {
        boolean cancelled = orderService.cancelOrder(orderId);

        if (cancelled) {
            redirectAttributes.addFlashAttribute("success", "✅ Order cancelled successfully");
        } else {
            redirectAttributes.addFlashAttribute("error", "⚠️ Unable to cancel order");
        }

            return "redirect:/order/history";

    }
}
