package com.lathief.graphqldemo.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Set;

@Data
@Entity
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Author {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "name_author", nullable = false)
    private String name;
    @OneToMany(mappedBy = "author")
    private Set<Book> books;

    public Author(String name, Set<Book> books) {
        this.name = name;
        this.books = books;
    }
}
