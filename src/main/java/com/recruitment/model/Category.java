package com.recruitment.model;

import jakarta.persistence.*;
import lombok.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "categories")
@Getter
@Setter
@NoArgsConstructor
public class Category extends BaseEntity {

    @Column(nullable = false, unique = true)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    private String icon;

    private boolean active;

    @OneToMany(mappedBy = "category")
    private List<Job> jobs = new ArrayList<>();
    
    public Category(String name) {
        this.name = name;
        this.active = true;
    }
}
