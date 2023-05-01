package com.lathief.graphqldemo.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;
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
    private List<Book> books;

    public Publisher(String name, String address) {
        this.name = name;
        this.address = address;
    }

    public Publisher(String name, List<Book> books) {
        this.name = name;
        this.books = books;
    }
}
