package com.wootecobook.turkey.good.service;

import com.wootecobook.turkey.user.domain.User;

public interface GoodService<D> {

    int toggleGood(D d, User user);

    int countBy(D d);

    boolean existsByPostAndUser(D d, User user);
}
