package com.main.models;

import jakarta.persistence.*;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "students", uniqueConstraints = { @UniqueConstraint(columnNames = { "username" }) })
public class Student {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private DocumentType documentType;

    @Column(nullable = false, unique = true)
    private long document;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String firstName;

    @Column
    private String middleName;

    @Column(nullable = false)
    private String firstLastName;

    @Column
    private String middleLastName;

    @Column
    private String codeStudent;
    
    @Column
    private String faculty;
    
    @Column
    private String degreeProgram;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;
}
