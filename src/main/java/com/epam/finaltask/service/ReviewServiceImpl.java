package com.epam.finaltask.service;



import com.epam.finaltask.dto.ReviewDTO;
import com.epam.finaltask.model.Review;
import com.epam.finaltask.model.User;
import com.epam.finaltask.model.Voucher;
import com.epam.finaltask.repository.ReviewRepository;
import com.epam.finaltask.repository.UserRepository;
import com.epam.finaltask.repository.VoucherRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final VoucherRepository voucherRepository;
    private final UserRepository userRepository;

    @Override
    public List<ReviewDTO> getReviewsByVoucherId(UUID voucherId) {
        log.info("Fetching reviews for voucher ID: {}", voucherId);

        Voucher voucher = voucherRepository.findById(voucherId)
                .orElseThrow(() -> {
                    log.warn("Voucher not found with ID: {}", voucherId);
                    return new RuntimeException("Voucher not found");
                });

        List<ReviewDTO> reviews = reviewRepository.findAllByVoucherOrderByCreatedAtDesc(voucher)
                .stream()
                .map(r -> ReviewDTO.builder()
                        .id(r.getId())
                        .userName(r.getUserName())
                        .content(r.getContent())
                        .createdAt(r.getCreatedAt())
                        .build())
                .collect(Collectors.toList());

        log.info("Found {} reviews for voucher ID: {}", reviews.size(), voucherId);
        return reviews;
    }

    @Override
    public void addReview(UUID voucherId, UUID userId, String content) {
        log.info("Adding review for voucher ID: {} by user ID: {}", voucherId, userId);

        Voucher voucher = voucherRepository.findById(voucherId)
                .orElseThrow(() -> {
                    log.warn("Cannot add review: voucher not found with ID: {}", voucherId);
                    return new RuntimeException("Voucher not found");
                });

        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.warn("Cannot add review: user not found with ID: {}", userId);
                    return new RuntimeException("User not found");
                });

        Review review = Review.builder()
                .voucher(voucher)
                .user(user)
                .userName(user.getUsername())
                .userEmail(user.getEmail())
                .content(content)
                .createdAt(LocalDateTime.now())
                .build();

        reviewRepository.save(review);
        log.info("Review successfully added by user '{}' for voucher ID: {}", user.getUsername(), voucherId);
    }
}
