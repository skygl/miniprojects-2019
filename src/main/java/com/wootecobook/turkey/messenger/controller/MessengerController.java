package com.wootecobook.turkey.messenger.controller;

import com.wootecobook.turkey.commons.resolver.LoginUser;
import com.wootecobook.turkey.commons.resolver.UserSession;
import com.wootecobook.turkey.messenger.service.MessengerService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/messenger")
public class MessengerController {

    private final MessengerService messengerService;

    public MessengerController(final MessengerService messengerService) {
        this.messengerService = messengerService;
    }

    @GetMapping("/{roomId}")
    public ModelAndView form(@PathVariable Long roomId, @LoginUser UserSession userSession) {
        messengerService.checkMember(roomId, userSession.getId());
        return new ModelAndView("messenger", "roomId", roomId);
    }
}

