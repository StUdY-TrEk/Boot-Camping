package com.sparta.studytrek.domain.question.service;

import java.util.List;
import java.util.stream.Collectors;

import com.sparta.studytrek.domain.auth.entity.User;
import com.sparta.studytrek.domain.question.dto.QuestionRequestDto;
import com.sparta.studytrek.domain.question.dto.QuestionResponseDto;
import com.sparta.studytrek.domain.question.entity.Question;
import com.sparta.studytrek.domain.question.repository.QuestionRepository;
//import com.sparta.studytrek.domain.question.repository.QuestionRepositoryCustom;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class QuestionService {

    private final QuestionRepository questionRepository;

    /**
     * 질문 작성
     *
     * @param requestDto 질문 등록 요청 데이터
     * @param user       요청한 유저의 정보
     * @return 질문 응답 데이터
     */
    public QuestionResponseDto createQuestion(QuestionRequestDto requestDto, User user) {
        Question question = new Question(requestDto, user);
        Question createQuestion = questionRepository.save(question);
        return new QuestionResponseDto(createQuestion);
    }

    /**
     * 질문 수정
     *
     * @param id            질문 ID
     * @param requestDto    질문 수정 요청 데이터
     * @param user          요청한 유저의 정보
     * @return  질문 응답 데이터
     */
    @Transactional
    public QuestionResponseDto updateQuestion(Long id, QuestionRequestDto requestDto, User user) {
        Question question = questionRepository.findByQuestionId(id);
        question.update(requestDto);
        if (!question.getUser().getId().equals(user.getId())) {
            throw new CustomException(ErrorCode.QUESTION_UPDATE_NOT_AUTHORIZED);
        }
        return new QuestionResponseDto(question);
    }

    /**
     * 질문 삭제
     *
     * @param id   질문 ID
     * @param user 요청한 유저의 정보
     */
    public void deleteQuestion(Long id, User user) {
        Question question = questionRepository.findByQuestionId(id);
        if (!question.getUser().getId().equals(user.getId())) {
            throw new CustomException(ErrorCode.QUESTION_DELETE_NOT_AUTHORIZED);
        }
        questionRepository.delete(question);
    }

    /**
     * 질문 전체 조회
     *
     * @param pageable 페이지 정보
     * @return 질문 전체 목록
     */
    public Page<QuestionResponseDto> getQuestions(Pageable pageable) {
        Page<Question> questionPage = questionRepository.findByAllByOrderByCreatedAtDesc(pageable);
        return questionPage.map(QuestionResponseDto::new);
    }

    /**
     * 리뷰 단건 조회
     *
     * @param id 질문 ID
     * @return 해당 질문의 응답 데이터
     */
    public QuestionResponseDto getQuestion(Long id) {
        Question question = questionRepository.findByQuestionId(id);
        return new QuestionResponseDto(question);
    }

    public int countUserQuestions(User user) {
        return questionRepository.countByUser(user);
    }

    public List<String> listUserQuestions(User user) {
        List<Question> questions = questionRepository.findAllByUserOrderByCreatedAtDesc(user);
        return questions.stream()
            .map(Question::getTitle)
            .collect(Collectors.toList());
    }
}
