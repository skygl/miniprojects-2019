package com.wootecobook.turkey.good.service;

import com.wootecobook.turkey.good.domain.post.PostGood;
import com.wootecobook.turkey.good.domain.post.PostGoodRepository;
import com.wootecobook.turkey.post.domain.Post;
import com.wootecobook.turkey.user.domain.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class PostGoodService implements GoodService<Post> {

    private final PostGoodRepository postGoodRepository;

    public PostGoodService(final PostGoodRepository postGoodRepository) {
        this.postGoodRepository = postGoodRepository;
    }

    @Override
    public int toggleGood(Post post, User user) {
        Optional<PostGood> maybeGood = postGoodRepository.findByPostAndUser(post, user);

        if (maybeGood.isPresent()) {
            cancelGoodRequest(maybeGood.get());
        } else {
            approveGoodRequest(new PostGood(user, post));
        }

        return countBy(post);
    }

    private void approveGoodRequest(PostGood postGood) {
        postGoodRepository.save(postGood);
    }

    private void cancelGoodRequest(PostGood postGood) {
        postGoodRepository.delete(postGood);
    }

    @Override
    @Transactional(readOnly = true)
    public int countBy(final Post post) {
        return postGoodRepository.countByPost(post);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByPostAndUser(final Post post, final User user) {
        return postGoodRepository.existsByPostAndUser(post, user);
    }
}
