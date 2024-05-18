package com.main.models;

import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "service_units", uniqueConstraints = { @UniqueConstraint(columnNames = { "name" }) })
public class ServiceUnit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false)
    private int granularityInMinutes;

    @OneToMany(mappedBy = "serviceUnit", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<AvailabilitySchedule> availabilitySchedules;

    @OneToMany(mappedBy = "serviceUnit", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<TypeResource> typeResources;

    @OneToMany(mappedBy = "serviceUnit", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<Employee> employees;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;
}
