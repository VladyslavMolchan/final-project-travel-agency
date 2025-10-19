package com.epam.finaltask.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDate;

@Data
public class OrderDTO {

    private String id;

    @NotBlank(message = "Voucher ID is required")
    private String voucherId;

    @NotBlank(message = "Customer name is required")
    private String customerName;

    @Email(message = "Invalid email format")
    @NotBlank(message = "Customer email is required")
    private String customerEmail;

    @NotBlank(message = "Order status is required")
    private String status;

    // 🔹 Додаткові поля для відображення інформації про ваучер
    private String voucherTitle;
    private String voucherDescription;
    private Double voucherPrice;
    private String voucherTourType;
    private String voucherTransferType;
    private String voucherHotelType;
    private LocalDate voucherArrivalDate;
    private LocalDate voucherEvictionDate;
}
