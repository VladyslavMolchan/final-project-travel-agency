package com.epam.finaltask.restcontroller;

import com.epam.finaltask.dto.VoucherDTO;
import com.epam.finaltask.service.VoucherService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;



@RestController
@RequestMapping("/api/vouchers")
public class VoucherRestController {

    private final VoucherService voucherService;

    public VoucherRestController(VoucherService voucherService) {
        this.voucherService = voucherService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<VoucherDTO>>> findAll() {
        List<VoucherDTO> vouchers = voucherService.findAll();
        return ResponseEntity.ok(new ApiResponse<>("OK", "Success", vouchers));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<List<VoucherDTO>>> findAllByUserId(@PathVariable String userId) {
        List<VoucherDTO> vouchers = voucherService.findAllByUserId(userId);
        return ResponseEntity.ok(new ApiResponse<>("OK", "Success", vouchers));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<VoucherDTO>> createVoucher(@RequestBody VoucherDTO dto) {
        VoucherDTO created = voucherService.create(dto);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new ApiResponse<>("OK", "Voucher is successfully created", created));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<VoucherDTO>> updateVoucher(
            @PathVariable String id,
            @RequestBody VoucherDTO dto) {
        VoucherDTO updated = voucherService.update(id, dto);
        return ResponseEntity.ok(new ApiResponse<>("OK", "Voucher is successfully updated", updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteVoucher(@PathVariable String id) {
        voucherService.delete(id);
        return ResponseEntity.ok(new ApiResponse<>("OK", "Voucher with Id " + id + " has been deleted", null));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<VoucherDTO>> changeVoucherStatus(
            @PathVariable String id,
            @RequestBody VoucherDTO dto) {
        VoucherDTO changed = voucherService.changeHotStatus(id, dto);
        return ResponseEntity.ok(new ApiResponse<>("OK", "Voucher status is successfully changed", changed));
    }
}
