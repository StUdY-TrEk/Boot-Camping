package com.sparta.studytrek.domain.auth.repository;

import java.util.List;
import org.springframework.stereotype.Repository;

@Repository
public interface CampUserRepositoryCustom {
    List<String> findCampNamesById(Long userId);
}
