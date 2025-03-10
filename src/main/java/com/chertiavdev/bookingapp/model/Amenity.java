package com.chertiavdev.bookingapp.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "amenities")
public class Amenity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    @Enumerated(EnumType.STRING)
    private AmenityName name;

    public enum AmenityName {
        WIFI,
        WASHING_MACHINE,
        BABY_CHANGING,
        BATHROOM,
        KITCHEN,
        PARTY_ROOM,
        SMOKE_DETECTOR,
        SWIMMING_POOL
    }
}
