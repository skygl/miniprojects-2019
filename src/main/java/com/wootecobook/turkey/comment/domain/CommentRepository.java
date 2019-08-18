package com.wootecobook.turkey.comment.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    Page<Comment> findAllByParentId(@Param("parentId") Long parentId, Pageable pageable);

    Page<Comment> findAllByPostIdAndParentIdIsNull(@Param("postId") Long postId, Pageable pageable);
}
