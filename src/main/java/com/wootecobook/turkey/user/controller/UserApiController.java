package com.wootecobook.turkey.user.controller;

import com.wootecobook.turkey.commons.resolver.LoginUser;
import com.wootecobook.turkey.commons.resolver.UserSession;
import com.wootecobook.turkey.file.domain.FileFeature;
import com.wootecobook.turkey.user.service.*;
import com.wootecobook.turkey.user.service.dto.MyPageResponse;
import com.wootecobook.turkey.user.service.dto.UploadImage;
import com.wootecobook.turkey.user.service.dto.UserRequest;
import com.wootecobook.turkey.user.service.dto.UserResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.net.URI;
import java.util.List;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;

@RestController
@RequestMapping("/api/users")
public class UserApiController {

    private final MyPageService myPageService;
    private final UserService userService;
    private final UserCreateService userCreateService;
    private final UserDeleteService userDeleteService;
    private final UserImageUploadService userImageUploadService;

    public UserApiController(final MyPageService myPageService,
                             final UserService userService,
                             final UserCreateService userCreateService,
                             final UserDeleteService userDeleteService,
                             final UserImageUploadService userImageUploadService) {
        this.myPageService = myPageService;
        this.userService = userService;
        this.userCreateService = userCreateService;
        this.userDeleteService = userDeleteService;
        this.userImageUploadService = userImageUploadService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> show(@PathVariable Long id) {
        return ResponseEntity.ok(userService.findUserResponseById(id));
    }

    @GetMapping("/{id}/mypage")
    public ResponseEntity<MyPageResponse> myPage(@PathVariable Long id) {
        return ResponseEntity.ok(myPageService.findUserResponseById(id));
    }

    @PostMapping
    public ResponseEntity<UserResponse> create(@RequestBody UserRequest userRequest) {
        UserResponse userResponse = userCreateService.create(userRequest);
        URI uri = linkTo(UserApiController.class).slash(userResponse.getId()).toUri();
        return ResponseEntity.created(uri).body(userResponse);
    }

    @GetMapping
    public ResponseEntity<List<UserResponse>> list(@LoginUser UserSession userSession) {
        return ResponseEntity.ok(userService.findAllUsersWithoutCurrentUser(userSession.getId()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity delete(@PathVariable Long id, @LoginUser UserSession userSession, HttpSession httpSession) {
        userDeleteService.delete(id, userSession.getId());
        httpSession.removeAttribute(UserSession.USER_SESSION_KEY);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{name}/search")
    public ResponseEntity<Page<UserResponse>> search(@PathVariable String name,
                                                     @PageableDefault(size = 5, sort = "updatedAt", direction = Sort.Direction.DESC) Pageable pageable) {

        Page<UserResponse> userResponses = userService.findByName(name, pageable);
        return ResponseEntity.ok(userResponses);
    }

    @PutMapping("/{id}/upload")
    public ResponseEntity<FileFeature> uploadUserImage(@PathVariable Long id, UploadImage image,
                                                       @LoginUser UserSession userSession) {
        FileFeature fileFeature = userImageUploadService.uploadImage(image, id, userSession.getId());

        return ResponseEntity.ok(fileFeature);
    }
}