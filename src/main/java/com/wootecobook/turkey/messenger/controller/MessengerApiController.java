package com.wootecobook.turkey.messenger.controller;

import com.wootecobook.turkey.commons.resolver.LoginUser;
import com.wootecobook.turkey.commons.resolver.UserSession;
import com.wootecobook.turkey.messenger.service.MessengerService;
import com.wootecobook.turkey.messenger.service.dto.MessengerRequest;
import com.wootecobook.turkey.messenger.service.dto.MessengerRoomResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;

@RestController
@RequestMapping("/api/messenger")
public class MessengerApiController {

    private final MessengerService messengerService;

    public MessengerApiController(MessengerService messengerService) {
        this.messengerService = messengerService;
    }

    @PostMapping
    public ResponseEntity<MessengerRoomResponse> create(@RequestBody MessengerRequest messengerRequest, @LoginUser UserSession userSession) {
        final MessengerRoomResponse messengerRoomResponse = messengerService.findMessengerRoom(messengerRequest, userSession.getId());
        final URI uri = linkTo(MessengerController.class, messengerRoomResponse.getId()).toUri();
        return ResponseEntity.created(uri).body(messengerRoomResponse);
    }
}
