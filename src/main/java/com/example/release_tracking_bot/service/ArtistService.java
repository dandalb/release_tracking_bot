package com.example.release_tracking_bot.service;

import com.example.release_tracking_bot.config.TokenConfig;
import com.example.release_tracking_bot.dto.response.SpotifySearchResponse;
import com.example.release_tracking_bot.model.Artist;
import com.example.release_tracking_bot.repository.ArtistRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ArtistService {
    private final TokenConfig tokenConfig;
    private final ArtistRepository artistRepository;
    private final ReleaseService releaseService;

    public Artist requestArtistByName(String artistName) {
        WebClient webClient = tokenConfig.getBaseWebClientWithCurrentToken();
        String apiEndpoint = "/search?q=" + artistName + "&type=artist&limit=20";

        Artist[] artists = webClient.get()
                .uri(apiEndpoint)
                .retrieve()
                .bodyToMono(SpotifySearchResponse.class)
                .block()
                .getArtists().getItems();
        for (Artist artist : artists) {
            if (artist.getName().equals(artistName.trim())) {
                return artist;
            }
        }
        return artists[0];
    }

    public Artist requestArtistById(String artistId) {
        WebClient webClient = tokenConfig.getBaseWebClientWithCurrentToken();
        String apiEndpoint = "/artists/" + artistId;
        Artist[] artists = webClient.get()
                .uri(apiEndpoint)
                .retrieve()
                .bodyToMono(SpotifySearchResponse.class)
                .block()
                .getArtists().getItems();
        if (artists.length != 0) {
            return artists[0];
        }
        throw new RuntimeException("Cant find artist by ID: " + artistId);
    }

    public List<Artist> saveAllArtists(List<Artist> artists) {
        return artistRepository.saveAll(artists.stream()
                .distinct()
                .peek(artist -> artist.setLastRelease(releaseService.getLatestReleaseByArtistId(artist.getId())))
                .collect(Collectors.toList()));
    }

    public Artist findByArtistId(String id) {
        return artistRepository.findById(id);
    }

    public List<Artist> findAll() {
        return artistRepository.findAll();
    }
}
