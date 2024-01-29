package com.example.release_tracking_bot.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Entity
@Table(name = "releases")
@NoArgsConstructor
@AllArgsConstructor
public class Release {
    @Id
    @JsonIgnore
    @GeneratedValue
    private UUID entityId;
    private String id;
    private String name;
    private String releaseDate;
    private String link;
    private String type;
}
