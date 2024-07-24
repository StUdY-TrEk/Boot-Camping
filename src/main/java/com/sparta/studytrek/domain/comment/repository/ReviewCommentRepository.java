package com.sparta.studytrek.domain.comment.repository;

import com.sparta.studytrek.domain.comment.entity.ReviewComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReviewCommentRepository extends JpaRepository<ReviewComment, Long> {
}
