package com.epam.finaltask.repository;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;
import java.util.UUID;
import com.epam.finaltask.model.Voucher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import com.epam.finaltask.model.HotelType;
import com.epam.finaltask.model.TourType;
import com.epam.finaltask.model.TransferType;
import java.util.List;



public interface VoucherRepository extends JpaRepository<Voucher, UUID> {

    List<Voucher> findAllByUserId(UUID userId);
    List<Voucher> findAllByTourType(TourType tourType);
    List<Voucher> findAllByTransferType(TransferType transferType);
    List<Voucher> findAllByPrice(Double price);
    List<Voucher> findAllByHotelType(HotelType hotelType);

    Page<Voucher> findAll(Pageable pageable);


    @Query("SELECT v FROM Voucher v " +
            "WHERE (:search IS NULL OR " +
            "LOWER(v.title) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(v.description) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "CAST(v.price AS string) LIKE CONCAT('%', :search, '%') OR " +
            "LOWER(v.tourType) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(v.transferType) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(v.hotelType) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(v.status) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "CAST(v.arrivalDate AS string) LIKE CONCAT('%', :search, '%') OR " +
            "CAST(v.evictionDate AS string) LIKE CONCAT('%', :search, '%') OR " +
            "(v.hot = true AND LOWER(:search) = 'так') OR (v.hot = false AND LOWER(:search) = 'ні'))")
    Page<Voucher> searchAll(@Param("search") String search, Pageable pageable);
}
