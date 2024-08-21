package com.sparta.studytrek.domain.auth.repository;

import static com.sparta.studytrek.domain.auth.entity.match.QCampUser.campUser;
import static com.sparta.studytrek.domain.camp.entity.QCamp.camp;

import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class CampUserRepositoryImpl implements CampUserRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<String> findCampNamesById(Long userId) {
        return jpaQueryFactory
            .select(camp.name)
            .from(campUser)
            .where(campUser.user.id.eq(userId))
            .fetch();
    }
}
