package com.wootecobook.turkey.user.service;

import com.wootecobook.turkey.file.domain.FileFeature;
import com.wootecobook.turkey.file.service.UploadFileService;
import com.wootecobook.turkey.user.domain.User;
import com.wootecobook.turkey.user.service.dto.MyPageResponse;
import com.wootecobook.turkey.user.service.dto.UserResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.when;

@ExtendWith({MockitoExtension.class})
public class MyPageServiceTest {

    @Mock
    private UserService userService;

    @Mock
    private UploadFileService uploadFileService;

    @InjectMocks
    private MyPageService myPageService;

    @Test
    void 마이페이지_정보_조회_테스트() {
        // given
        User testUser = User.builder()
                .name("pkch")
                .email("pkch@woowa.com")
                .password("passw0rD!")
                .build();

        List<FileFeature> testFileFeature = Arrays.asList(FileFeature.builder().build());
        List<UserResponse> testFriend = Arrays.asList(UserResponse.builder()
                .id(2L)
                .name("olaf")
                .email("olaf@woowa.com")
                .build());

        // when
        long testId = 1L;
        when(userService.findById(testId)).thenReturn(testUser);
        when(userService.findUserResponseOfFriendsById(testId)).thenReturn(testFriend);
        when(uploadFileService.findFileFeaturesByUserId(testId)).thenReturn(testFileFeature);

        // then
        MyPageResponse myPageResponse = myPageService.findUserResponseById(testId);
        assertThat(myPageResponse.getUser().getName()).isEqualTo(testUser.getName());
        assertThat(myPageResponse.getUser().getEmail()).isEqualTo(testUser.getEmail());
        assertThat(myPageResponse.getImages()).isEqualTo(testFileFeature);
        assertThat(myPageResponse.getFriends()).isEqualTo(testFriend);
        assertNull(myPageResponse.getCover());
    }
}
