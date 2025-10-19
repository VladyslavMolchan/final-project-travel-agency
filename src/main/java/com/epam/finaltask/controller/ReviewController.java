package com.epam.finaltask.controller;

import com.epam.finaltask.model.User;
import com.epam.finaltask.service.ReviewService;
import com.epam.finaltask.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/reviews")
public class ReviewController {

    private final ReviewService reviewService;
    private final UserRepository userRepository;

    @PostMapping("/{voucherId}/add")
    public String addReview(@PathVariable UUID voucherId,
                            @RequestParam("content") String content,
                            Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            log.warn(" Unauthenticated user tried to add a review to voucher: {}", voucherId);
            return "redirect:/login";
        }


        String username = authentication.getName();
        log.debug(" Authenticated user: {}", username);


        User user = userRepository.findUserByUsername(username)
                .orElseThrow(() -> {
                    log.error(" User '{}' not found in database", username);
                    return new RuntimeException("User not found");
                });

        log.info(" User '{}' is adding a review to voucher {}: {}", username, voucherId, content);

        reviewService.addReview(voucherId, user.getId(), content);
        log.debug(" Review successfully added");

        return "redirect:/dashboard/" + voucherId;
    }
}
