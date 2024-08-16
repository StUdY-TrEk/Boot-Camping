package com.sparta.studytrek.websocket.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sparta.studytrek.websocket.entity.Notification;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
	Page<Notification> findByUsername(String username, Pageable pageable);
	void deleteByUsername(String username);
	long countByUsernameAndIsReadFalse(String username);
}
