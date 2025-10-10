package com.epam.finaltask.restcontroller;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.epam.finaltask.dto.VoucherDTO;
import com.epam.finaltask.service.VoucherService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.*;

import com.fasterxml.jackson.databind.ObjectMapper;

class VoucherRestControllerTest {

    @Mock
    private VoucherService voucherService;

    @InjectMocks
    private VoucherRestController voucherRestController;

    private MockMvc mockMvc;

    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(voucherRestController).build();
    }

    @Test
    void findAll_ShouldReturnListOfVouchers() throws Exception {
        List<VoucherDTO> vouchers = List.of(new VoucherDTO(), new VoucherDTO());
        when(voucherService.findAll()).thenReturn(vouchers);

        mockMvc.perform(get("/api/vouchers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value("OK"))
                .andExpect(jsonPath("$.results").isArray())
                .andExpect(jsonPath("$.results.length()").value(vouchers.size()));

        verify(voucherService).findAll();
    }

    @Test
    void findAllByUserId_ShouldReturnVouchersForUser() throws Exception {
        String userId = "user123";
        List<VoucherDTO> vouchers = List.of(new VoucherDTO());
        when(voucherService.findAllByUserId(userId)).thenReturn(vouchers);

        mockMvc.perform(get("/api/vouchers/user/{userId}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value("OK"))
                .andExpect(jsonPath("$.results").isArray())
                .andExpect(jsonPath("$.results.length()").value(vouchers.size()));

        verify(voucherService).findAllByUserId(userId);
    }

    @Test
    void createVoucher_ShouldReturnCreatedVoucher() throws Exception {
        VoucherDTO voucher = new VoucherDTO();
        voucher.setId("voucher1");

        when(voucherService.create(any(VoucherDTO.class))).thenReturn(voucher);

        mockMvc.perform(post("/api/vouchers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(voucher)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.statusCode").value("OK"))
                .andExpect(jsonPath("$.results.id").value("voucher1"));

        verify(voucherService).create(any(VoucherDTO.class));
    }

    @Test
    void updateVoucher_ShouldReturnUpdatedVoucher() throws Exception {
        String id = "voucher1";
        VoucherDTO voucher = new VoucherDTO();
        voucher.setId(id);

        when(voucherService.update(eq(id), any(VoucherDTO.class))).thenReturn(voucher);

        mockMvc.perform(patch("/api/vouchers/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(voucher)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value("OK"))
                .andExpect(jsonPath("$.results.id").value(id));

        verify(voucherService).update(eq(id), any(VoucherDTO.class));
    }

    @Test
    void deleteVoucher_ShouldReturnOk() throws Exception {
        String id = "voucher1";

        doNothing().when(voucherService).delete(id);

        mockMvc.perform(delete("/api/vouchers/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value("OK"))
                .andExpect(jsonPath("$.statusMessage").value("Voucher with Id " + id + " has been deleted"))
                .andExpect(jsonPath("$.results").doesNotExist());

        verify(voucherService).delete(id);
    }

    @Test
    void changeVoucherStatus_ShouldReturnUpdatedVoucher() throws Exception {
        String id = "voucher1";
        VoucherDTO voucher = new VoucherDTO();
        voucher.setId(id);

        when(voucherService.changeHotStatus(eq(id), any(VoucherDTO.class))).thenReturn(voucher);

        mockMvc.perform(patch("/api/vouchers/{id}/status", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(voucher)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value("OK"))
                .andExpect(jsonPath("$.results.id").value(id));

        verify(voucherService).changeHotStatus(eq(id), any(VoucherDTO.class));
    }
}
