package com.main.models;

import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

@Entity
@Table(name = "features")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Feature {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DataType type;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "type_resource_id")
    private TypeResource typeResource;

    @OneToMany(mappedBy = "feature")
    private Set<ResourceFeatures> resourceFeatures;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Feature feature = (Feature) o;

        return id != null ? id.equals(feature.id) : feature.id == null;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
