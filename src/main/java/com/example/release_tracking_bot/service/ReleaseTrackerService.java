package com.example.release_tracking_bot.service;

import com.example.release_tracking_bot.model.Artist;
import com.example.release_tracking_bot.model.Release;
import com.example.release_tracking_bot.model.User;
import com.example.release_tracking_bot.repository.ArtistRepository;
import com.example.release_tracking_bot.repository.ReleaseRepository;
import com.example.release_tracking_bot.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@AllArgsConstructor
public class ReleaseTrackerService {
    private final ReleaseService releaseService;
    private final UserRepository userRepository;
    private final ReleaseRepository releaseRepository;
    private final ArtistRepository artistRepository;

    // try to use multithreading
    public Map<String, List<Artist>> checkLastUpdates() {
        Map<String, List<Artist>> resultUpdates = new HashMap<>();

        for (User user : userRepository.findAll()) {
            List<Artist> updatedArtists = new ArrayList<>();

            for (Artist artist : user.getArtists()) {
                Release lastRelease = artist.getLastRelease();
                Release actualRelease = releaseService.getLatestReleaseByArtistId(artist.getId());

                if (lastRelease.getReleaseDate() != null && actualRelease.getReleaseDate() != null &&
                        (actualRelease.getReleaseDate().compareTo(lastRelease.getReleaseDate()) > 0)) {
                    artist.setLastRelease(updateRelease(lastRelease, actualRelease));
                    updatedArtists.add(artist);
                }
            }

            if (!updatedArtists.isEmpty()) {
                resultUpdates.put(user.getChatId(), updatedArtists);
                artistRepository.saveAll(updatedArtists);
            }
        }
        return resultUpdates;
    }

    private Release updateRelease(Release lastRelease, Release actualRelease) {
        lastRelease.setReleaseDate(actualRelease.getReleaseDate());
        lastRelease.setLink(actualRelease.getLink());
        lastRelease.setType(actualRelease.getType());
        lastRelease.setName(actualRelease.getName());
        lastRelease.setId(actualRelease.getId());
        return lastRelease;
    }
}
