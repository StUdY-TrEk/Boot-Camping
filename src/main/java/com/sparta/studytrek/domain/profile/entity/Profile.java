package com.sparta.studytrek.domain.profile.entity;

import java.time.LocalDate;
import java.util.Set;

import com.sparta.studytrek.domain.auth.entity.User;
import com.sparta.studytrek.domain.profile.dto.ProfileRequestDto;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class Profile {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String bootcampName;
	private String track;
	private String generation;
	private LocalDate startDate;
	private LocalDate endDate;

	@ElementCollection
	@CollectionTable(name = "profile_tech_stack", joinColumns = @JoinColumn(name = "profile_id"))
	@Column(name = "tech_stack")
	private Set<String> techStack;

	private String certificate;

	@ManyToOne
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	public Profile(String bootcampName, String track, String generation, LocalDate startDate, LocalDate endDate,
		String certificate, User user) {
		this.bootcampName = bootcampName;
		this.track = track;
		this.generation = generation;
		this.startDate = startDate;
		this.endDate = endDate;
		this.certificate = certificate;
		this.user = user;
	}

	public void updateProfile(ProfileRequestDto requestDto) {
		this.bootcampName = requestDto.getBootcampName();
		this.track = requestDto.getTrack();
		this.generation = requestDto.getGeneration();
		this.startDate = requestDto.getStartDate();
		this.endDate = requestDto.getEndDate();
		this.certificate = requestDto.getCertificate();
		this.techStack = requestDto.getTechStack();
	}
}
