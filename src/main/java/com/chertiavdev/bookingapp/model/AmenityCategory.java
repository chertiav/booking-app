package com.chertiavdev.bookingapp.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Getter
@Setter
@SQLDelete(sql = "UPDATE amenity_categories SET is_deleted = true WHERE id = ?")
@SQLRestriction("is_deleted = false")
@ToString
@NoArgsConstructor
@EqualsAndHashCode
@Entity
@Table(name = "amenity_categories")
public class AmenityCategory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false)
    private boolean isDeleted = false;

    public AmenityCategory(Long id) {
        this.id = id;
    }
}
