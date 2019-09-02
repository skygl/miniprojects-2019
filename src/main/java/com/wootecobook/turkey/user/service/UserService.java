package com.wootecobook.turkey.user.service;

import com.wootecobook.turkey.user.domain.User;
import com.wootecobook.turkey.user.domain.UserRepository;
import com.wootecobook.turkey.user.service.dto.UserRequest;
import com.wootecobook.turkey.user.service.dto.UserResponse;
import com.wootecobook.turkey.user.service.exception.SignUpException;
import com.wootecobook.turkey.user.service.exception.UserDeleteException;
import com.wootecobook.turkey.user.service.exception.UserMismatchException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserService {

    public static final String NOT_FOUND_MESSAGE = "유저를 찾을수 없습니다.";
    public static final String EMAIL_DUPLICATE_MESSAGE = "중복된 이메일입니다.";

    private final UserRepository userRepository;

    public UserService(final UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public User findById(final Long id) {
        return userRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(NOT_FOUND_MESSAGE));
    }

    @Transactional(readOnly = true)
    public User findByEmail(final String email) {
        return userRepository.findByEmail(email).orElseThrow(() -> new EntityNotFoundException(NOT_FOUND_MESSAGE));
    }

    @Transactional(readOnly = true)
    public UserResponse findUserResponseById(final Long id) {
        return UserResponse.from(findById(id));
    }

    public User save(final UserRequest userRequest) {
        try {
            checkEmailDuplicate(userRequest.getEmail());
            return userRepository.save(userRequest.toEntity());
        } catch (Exception e) {
            throw new SignUpException(e.getMessage());
        }
    }

    private void checkEmailDuplicate(String email) {
        if (userRepository.findByEmail(email).isPresent()) {
            throw new IllegalArgumentException(EMAIL_DUPLICATE_MESSAGE);
        }
    }

    public void delete(final Long userId, final Long sessionUserId) {
        matchId(userId, sessionUserId);
        try {
            userRepository.deleteById(userId);
        } catch (Exception e) {
            throw new UserDeleteException();
        }
    }

    private void matchId(final Long userId, final Long sessionUserId) {
        if (userId == null || !userId.equals(sessionUserId)) {
            throw new UserMismatchException();
        }
    }

    public Page<UserResponse> findByName(final String name, final Pageable pageable) {
        return userRepository.findAllByNameIsContaining(name, pageable)
                .map(UserResponse::from);
    }

    public List<UserResponse> findAllUsersWithoutCurrentUser(final Long id) {
        return userRepository.findAll().stream()
                .filter(user -> !user.matchId(id))
                .map(UserResponse::from)
                .collect(Collectors.toList());
    }

    public List<UserResponse> findUserResponseOfFriendsById(final Long id) {
        return userRepository.findFriendsByUserId(id).stream()
                .map(UserResponse::from)
                .collect(Collectors.toList());
    }

    public boolean existsById(final Long id) {
        return userRepository.existsById(id);
    }
}
