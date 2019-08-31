package com.wootecobook.turkey.search.controller;

import com.wootecobook.turkey.search.service.UserElasticSearchService;
import com.wootecobook.turkey.search.service.dto.UserSearchResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/search")
public class SearchApiController {

    private final UserElasticSearchService userElasticSearchService;

    public SearchApiController(final UserElasticSearchService userElasticSearchService) {
        this.userElasticSearchService = userElasticSearchService;
    }

    @GetMapping("/{name}")
    public ResponseEntity<List<UserSearchResponse>> search(@PathVariable String name) {
        final List<UserSearchResponse> userSearchResponses = userElasticSearchService.findByUserName(name);
        return ResponseEntity.ok(userSearchResponses);
    }
}
