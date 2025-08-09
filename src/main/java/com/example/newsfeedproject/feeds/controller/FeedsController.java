package com.example.newsfeedproject.feeds.controller;

import com.example.newsfeedproject.feeds.dto.FeedsDto;
import com.example.newsfeedproject.feeds.service.FeedsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/api/feeds")
@RequiredArgsConstructor
@Slf4j
public class FeedsController {
    private final FeedsService feedsService;

    @GetMapping("/{feedId}")
    public String getFeedList(Model model) {
        List<FeedsDto> feeds = feedsService.findAll ();
        model.addAttribute("feeds", feeds);
        return "feeds";
    }
}
