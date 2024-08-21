package com.sparta.studytrek.domain.like.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sparta.studytrek.domain.like.entity.ReviewLike;

@Repository
public interface ReviewLikeRepository extends JpaRepository<ReviewLike, Long> {

    Optional<ReviewLike> findByReviewIdAndUserId(Long reviewId, Long id);

    int countLikeByReviewId(Long reviewId);

    List<ReviewLike> findAllByUserId(Long userId);

    int countByUserId(Long id);
}
