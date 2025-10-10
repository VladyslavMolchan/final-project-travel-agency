package com.epam.finaltask.restcontroller;


import com.epam.finaltask.dto.VoucherDTO;
import com.epam.finaltask.service.VoucherService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/vouchers")
@Validated
public class VoucherRestController {

    private final VoucherService voucherService;

    public VoucherRestController(VoucherService voucherService) {
        this.voucherService = voucherService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<VoucherDTO>>> findAll() {
        log.info("Request to get all vouchers");
        List<VoucherDTO> vouchers = voucherService.findAll();
        log.info("Returning {} vouchers", vouchers.size());
        return ResponseEntity.ok(new ApiResponse<>("OK", "Success", vouchers));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<List<VoucherDTO>>> findAllByUserId(@PathVariable String userId) {
        log.info("Request to get vouchers for user with ID: {}", userId);
        List<VoucherDTO> vouchers = voucherService.findAllByUserId(userId);
        log.info("Returning {} vouchers for user {}", vouchers.size(), userId);
        return ResponseEntity.ok(new ApiResponse<>("OK", "Success", vouchers));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<VoucherDTO>> createVoucher(@RequestBody @Valid VoucherDTO dto) {
        log.info("Request to create voucher: {}", dto);
        VoucherDTO created = voucherService.create(dto);
        log.info("Voucher created with ID: {}", created.getId());
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new ApiResponse<>("OK", "Voucher is successfully created", created));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<VoucherDTO>> updateVoucher(
            @PathVariable String id,
            @RequestBody @Valid VoucherDTO dto) {
        log.info("Request to update voucher with ID: {}", id);
        VoucherDTO updated = voucherService.update(id, dto);
        log.info("Voucher with ID {} updated successfully", id);
        return ResponseEntity.ok(new ApiResponse<>("OK", "Voucher is successfully updated", updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteVoucher(@PathVariable String id) {
        log.info("Request to delete voucher with ID: {}", id);
        voucherService.delete(id);
        log.info("Voucher with ID {} deleted successfully", id);
        return ResponseEntity.ok(new ApiResponse<>("OK", "Voucher with Id " + id + " has been deleted", null));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<VoucherDTO>> changeVoucherStatus(
            @PathVariable String id,
            @RequestBody VoucherDTO dto) {
        log.info("Request to change status of voucher with ID: {}", id);
        VoucherDTO changed = voucherService.changeHotStatus(id, dto);
        log.info("Voucher with ID {} status changed successfully", id);
        return ResponseEntity.ok(new ApiResponse<>("OK", "Voucher status is successfully changed", changed));
    }
}
