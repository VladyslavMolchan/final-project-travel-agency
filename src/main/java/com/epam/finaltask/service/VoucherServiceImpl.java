package com.epam.finaltask.service;

import com.epam.finaltask.dto.VoucherDTO;
import com.epam.finaltask.exception.UserNotFoundException;
import com.epam.finaltask.exception.VoucherNotFoundException;
import com.epam.finaltask.mapper.VoucherMapper;

import com.epam.finaltask.repository.UserRepository;
import com.epam.finaltask.repository.VoucherRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;


import com.epam.finaltask.model.HotelType;
import com.epam.finaltask.model.TourType;
import com.epam.finaltask.model.Voucher;
import com.epam.finaltask.model.TransferType;
import com.epam.finaltask.model.VoucherStatus;
import com.epam.finaltask.model.User;



import lombok.extern.slf4j.Slf4j;


@Slf4j
@Service
public class VoucherServiceImpl implements VoucherService {

    private final VoucherRepository voucherRepository;
    private final UserRepository userRepository;
    private final VoucherMapper voucherMapper;

    public VoucherServiceImpl(VoucherRepository voucherRepository,
                              UserRepository userRepository,
                              VoucherMapper voucherMapper) {
        this.voucherRepository = voucherRepository;
        this.userRepository = userRepository;
        this.voucherMapper = voucherMapper;
    }

    @Override
    public VoucherDTO create(VoucherDTO voucherDTO) {
        log.info("Creating voucher '{}'", voucherDTO.getTitle());
        Voucher voucher = voucherMapper.toVoucher(voucherDTO);
        Voucher saved = voucherRepository.save(voucher);
        log.info("Voucher {} created successfully", saved.getId());
        return voucherMapper.toVoucherDTO(saved);
    }

    @Override
    public VoucherDTO order(String id, String userId) {
        log.info("Ordering voucher {} for user {}", id, userId);
        Voucher voucher = voucherRepository.findById(UUID.fromString(id))
                .orElseThrow(() -> {
                    log.error("Voucher {} not found", id);
                    return new VoucherNotFoundException("Voucher with id " + id + " not found");
                });
        User user = userRepository.findById(UUID.fromString(userId))
                .orElseThrow(() -> {
                    log.error("User {} not found", userId);
                    return new UserNotFoundException("User with id " + userId + " not found");
                });

        voucher.setUser(user);
        voucher.setStatus(VoucherStatus.REGISTERED);

        Voucher saved = voucherRepository.save(voucher);
        log.info("Voucher {} successfully ordered by user {}", saved.getId(), userId);
        return voucherMapper.toVoucherDTO(saved);
    }

    @Override
    public VoucherDTO update(String id, VoucherDTO voucherDTO) {
        log.info("Updating voucher {}", id);
        Voucher existing = voucherRepository.findById(UUID.fromString(id))
                .orElseThrow(() -> {
                    log.error("Voucher {} not found for update", id);
                    return new VoucherNotFoundException("Voucher with id " + id + " not found");
                });

        existing.setTitle(voucherDTO.getTitle());
        existing.setDescription(voucherDTO.getDescription());
        existing.setPrice(voucherDTO.getPrice());
        if (voucherDTO.getTourType() != null) existing.setTourType(TourType.valueOf(voucherDTO.getTourType()));
        if (voucherDTO.getTransferType() != null) existing.setTransferType(TransferType.valueOf(voucherDTO.getTransferType()));
        if (voucherDTO.getHotelType() != null) existing.setHotelType(HotelType.valueOf(voucherDTO.getHotelType()));
        if (voucherDTO.getStatus() != null) existing.setStatus(VoucherStatus.valueOf(voucherDTO.getStatus()));
        existing.setArrivalDate(voucherDTO.getArrivalDate());
        existing.setEvictionDate(voucherDTO.getEvictionDate());
        existing.setHot(voucherDTO.isHot());

        Voucher saved = voucherRepository.save(existing);
        log.info("Voucher {} updated successfully", saved.getId());
        return voucherMapper.toVoucherDTO(saved);
    }

    @Override
    public void delete(String voucherId) {
        log.warn("Deleting voucher {}", voucherId);
        Voucher voucher = voucherRepository.findById(UUID.fromString(voucherId))
                .orElseThrow(() -> {
                    log.error("Voucher {} not found for deletion", voucherId);
                    return new VoucherNotFoundException("Voucher with id " + voucherId + " not found");
                });
        voucherRepository.delete(voucher);
        log.info("Voucher {} deleted", voucherId);
    }

    @Override
    public VoucherDTO changeHotStatus(String id, VoucherDTO voucherDTO) {
        log.info("Changing hot status of voucher {} → {}", id, voucherDTO.isHot());
        Voucher voucher = voucherRepository.findById(UUID.fromString(id))
                .orElseThrow(() -> new VoucherNotFoundException("Voucher with id " + id + " not found"));
        voucher.setHot(voucherDTO.isHot());
        return voucherMapper.toVoucherDTO(voucherRepository.save(voucher));
    }

    @Override
    public List<VoucherDTO> findAllByUserId(String userId) {
        log.debug("Fetching vouchers for user {}", userId);
        return voucherRepository.findAllByUserId(UUID.fromString(userId))
                .stream().map(voucherMapper::toVoucherDTO).collect(Collectors.toList());
    }

    @Override
    public List<VoucherDTO> findAllByTourType(TourType tourType) {
        log.debug("Fetching vouchers by tour type {}", tourType);
        return voucherRepository.findAllByTourType(tourType).stream()
                .map(voucherMapper::toVoucherDTO).collect(Collectors.toList());
    }

    @Override
    public List<VoucherDTO> findAllByTransferType(String transferType) {
        log.debug("Fetching vouchers by transfer type {}", transferType);
        return voucherRepository.findAllByTransferType(TransferType.valueOf(transferType)).stream()
                .map(voucherMapper::toVoucherDTO).collect(Collectors.toList());
    }

    @Override
    public List<VoucherDTO> findAllByPrice(Double price) {
        log.debug("Fetching vouchers by price {}", price);
        return voucherRepository.findAllByPrice(price).stream()
                .map(voucherMapper::toVoucherDTO).collect(Collectors.toList());
    }

    @Override
    public List<VoucherDTO> findAllByHotelType(HotelType hotelType) {
        log.debug("Fetching vouchers by hotel type {}", hotelType);
        return voucherRepository.findAllByHotelType(hotelType).stream()
                .map(voucherMapper::toVoucherDTO).collect(Collectors.toList());
    }

    @Override
    public List<VoucherDTO> findAll() {
        log.debug("Fetching all vouchers");
        return voucherRepository.findAll().stream()
                .map(voucherMapper::toVoucherDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Page<VoucherDTO> findAll(Pageable pageable) {
        log.debug("Fetching vouchers with paging {}", pageable);
        return voucherRepository.findAll(pageable).map(voucherMapper::toVoucherDTO);
    }

    @Override
    public Page<VoucherDTO> findFiltered(String search, Boolean hot, String tourType, String hotelType, Pageable pageable) {
        log.debug("Filtering vouchers with search='{}', hot={}, tourType={}, hotelType={}",
                search, hot, tourType, hotelType);
        Page<Voucher> page = voucherRepository.searchAll(
                (search != null && !search.isBlank()) ? search : null,
                pageable
        );
        return page.map(voucherMapper::toVoucherDTO);
    }

    @Override
    public VoucherDTO findById(String id) {
        log.debug("Fetching voucher by id {}", id);
        return voucherRepository.findById(UUID.fromString(id))
                .map(voucherMapper::toVoucherDTO)
                .orElseThrow(() -> {
                    log.error("Voucher {} not found", id);
                    return new VoucherNotFoundException("Voucher with id " + id + " not found");
                });
    }
}
