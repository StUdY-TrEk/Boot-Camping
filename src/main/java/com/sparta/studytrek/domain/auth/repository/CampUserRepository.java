package com.sparta.studytrek.domain.auth.repository;

import com.sparta.studytrek.domain.auth.entity.match.CampUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CampUserRepository extends JpaRepository<CampUser, Long>, CampUserRepositoryCustom {

}
