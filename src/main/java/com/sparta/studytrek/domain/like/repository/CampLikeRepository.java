package com.sparta.studytrek.domain.like.repository;

import com.sparta.studytrek.domain.camp.entity.Camp;
import com.sparta.studytrek.domain.like.entity.CampLike;
import com.sparta.studytrek.domain.auth.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CampLikeRepository extends JpaRepository<CampLike, Long> {

    // 캠프와 사용자로 좋아요 기록을 찾는 메서드
    Optional<CampLike> findByCampAndUser(Camp camp, User user);

    // 캠프와 사용자가 좋아요를 눌렀는지 여부 확인
    boolean existsByCampAndUser(Camp camp, User user);

    List<CampLike> findByUser(User user);

	int countByUser(User user);
}