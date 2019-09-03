package com.wootecobook.turkey.post.service.dto;

import com.wootecobook.turkey.file.domain.UploadFile;
import com.wootecobook.turkey.post.domain.Contents;
import com.wootecobook.turkey.post.domain.Post;
import com.wootecobook.turkey.user.domain.User;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class PostRequest {

    @NotBlank(message = "글의 내용을 입력하세요")
    private String contents;
    private List<MultipartFile> files;
    private Long receiver;
    private List<Long> taggedUsers;

    @Builder
    private PostRequest(final String contents, final List<MultipartFile> files, final Long receiver, final List<Long> taggedUsers) {
        this.contents = contents;
        this.files = files;
        this.receiver = receiver;
        this.taggedUsers = taggedUsers;
    }

    public Post toEntity(final User author, final User receiver,
                         final List<UploadFile> savedFiles, final List<User> taggedUsers) {
        return Post.builder()
                .author(author)
                .receiver(receiver)
                .contents(new Contents(contents))
                .uploadFiles(savedFiles)
                .taggedUsers(taggedUsers)
                .build();
    }
}
