package com.epam.finaltask.controller;

import com.epam.finaltask.dto.VoucherDTO;
import com.epam.finaltask.service.ReviewService;
import com.epam.finaltask.service.VoucherService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Controller
@RequestMapping("/dashboard")
@Slf4j
public class VoucherController {

    private final VoucherService voucherService;
    private final ReviewService reviewService;

    public VoucherController(VoucherService voucherService, ReviewService reviewService) {
        this.voucherService = voucherService;
        this.reviewService = reviewService;
    }

    @GetMapping("/{id}")
    public String showVoucherDetails(@PathVariable UUID id, Model model) {
        VoucherDTO voucher = voucherService.findById(id.toString());
        model.addAttribute("voucher", voucher);
        model.addAttribute("reviews", reviewService.getReviewsByVoucherId(id));
        return "voucher-details";
    }

    @GetMapping
    public String showDashboard(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String direction,
            @RequestParam(required = false) String search,
            @RequestParam(required = false, name = "hot") String hotParam,
            @RequestParam(required = false) String tourType,
            @RequestParam(required = false) String hotelType,
            Model model) {

        log.info("Dashboard requested — page={}, size={}, sortBy={}, direction={}, search={}, hot={}, tourType={}, hotelType={}",
                page, size, sortBy, direction, search, hotParam, tourType, hotelType);

        Boolean hot = null;
        if (hotParam != null && !hotParam.isBlank()) {
            if ("true".equalsIgnoreCase(hotParam)) {
                hot = true;
            } else if ("false".equalsIgnoreCase(hotParam)) {
                hot = false;
            } else {
                log.warn("Invalid 'hot' parameter received: {}", hotParam);
            }
        }

        Sort sort = "asc".equalsIgnoreCase(direction)
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        PageRequest pageRequest = PageRequest.of(page, size, sort);
        Page<VoucherDTO> voucherPage = voucherService.findFiltered(search, hot, tourType, hotelType, pageRequest);

        model.addAttribute("vouchers", voucherPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", voucherPage.getTotalPages());
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("direction", direction);
        model.addAttribute("search", search == null ? "" : search);
        model.addAttribute("hotParam", hotParam == null ? "" : hotParam);
        model.addAttribute("tourType", tourType == null ? "" : tourType);
        model.addAttribute("hotelType", hotelType == null ? "" : hotelType);
        model.addAttribute("noResults", voucherPage.isEmpty());

        return "user/dashboard";
    }
}
