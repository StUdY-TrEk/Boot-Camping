package com.sparta.studytrek.domain.rank.service;

import com.sparta.studytrek.common.exception.CustomException;
import com.sparta.studytrek.common.exception.ErrorCode;
import com.sparta.studytrek.domain.camp.entity.Camp;
import com.sparta.studytrek.domain.camp.repository.CampRepository;
import com.sparta.studytrek.domain.rank.dto.RankListResponseDto;
import com.sparta.studytrek.domain.rank.dto.RankRequestDto;
import com.sparta.studytrek.domain.rank.dto.RankResponseDto;
import com.sparta.studytrek.domain.rank.entity.Rank;
import com.sparta.studytrek.domain.rank.repository.RankRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class RankService {

    private final RankRepository rankRepository;
    private final CampRepository campRepository;

    /**
     * 새로운 순위를 생성하는 메서드
     *
     * @param requestDto 순위를 생성하기 위한 데이터가 담긴 DTO 객체 (campId: 캠프 ID, ranking: 순위 값)
     * @return 생성된 순위의 정보를 담고 있는 RankResponseDto 객체
     */
    @Transactional
    public RankResponseDto createRank(RankRequestDto requestDto) {
        Camp camp = campRepository.findById(requestDto.campId())
            .orElseThrow(() -> new CustomException(ErrorCode.NOTFOUND_CAMP));

        Integer ranking = getNextAvailableRanking();
        Rank rank = new Rank(camp, ranking);
        Rank savedRank = rankRepository.save(rank);
        return new RankResponseDto(
            savedRank.getId(),
            savedRank.getCamp().getId(),
            savedRank.getCamp().getName(),
            savedRank.getRanking(),
            savedRank.getCamp().getImageUrl(),
            savedRank.getCamp().getLikesCount()
        );
    }

    /**
     * 다음 사용 가능한 순위를 결정하는 메서드
     *
     * @return 다음 순위 값
     */
    private Integer getNextAvailableRanking() {
        // 현재 최대 순위를 조회하여 다음 순위를 결정
        Integer maxRanking = rankRepository.findMaxRanking().orElse(0);
        return maxRanking + 1;
    }

    /**
     * 모든 순위를 페이지 단위로 조회하는 메서드
     *
     * @param pageable 페이지 처리와 정렬을 위한 Pageable 객체 (page: 페이지 번호, size: 페이지 크기, sort: 정렬 기준)
     * @return 순위 목록을 담고 있는 RankListResponseDto 객체
     */
    @Transactional(readOnly = true)
    public RankListResponseDto getAllRanks(Pageable pageable) {
        // 로그 추가 - 메서드 시작
        log.info("getAllRanks 메서드 호출 - Pageable: {}", pageable);

        Page<Rank> rankPage;
        try {
            rankPage = rankRepository.findAllOrderByRankingAsc(pageable);
            log.info("랭크 데이터 페이징 조회 성공 - 총 페이지: {}, 총 요소: {}", rankPage.getTotalPages(), rankPage.getTotalElements());
        } catch (Exception e) {
            log.error("랭크 데이터 페이징 조회 실패", e);
            throw e;  // 원래 예외를 다시 던져서 처리
        }

        List<RankResponseDto> rankList = rankPage.getContent().stream()
            .map(rank -> {
                log.info("Rank 데이터 매핑 - Rank ID: {}, Camp ID: {}", rank.getId(), rank.getCamp().getId());
                return new RankResponseDto(
                    rank.getId(),
                    rank.getCamp().getId(),
                    rank.getCamp().getName(),
                    rank.getRanking(),
                    rank.getCamp().getImageUrl(),
                    rank.getCamp().getLikesCount()
                );
            })
            .toList();

        // 로그 추가 - 메서드 종료
        log.info("getAllRanks 메서드 종료 - 조회된 Rank 수: {}", rankList.size());
        return new RankListResponseDto(rankList, rankPage.getTotalPages(), rankPage.getTotalElements());
    }
}