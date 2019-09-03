package com.wootecobook.turkey.good.service;

import com.wootecobook.turkey.comment.domain.Comment;
import com.wootecobook.turkey.good.domain.comment.CommentGood;
import com.wootecobook.turkey.good.domain.comment.CommentGoodRepository;
import com.wootecobook.turkey.user.domain.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class CommentGoodService implements GoodService<Comment> {

    private final CommentGoodRepository commentGoodRepository;

    public CommentGoodService(final CommentGoodRepository commentGoodRepository) {
        this.commentGoodRepository = commentGoodRepository;
    }

    @Override
    public int toggleGood(final Comment comment, final User user) {
        Optional<CommentGood> maybeCommentGood = commentGoodRepository.findByCommentAndUser(comment, user);

        if (maybeCommentGood.isPresent()) {
            cancelGoodRequest(maybeCommentGood.get());
        } else {
            approveGoodRequest(new CommentGood(user, comment));
        }

        return countBy(comment);
    }

    private void approveGoodRequest(CommentGood commentGood) {
        commentGoodRepository.save(commentGood);
    }

    private void cancelGoodRequest(CommentGood commentGood) {
        commentGoodRepository.delete(commentGood);
    }

    @Override
    @Transactional(readOnly = true)
    public int countBy(Comment comment) {
        return commentGoodRepository.countByComment(comment);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByPostAndUser(final Comment comment, final User user) {
        return commentGoodRepository.existsByCommentAndUser(comment, user);
    }
}
