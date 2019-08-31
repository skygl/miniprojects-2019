package com.wootecobook.turkey.messenger.controller;

import com.wootecobook.turkey.commons.resolver.LoginUser;
import com.wootecobook.turkey.commons.resolver.UserSession;
import com.wootecobook.turkey.messenger.service.MessengerService;
import com.wootecobook.turkey.messenger.service.dto.MessageResponse;
import com.wootecobook.turkey.messenger.service.dto.MessengerRequest;
import com.wootecobook.turkey.messenger.service.dto.MessengerRoomResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;

@RestController
@RequestMapping("/api/messenger")
public class MessengerApiController {

    private final MessengerService messengerService;

    public MessengerApiController(final MessengerService messengerService) {
        this.messengerService = messengerService;
    }

    @PostMapping
    public ResponseEntity<MessengerRoomResponse> create(@RequestBody MessengerRequest messengerRequest, @LoginUser UserSession userSession) {
        final MessengerRoomResponse messengerRoomResponse = messengerService.findMessengerRoom(messengerRequest, userSession.getId());
        final URI uri = linkTo(MessengerController.class, messengerRoomResponse.getId()).toUri();
        return ResponseEntity.created(uri).body(messengerRoomResponse);
    }

    @GetMapping("/{roomId}")
    public ResponseEntity<List<MessageResponse>> list(@PathVariable Long roomId, @LoginUser UserSession userSession) {
        messengerService.checkMember(roomId, userSession.getId());
        return ResponseEntity.ok(messengerService.findMessageResponsesByRoomId(roomId));
    }
}
