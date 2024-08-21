package com.sparta.studytrek.domain.rank.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class RankResponseDto {

    private Long id;
    private Long campId;
    private String campName;
    private String description;
    private Integer ranking;
    private String campImage;
    private Integer likesCount;

    public RankResponseDto(Long id, Long campId, String campName, String description,
        Integer ranking, String campImage, Integer likesCount) {
        this.id = id;
        this.campId = campId;
        this.campName = campName;
        this.description = description;
        this.ranking = ranking;
        this.campImage = campImage;
        this.likesCount = likesCount;
    }
}
