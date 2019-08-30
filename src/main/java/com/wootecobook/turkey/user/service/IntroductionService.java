package com.wootecobook.turkey.user.service;

import com.wootecobook.turkey.user.domain.Introduction;
import com.wootecobook.turkey.user.domain.IntroductionRepository;
import com.wootecobook.turkey.user.service.dto.IntroductionRequest;
import com.wootecobook.turkey.user.service.dto.IntroductionResponse;
import com.wootecobook.turkey.user.service.exception.UserMismatchException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;

@Service
@Transactional
public class IntroductionService {

    public static final String NOT_FOUND_INTRODUCTION_MESSAGE = "소개를 찾을 수 없습니다.";
    public static final String MISMATCH_USER_MESSAGE = "본인의 소개만 수정할 수 있습니다.";
    private static final String NOT_FOUND_USER_MESSAGE = "유저를 찾을 수 없습니다.";
    private static final String NULL_USER_MESSAGE = "사용자 ID가 Null입니다.";

    private final IntroductionRepository introductionRepository;
    private final UserService userService;

    public IntroductionService(final IntroductionRepository introductionRepository, final UserService userService) {
        this.introductionRepository = introductionRepository;
        this.userService = userService;
    }

    public Introduction save(Long userId) {
        checkUserExistence(userId);
        return introductionRepository.save(Introduction.builder().userId(userId).build());
    }

    @Transactional(readOnly = true)
    public Introduction findByUserId(Long userId) {
        return introductionRepository.findByUserId(userId)
                .orElseThrow(() -> new EntityNotFoundException(NOT_FOUND_INTRODUCTION_MESSAGE));
    }

    public IntroductionResponse findIntroductionResponseByUserId(Long userId) {
        return IntroductionResponse.from(findByUserId(userId));
    }

    public IntroductionResponse update(IntroductionRequest introductionRequest, Long sessionUserId) {
        checkSessionUserId(introductionRequest.getUserId(), sessionUserId);
        checkUserExistence(sessionUserId);
        Introduction introduction = findByUserId(sessionUserId);
        introduction.update(introductionRequest.toEntity());
        return IntroductionResponse.from(introduction);
    }

    public void delete(Long userId) {
        Introduction introduction = findByUserId(userId);
        introductionRepository.delete(introduction);
    }

    private void checkSessionUserId(Long introductionUserId, Long sessionUserId) {
        if (introductionUserId == null || sessionUserId == null) {
            throw new UserMismatchException(NULL_USER_MESSAGE);
        }
        if (!introductionUserId.equals(sessionUserId)) {
            throw new UserMismatchException(MISMATCH_USER_MESSAGE);
        }
    }

    private void checkUserExistence(Long userId) {
        if (!userService.existsById(userId)) {
            throw new EntityNotFoundException(NOT_FOUND_USER_MESSAGE);
        }
    }
}