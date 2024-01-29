package com.example.release_tracking_bot.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue
    @JsonIgnore
    private UUID id;
    private String chatId;
    private String name;
    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    private List<Artist> artists;
}
