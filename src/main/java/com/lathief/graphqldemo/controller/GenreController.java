package com.lathief.graphqldemo.controller;

import com.lathief.graphqldemo.model.*;
import com.lathief.graphqldemo.repository.AuthorRepository;
import com.lathief.graphqldemo.repository.BookRepository;
import com.lathief.graphqldemo.repository.GenreRepository;
import com.lathief.graphqldemo.repository.PublisherRepository;
import graphql.schema.DataFetchingEnvironment;
import graphql.schema.DataFetchingFieldSelectionSet;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import javax.persistence.criteria.Fetch;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import java.util.*;

@Controller
public class GenreController {
    BookRepository bookRepository;
    PublisherRepository publisherRepository;
    AuthorRepository authorRepository;
    GenreRepository genreRepository;

    GenreController(BookRepository bookRepository, PublisherRepository publisherRepository, AuthorRepository authorRepository, GenreRepository genreRepository) {
        this.bookRepository = bookRepository;
        this.publisherRepository = publisherRepository;
        this.authorRepository = authorRepository;
        this.genreRepository = genreRepository;
    }
//    newGenre
//            addGenreToBook
//    genres
//            genre
    @QueryMapping
    public Iterable<Genre> genres(DataFetchingEnvironment environment) {
        DataFetchingFieldSelectionSet s = environment.getSelectionSet();
        List<Specification<Genre>> specifications = new ArrayList<>();
        if (s.contains("books"))
            return genreRepository.findAll(fetchBook());
        else
            return genreRepository.findAll();
    }
    @QueryMapping
    public Genre genre(@Argument Long id, DataFetchingEnvironment environment) {
        System.out.println(id);
        Specification<Genre> spec = byId(id);
        DataFetchingFieldSelectionSet selectionSet = environment
                .getSelectionSet();
        if (selectionSet.contains("books"))
            spec = spec.and(fetchBook());
        return genreRepository.findOne(spec).orElseThrow(NoSuchElementException::new);
    }

    @MutationMapping
    public Genre newGenre(@Argument GenreInput genre) {
        Genre genreSave = new Genre();
        genreSave.setName(genre.getName());
        return genreRepository.save(genreSave);
    }

    @MutationMapping
    public Book addGenreToBook(@Argument Long id, @Argument Iterable<GenreInput> genres) {
        Set<Genre> genresToAdd = new HashSet<>();
        if (!bookRepository.existsById(id)) {
            return null;
        }

        for (GenreInput genreInput: genres) {
            Genre genre = genreRepository.findByName(genreInput.getName());
            if (genre == null) {
                Genre genreSave = new Genre();
                genreSave.setName(genreInput.getName());
                genreRepository.save(genreSave);
                genresToAdd.add(genreRepository.findByName(genreInput.getName()));
            } else {
                genresToAdd.add(genre);
            }
        }
        Book getBook = bookRepository.findById(id).get();

        getBook.setGenres(genresToAdd);
        return bookRepository.save(getBook);
    }

    private Specification<Genre> fetchBook() {
        return (root, query, builder) -> {
            Fetch<Genre, Book> f = root
                    .fetch("books", JoinType.LEFT);
            Join<Genre, Book> join = (Join<Genre, Book>) f;
            return join.getOn();
        };
    }
    private Specification<Genre> byId(Long id) {
        return (root, query, builder) -> builder.equal(root.get("id"), id);
    }
}