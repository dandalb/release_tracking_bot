package com.example.release_tracking_bot.service;

import com.example.release_tracking_bot.config.TokenConfig;
import com.example.release_tracking_bot.model.Release;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.*;

@Service
@AllArgsConstructor
@Log4j2
public class ReleaseService {
    private final TokenConfig tokenConfig;

    public Release getLatestReleaseByArtistId(String artistId) {
        String apiEndpoint = "/artists/" + artistId + "/albums" +
                "?include_groups=album,single,compilation" +
                "&limit=50";
        WebClient webClient = tokenConfig.getBaseWebClientWithCurrentToken();

        Map response = webClient.get()
                .uri(apiEndpoint)
                .retrieve()
                .bodyToMono(Map.class)
                .block();
        Release artistRelease = new Release();

        if (response != null && !response.isEmpty()) {
            Optional<LinkedHashMap> optional = ((List) response.get("items")).stream().sorted(new SpotifyAlbumComparator()).findFirst();

            if (optional.isEmpty()) {
                return artistRelease;
            }
            LinkedHashMap release = optional.get();
            artistRelease.setId((String) release.get("id"));
            artistRelease.setName((String) release.get("name"));
            artistRelease.setReleaseDate((String) release.get("release_date"));
            artistRelease.setLink((String) ((LinkedHashMap) release.get("external_urls")).get("spotify"));
            artistRelease.setType((String) release.get("album_type"));
        }
        return artistRelease;
    }

    static class SpotifyAlbumComparator implements Comparator<LinkedHashMap> {
        @Override
        public int compare(LinkedHashMap album1, LinkedHashMap album2) {
            String releaseDate1 = (String) album1.get("release_date");
            String releaseDate2 = (String) album2.get("release_date");

            return releaseDate2.compareTo(releaseDate1);
        }
    }

    public List<?> getLatestReleases() {
        String apiEndpoint = "/artists/" + artistId + "/albums" +
                "?include_groups=album,single,compilation" +
                "&limit=50";
        WebClient webClient = tokenConfig.getBaseWebClientWithCurrentToken();

        Map response = webClient.get()
                .uri(apiEndpoint)
                .retrieve()
                .bodyToMono(Map.class)
                .block();

    }
}