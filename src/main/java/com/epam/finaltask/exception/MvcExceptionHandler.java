package com.epam.finaltask.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.NoHandlerFoundException;
import java.nio.file.AccessDeniedException;

@Slf4j
@ControllerAdvice
public class MvcExceptionHandler {

    @ExceptionHandler(VoucherNotFoundException.class)
    public String handleVoucherNotFound(VoucherNotFoundException ex, Model model) {
        log.warn("Voucher not found (MVC): {}", ex.getMessage());
        model.addAttribute("errorMessage", ex.getMessage());
        return "error/404";
    }

    @ExceptionHandler(OrderCreationException.class)
    public String handleOrderError(OrderCreationException ex, Model model) {
        log.warn("Order creation failed (MVC): {}", ex.getMessage());
        model.addAttribute("errorMessage", ex.getMessage());
        return "user/order-failed";
    }

    @ExceptionHandler(UserNotFoundException.class)
    public String handleUserNotFound(UserNotFoundException ex, Model model) {
        log.warn("User not found (MVC): {}", ex.getMessage());
        model.addAttribute("errorMessage", ex.getMessage());
        return "user/404";
    }

    @ExceptionHandler(AccessDeniedException.class)
    public String handleAccessDenied(AccessDeniedException ex, Model model) {
        log.warn("Access denied (MVC): {}", ex.getMessage());
        model.addAttribute("errorMessage", "You don’t have access to this page.");
        return "error/403";
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public String handleNotFound(NoHandlerFoundException ex, Model model) {
        log.warn("Page not found (MVC): {}", ex.getRequestURL());
        model.addAttribute("errorMessage", "Page not found: " + ex.getRequestURL());
        return "error/404";
    }

    @ExceptionHandler(Exception.class)
    public String handleGeneralError(Exception ex, Model model) {
        log.error("Unexpected error (MVC): ", ex);
        model.addAttribute("errorMessage", "Unexpected error: " + ex.getMessage());
        return "error/general";
    }
}
