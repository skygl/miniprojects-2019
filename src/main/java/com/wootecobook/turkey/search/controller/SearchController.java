package com.wootecobook.turkey.search.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/search")
public class SearchController {

    @GetMapping
    public ModelAndView form(@RequestParam String keyword) {
        return new ModelAndView("search-result", "keyword", keyword);
    }
}
