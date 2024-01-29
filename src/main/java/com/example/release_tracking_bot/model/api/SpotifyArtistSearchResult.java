package com.example.release_tracking_bot.model.api;


import com.example.release_tracking_bot.model.Artist;

public class SpotifyArtistSearchResult {
    private Artist[] items;

    public Artist[] getItems() {
        return items;
    }

    public void setItems(Artist[] items) {
        this.items = items;
    }
}
