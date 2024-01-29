package com.example.release_tracking_bot.controller;

import com.example.release_tracking_bot.config.TokenConfig;
import com.example.release_tracking_bot.model.Artist;
import com.example.release_tracking_bot.model.Release;
import com.example.release_tracking_bot.repository.ArtistRepository;
import com.example.release_tracking_bot.repository.ReleaseRepository;
import com.example.release_tracking_bot.service.ReleaseService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@AllArgsConstructor
public class RequestController {
    private final ReleaseService releaseService;
    private final ReleaseRepository releaseRepository;
    private final ArtistRepository artistRepository;

    @GetMapping("/update")
    public List<Artist> getArtistInfo() {
        List<Release> allReleases = releaseRepository.findAll();
        releaseRepository.saveAll(allReleases.stream()
                .peek(r -> r.setReleaseDate("12.05.2022"))
                .collect(Collectors.toList()));
        return artistRepository.findAll();
    }

    public List<?> getLatestReleases() {

    }
}