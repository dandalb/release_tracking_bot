package com.example.release_tracking_bot.dto.response;

import com.example.release_tracking_bot.model.api.SpotifyArtistSearchResult;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
public class SpotifySearchResponse {
    private SpotifyArtistSearchResult artists;
}