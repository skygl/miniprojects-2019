package com.wootecobook.turkey.user.service;

import com.wootecobook.turkey.search.service.UserElasticSearchService;
import com.wootecobook.turkey.user.domain.User;
import com.wootecobook.turkey.user.service.dto.UserRequest;
import com.wootecobook.turkey.user.service.dto.UserResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UserCreateService {

    private final UserService userService;
    private final IntroductionService introductionService;
    private final UserElasticSearchService userElasticSearchService;

    public UserCreateService(final UserService userService, final IntroductionService introductionService, final UserElasticSearchService userElasticSearchService) {
        this.userService = userService;
        this.introductionService = introductionService;
        this.userElasticSearchService = userElasticSearchService;
    }

    public UserResponse create(UserRequest userRequest) {
        User user = userService.save(userRequest);
        introductionService.save(user.getId());
        userElasticSearchService.save(user);
        return UserResponse.from(user);
    }
}
