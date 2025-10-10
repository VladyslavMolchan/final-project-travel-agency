package com.epam.finaltask.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.util.*;

import com.epam.finaltask.dto.VoucherDTO;
import com.epam.finaltask.exception.UserNotFoundException;
import com.epam.finaltask.exception.VoucherNotFoundException;
import com.epam.finaltask.mapper.VoucherMapper;
import com.epam.finaltask.model.*;
import com.epam.finaltask.repository.UserRepository;
import com.epam.finaltask.repository.VoucherRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

public class VoucherServiceImplTest {

    @Mock
    private VoucherRepository voucherRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private VoucherMapper voucherMapper;

    @InjectMocks
    private VoucherServiceImpl voucherService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void create_ShouldSaveAndReturnVoucherDTO() {
        VoucherDTO inputDto = new VoucherDTO();
        inputDto.setTitle("Test Voucher");

        Voucher voucher = new Voucher();
        Voucher savedVoucher = new Voucher();
        savedVoucher.setId(UUID.randomUUID());

        VoucherDTO savedDto = new VoucherDTO();

        when(voucherMapper.toVoucher(inputDto)).thenReturn(voucher);
        when(voucherRepository.save(voucher)).thenReturn(savedVoucher);
        when(voucherMapper.toVoucherDTO(savedVoucher)).thenReturn(savedDto);

        VoucherDTO result = voucherService.create(inputDto);

        assertNotNull(result);
        assertEquals(savedDto, result);

        verify(voucherMapper).toVoucher(inputDto);
        verify(voucherRepository).save(voucher);
        verify(voucherMapper).toVoucherDTO(savedVoucher);
    }

    @Test
    void order_ShouldAssignUserAndStatusAndSave() {
        String voucherId = UUID.randomUUID().toString();
        String userId = UUID.randomUUID().toString();

        Voucher voucher = new Voucher();
        voucher.setId(UUID.fromString(voucherId));

        User user = new User();
        user.setId(UUID.fromString(userId));

        Voucher savedVoucher = new Voucher();
        savedVoucher.setId(UUID.fromString(voucherId));

        VoucherDTO savedDto = new VoucherDTO();

        when(voucherRepository.findById(UUID.fromString(voucherId))).thenReturn(Optional.of(voucher));
        when(userRepository.findById(UUID.fromString(userId))).thenReturn(Optional.of(user));
        when(voucherRepository.save(voucher)).thenReturn(savedVoucher);
        when(voucherMapper.toVoucherDTO(savedVoucher)).thenReturn(savedDto);

        VoucherDTO result = voucherService.order(voucherId, userId);

        assertNotNull(result);
        assertEquals(savedDto, result);
        assertEquals(user, voucher.getUser());
        assertEquals(VoucherStatus.REGISTERED, voucher.getStatus());

        verify(voucherRepository).findById(UUID.fromString(voucherId));
        verify(userRepository).findById(UUID.fromString(userId));
        verify(voucherRepository).save(voucher);
        verify(voucherMapper).toVoucherDTO(savedVoucher);
    }

    @Test
    void order_VoucherNotFound_ShouldThrowException() {
        String voucherId = UUID.randomUUID().toString();
        String userId = UUID.randomUUID().toString();

        when(voucherRepository.findById(UUID.fromString(voucherId))).thenReturn(Optional.empty());

        VoucherNotFoundException ex = assertThrows(VoucherNotFoundException.class, () -> {
            voucherService.order(voucherId, userId);
        });

        assertTrue(ex.getMessage().contains("Voucher with id"));
    }

    @Test
    void order_UserNotFound_ShouldThrowException() {
        String voucherId = UUID.randomUUID().toString();
        String userId = UUID.randomUUID().toString();

        Voucher voucher = new Voucher();

        when(voucherRepository.findById(UUID.fromString(voucherId))).thenReturn(Optional.of(voucher));
        when(userRepository.findById(UUID.fromString(userId))).thenReturn(Optional.empty());

        UserNotFoundException ex = assertThrows(UserNotFoundException.class, () -> {
            voucherService.order(voucherId, userId);
        });

        assertTrue(ex.getMessage().contains("User with id"));
    }

    @Test
    void update_ShouldUpdateVoucherAndReturnDTO() {
        String voucherId = UUID.randomUUID().toString();

        Voucher existingVoucher = new Voucher();
        existingVoucher.setId(UUID.fromString(voucherId));

        VoucherDTO dto = new VoucherDTO();
        dto.setTitle("Updated title");
        dto.setDescription("Updated description");
        dto.setPrice(100.0);
        dto.setTourType("LEISURE");
        dto.setTransferType("PRIVATE_CAR");
        dto.setHotelType("FOUR_STARS");
        dto.setStatus("REGISTERED");
        dto.setArrivalDate(LocalDate.now());
        dto.setEvictionDate(LocalDate.now().plusDays(5));
        dto.setHot(true);

        Voucher savedVoucher = new Voucher();
        savedVoucher.setId(UUID.fromString(voucherId));

        VoucherDTO savedDto = new VoucherDTO();

        when(voucherRepository.findById(UUID.fromString(voucherId))).thenReturn(Optional.of(existingVoucher));
        when(voucherRepository.save(existingVoucher)).thenReturn(savedVoucher);
        when(voucherMapper.toVoucherDTO(savedVoucher)).thenReturn(savedDto);

        VoucherDTO result = voucherService.update(voucherId, dto);

        assertNotNull(result);
        assertEquals(savedDto, result);
        assertEquals("Updated title", existingVoucher.getTitle());
        assertEquals("Updated description", existingVoucher.getDescription());
        assertEquals(100.0, existingVoucher.getPrice());
        assertEquals(TourType.LEISURE, existingVoucher.getTourType());
        assertEquals(TransferType.PRIVATE_CAR, existingVoucher.getTransferType());
        assertEquals(HotelType.FOUR_STARS, existingVoucher.getHotelType());
        assertEquals(VoucherStatus.REGISTERED, existingVoucher.getStatus());
        assertTrue(existingVoucher.isHot());

        verify(voucherRepository).findById(UUID.fromString(voucherId));
        verify(voucherRepository).save(existingVoucher);
        verify(voucherMapper).toVoucherDTO(savedVoucher);
    }

    @Test
    void update_VoucherNotFound_ShouldThrowException() {
        String voucherId = UUID.randomUUID().toString();
        VoucherDTO dto = new VoucherDTO();

        when(voucherRepository.findById(UUID.fromString(voucherId))).thenReturn(Optional.empty());

        VoucherNotFoundException ex = assertThrows(VoucherNotFoundException.class, () -> {
            voucherService.update(voucherId, dto);
        });

        assertTrue(ex.getMessage().contains("Voucher with id"));
    }

    @Test
    void delete_ShouldDeleteVoucher() {
        String voucherId = UUID.randomUUID().toString();
        Voucher voucher = new Voucher();

        when(voucherRepository.findById(UUID.fromString(voucherId))).thenReturn(Optional.of(voucher));

        voucherService.delete(voucherId);

        verify(voucherRepository).delete(voucher);
    }

    @Test
    void delete_VoucherNotFound_ShouldThrowException() {
        String voucherId = UUID.randomUUID().toString();

        when(voucherRepository.findById(UUID.fromString(voucherId))).thenReturn(Optional.empty());

        VoucherNotFoundException ex = assertThrows(VoucherNotFoundException.class, () -> {
            voucherService.delete(voucherId);
        });

        assertTrue(ex.getMessage().contains("Voucher with id"));
    }

    @Test
    void changeHotStatus_ShouldChangeHotAndReturnDTO() {
        String voucherId = UUID.randomUUID().toString();
        Voucher voucher = new Voucher();
        voucher.setHot(false);

        VoucherDTO dto = new VoucherDTO();
        dto.setHot(true);

        Voucher savedVoucher = new Voucher();
        savedVoucher.setHot(true);

        VoucherDTO savedDto = new VoucherDTO();
        savedDto.setHot(true);

        when(voucherRepository.findById(UUID.fromString(voucherId))).thenReturn(Optional.of(voucher));
        when(voucherRepository.save(voucher)).thenReturn(savedVoucher);
        when(voucherMapper.toVoucherDTO(savedVoucher)).thenReturn(savedDto);

        VoucherDTO result = voucherService.changeHotStatus(voucherId, dto);

        assertTrue(result.isHot());
        verify(voucherRepository).save(voucher);
        verify(voucherMapper).toVoucherDTO(savedVoucher);
    }

    @Test
    void changeHotStatus_VoucherNotFound_ShouldThrowException() {
        String voucherId = UUID.randomUUID().toString();
        VoucherDTO dto = new VoucherDTO();

        when(voucherRepository.findById(UUID.fromString(voucherId))).thenReturn(Optional.empty());

        VoucherNotFoundException ex = assertThrows(VoucherNotFoundException.class, () -> {
            voucherService.changeHotStatus(voucherId, dto);
        });

        assertTrue(ex.getMessage().contains("Voucher with id"));
    }

    @Test
    void findAllByUserId_ShouldReturnVoucherDTOList() {
        UUID userId = UUID.randomUUID();
        Voucher voucher1 = new Voucher();
        Voucher voucher2 = new Voucher();

        List<Voucher> vouchers = Arrays.asList(voucher1, voucher2);
        VoucherDTO dto1 = new VoucherDTO();
        VoucherDTO dto2 = new VoucherDTO();

        when(voucherRepository.findAllByUserId(userId)).thenReturn(vouchers);
        when(voucherMapper.toVoucherDTO(voucher1)).thenReturn(dto1);
        when(voucherMapper.toVoucherDTO(voucher2)).thenReturn(dto2);

        List<VoucherDTO> result = voucherService.findAllByUserId(userId.toString());

        assertEquals(2, result.size());
        assertTrue(result.contains(dto1));
        assertTrue(result.contains(dto2));
    }

    @Test
    void findAll_ShouldReturnVoucherDTOList() {
        Voucher voucher1 = new Voucher();
        Voucher voucher2 = new Voucher();

        List<Voucher> vouchers = Arrays.asList(voucher1, voucher2);
        VoucherDTO dto1 = new VoucherDTO();
        VoucherDTO dto2 = new VoucherDTO();

        when(voucherRepository.findAll()).thenReturn(vouchers);
        when(voucherMapper.toVoucherDTO(voucher1)).thenReturn(dto1);
        when(voucherMapper.toVoucherDTO(voucher2)).thenReturn(dto2);

        List<VoucherDTO> result = voucherService.findAll();

        assertEquals(2, result.size());
        assertTrue(result.contains(dto1));
        assertTrue(result.contains(dto2));
    }

    @Test
    void findById_ShouldReturnVoucherDTO() {
        String voucherId = UUID.randomUUID().toString();
        Voucher voucher = new Voucher();
        VoucherDTO dto = new VoucherDTO();

        when(voucherRepository.findById(UUID.fromString(voucherId))).thenReturn(Optional.of(voucher));
        when(voucherMapper.toVoucherDTO(voucher)).thenReturn(dto);

        VoucherDTO result = voucherService.findById(voucherId);

        assertEquals(dto, result);
    }

    @Test
    void findById_VoucherNotFound_ShouldThrowException() {
        String voucherId = UUID.randomUUID().toString();

        when(voucherRepository.findById(UUID.fromString(voucherId))).thenReturn(Optional.empty());

        VoucherNotFoundException ex = assertThrows(VoucherNotFoundException.class, () -> {
            voucherService.findById(voucherId);
        });

        assertTrue(ex.getMessage().contains("Voucher with id"));
    }
}
