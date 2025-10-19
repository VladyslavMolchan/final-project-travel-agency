package com.epam.finaltask.service;


import com.epam.finaltask.dto.ReviewDTO;

import java.util.List;
import java.util.UUID;

public interface ReviewService {
    List<ReviewDTO> getReviewsByVoucherId(UUID voucherId);
    void addReview(UUID voucherId, UUID userId, String content);
}
