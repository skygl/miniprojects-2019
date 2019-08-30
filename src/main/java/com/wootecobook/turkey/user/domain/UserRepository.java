package com.wootecobook.turkey.user.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    Page<User> findAllByNameIsContaining(String name, Pageable pageable);

    @Query("select u from User u where u.id in (select f.relatedUserId from Friend f where f.relatingUserId = :userId)")
    List<User> findFriendsByUserId(@Param("userId") Long userId);
}
