package com.epam.finaltask.repository;


import com.epam.finaltask.model.Review;
import com.epam.finaltask.model.Voucher;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ReviewRepository extends JpaRepository<Review, UUID> {
    List<Review> findAllByVoucherOrderByCreatedAtDesc(Voucher voucher);
}
