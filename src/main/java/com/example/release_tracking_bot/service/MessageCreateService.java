package com.example.release_tracking_bot.service;

import com.example.release_tracking_bot.model.Artist;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MessageCreateService {
    public String startCommandReceived(String name) {
        return "Hi + " + name + " you follow to release_tracking_bot you can use next commands:" +
                System.lineSeparator() + System.lineSeparator() +
                "/add artist1, artist2 ... — add artists to track new releases" +
                System.lineSeparator() +
                "/all — get all your artists" +
                System.lineSeparator() +
                "/delete artistName — delete your artist";
    }


    public List<String> allArtistMessage(List<Artist> artists) {
        return artists.stream()
                .map(this::mapAllArtistMessages)
                .collect(Collectors.toList());
    }

    public String parseUpdateMessages(List<String> messages) {
        return StringUtils.join(messages, System.lineSeparator());
    }

    public List<String> releaseUpdatedMessage(List<Artist> artists) {
        return artists.stream()
                .map(this::mapReleaseMessage)
                .collect(Collectors.toList());
    }

    private String mapReleaseMessage(Artist artist) {
        return artist.getName() + " — " + artist.getLastRelease().getName() +
                System.lineSeparator() +
                "Link: " + artist.getLastRelease().getLink() +
                System.lineSeparator();
    }

    private String mapAllArtistMessages(Artist artist) {
        return artist.getName() + " — " + artist.getLastRelease().getName();
    }
}
