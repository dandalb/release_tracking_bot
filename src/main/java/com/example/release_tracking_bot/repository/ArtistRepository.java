package com.example.release_tracking_bot.repository;

import com.example.release_tracking_bot.model.Artist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ArtistRepository extends JpaRepository <Artist, UUID> {
    Artist findById(String id);
}
