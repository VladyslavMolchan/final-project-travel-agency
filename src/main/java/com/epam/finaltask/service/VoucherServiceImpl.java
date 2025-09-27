package com.epam.finaltask.service;

import com.epam.finaltask.dto.VoucherDTO;
import com.epam.finaltask.mapper.VoucherMapper;
import com.epam.finaltask.model.User;
import com.epam.finaltask.model.Voucher;
import com.epam.finaltask.repository.UserRepository;
import com.epam.finaltask.repository.VoucherRepository;
import org.springframework.stereotype.Service;
import com.epam.finaltask.model.VoucherStatus;
import com.epam.finaltask.model.TransferType;
import com.epam.finaltask.model.TourType;
import com.epam.finaltask.model.HotelType;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class VoucherServiceImpl implements VoucherService {

    private final VoucherRepository voucherRepository;
    private final UserRepository userRepository;
    private final VoucherMapper voucherMapper;

    public VoucherServiceImpl(VoucherRepository voucherRepository, UserRepository userRepository, VoucherMapper voucherMapper) {
        this.voucherRepository = voucherRepository;
        this.userRepository = userRepository;
        this.voucherMapper = voucherMapper;
    }

    @Override
    public VoucherDTO create(VoucherDTO voucherDTO) {

        Voucher voucher = voucherMapper.toVoucher(voucherDTO);
        Voucher savedVoucher = voucherRepository.save(voucher);

        return voucherMapper.toVoucherDTO(savedVoucher);
    }

    @Override
    public VoucherDTO order(String id, String userId) {
        Voucher voucher = voucherRepository.findById(UUID.fromString(id))
                .orElseThrow(() -> new RuntimeException("Voucher not found"));

        User user = userRepository.findById(UUID.fromString(userId))
                .orElseThrow(() -> new RuntimeException("User not found"));

        voucher.setUser(user);
        voucher.setStatus(VoucherStatus.REGISTERED);
        Voucher updatedVoucher = voucherRepository.save(voucher);
        return voucherMapper.toVoucherDTO(updatedVoucher);
    }

    @Override
    public VoucherDTO update(String id, VoucherDTO voucherDTO) {
        Voucher existingVoucher = voucherRepository.findById(UUID.fromString(id))
                .orElseThrow(() -> new RuntimeException("Voucher not found"));


        existingVoucher.setTitle(voucherDTO.getTitle());
        existingVoucher.setDescription(voucherDTO.getDescription());
        existingVoucher.setPrice(voucherDTO.getPrice());

        if (voucherDTO.getTourType() != null) {
            existingVoucher.setTourType(TourType.valueOf(voucherDTO.getTourType()));
        }
        if (voucherDTO.getTransferType() != null) {
            existingVoucher.setTransferType(TransferType.valueOf(voucherDTO.getTransferType()));
        }
        if (voucherDTO.getHotelType() != null) {
            existingVoucher.setHotelType(HotelType.valueOf(voucherDTO.getHotelType()));
        }
        if (voucherDTO.getStatus() != null) {
            existingVoucher.setStatus(VoucherStatus.valueOf(voucherDTO.getStatus()));
        }

        existingVoucher.setArrivalDate(voucherDTO.getArrivalDate());
        existingVoucher.setEvictionDate(voucherDTO.getEvictionDate());
        existingVoucher.setHot(voucherDTO.isHot());

        Voucher updatedVoucher = voucherRepository.save(existingVoucher);
        return voucherMapper.toVoucherDTO(updatedVoucher);
    }

    @Override
    public void delete(String voucherId) {
        Voucher voucher = voucherRepository.findById(UUID.fromString(voucherId))
                .orElseThrow(() -> new RuntimeException("Voucher not found"));
        voucherRepository.delete(voucher);
    }

    @Override
    public VoucherDTO changeHotStatus(String id, VoucherDTO voucherDTO) {
        Voucher voucher = voucherRepository.findById(UUID.fromString(id))
                .orElseThrow(() -> new RuntimeException("Voucher not found"));

        voucher.setHot(voucherDTO.isHot());

        Voucher updatedVoucher = voucherRepository.save(voucher);
        return voucherMapper.toVoucherDTO(updatedVoucher);
    }

    @Override
    public List<VoucherDTO> findAllByUserId(String userId) {
        List<Voucher> vouchers = voucherRepository.findAllByUserId(UUID.fromString(userId));
        return vouchers.stream()
                .map(voucherMapper::toVoucherDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<VoucherDTO> findAllByTourType(TourType tourType) {
        List<Voucher> vouchers = voucherRepository.findAllByTourType(tourType);
        return vouchers.stream()
                .map(voucherMapper::toVoucherDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<VoucherDTO> findAllByTransferType(String transferType) {
        List<Voucher> vouchers = voucherRepository.findAllByTransferType(TransferType.valueOf(transferType));
        return vouchers.stream()
                .map(voucherMapper::toVoucherDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<VoucherDTO> findAllByPrice(Double price) {
        List<Voucher> vouchers = voucherRepository.findAllByPrice(price);
        return vouchers.stream()
                .map(voucherMapper::toVoucherDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<VoucherDTO> findAllByHotelType(HotelType hotelType) {
        List<Voucher> vouchers = voucherRepository.findAllByHotelType(hotelType);
        return vouchers.stream()
                .map(voucherMapper::toVoucherDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<VoucherDTO> findAll() {
        List<Voucher> vouchers = voucherRepository.findAll();
        return vouchers.stream()
                .map(voucherMapper::toVoucherDTO)
                .collect(Collectors.toList());
    }
}
