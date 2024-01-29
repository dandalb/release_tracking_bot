package com.example.release_tracking_bot.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.Hibernate;

import java.util.Objects;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "artists")
public class Artist {
    @Id
    @JsonIgnore
    @GeneratedValue
    private UUID entityId;
    private String id;
    private String name;
    @OneToOne(cascade = CascadeType.ALL)
    private Release lastRelease;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Artist artist = (Artist) o;
        return id != null && Objects.equals(id, artist.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
