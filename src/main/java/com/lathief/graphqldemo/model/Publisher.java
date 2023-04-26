package com.lathief.graphqldemo.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Set;

@Data
@Entity
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Publisher {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "name_publisher", nullable = false)
    private String name;
    private String address;
    @OneToMany(mappedBy = "publisher")
    private Set<Book> books;

    public Publisher(String name, Set<Book> books) {
        this.name = name;
        this.books = books;
    }
}
