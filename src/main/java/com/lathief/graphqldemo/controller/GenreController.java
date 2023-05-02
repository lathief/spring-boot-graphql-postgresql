package com.lathief.graphqldemo.controller;

import com.lathief.graphqldemo.filter.FilterField;
import com.lathief.graphqldemo.model.*;
import com.lathief.graphqldemo.repository.AuthorRepository;
import com.lathief.graphqldemo.repository.BookRepository;
import com.lathief.graphqldemo.repository.GenreRepository;
import com.lathief.graphqldemo.repository.PublisherRepository;
import graphql.schema.DataFetchingEnvironment;
import graphql.schema.DataFetchingFieldSelectionSet;
import lombok.NonNull;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.Fetch;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@Transactional
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

    @QueryMapping
    public Iterable<Genre> genres(DataFetchingEnvironment environment) {
        DataFetchingFieldSelectionSet s = environment.getSelectionSet();
        List<Specification<Genre>> specifications = new ArrayList<>();
        if (s.contains("books")){
            List<Genre> temp = genreRepository.findAll(fetchBook());
            Set<String> set = new HashSet<>(temp.size());
            temp.removeIf(p -> !set.add(p.getName()));
            return temp;
        }
        else
            return genreRepository.findAll();
    }
    @QueryMapping
    public Genre genre(@Argument Long id, DataFetchingEnvironment environment) {
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
    public Book addGenreToBook(@Argument Long bookid, @Argument @NonNull List<GenreInput> genres) {
        List<Genre> genresToAdd = new ArrayList<>();
        if (!bookRepository.existsById(bookid)) {
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
        Book getBook = bookRepository.findById(bookid).get();

        getBook.setGenres(genresToAdd);
        return bookRepository.save(getBook);
    }
    @QueryMapping
    public Iterable<Book> getBookSpesificGenres(@Argument @NonNull List<GenreInput> genre,
                                            DataFetchingEnvironment dataFetchingEnvironment) {
        List<Book> resultBooks = new ArrayList<>();
        List<Book> result = new ArrayList<>();
        List<Book> filterResult = new ArrayList<>();
        List<Genre> genres = new ArrayList<>();
        Specification<Genre> spec = null;
        for (GenreInput g : genre) {
            DataFetchingFieldSelectionSet s = dataFetchingEnvironment.getSelectionSet();
            spec = byGenre(g.getName());
            genres.addAll(genreRepository.findAll(spec));
        }
        for ( Genre input : genres) {
            resultBooks.addAll(input.getBooks());
        }
        Set<String> bookNames = new HashSet<>();
        for (Book r: resultBooks){
            bookNames.add(r.getTitle());
        }
        for (String n: bookNames){
            result.add(bookRepository.findByTitle(n));
        }
        for (Book r: result) {
            int i = 0;
            for (Genre n: r.getGenres()){
                for (GenreInput g : genre) {
                    if (g.getName().equals(n.getName())){
                        i++;
                    }
                }
            }
            if (i == genre.size()) {
                filterResult.add(r);
            }
        }
        return filterResult;
    }
    private Specification<Genre> fetchBook() {
        return (root, query, builder) -> {
            Fetch<Genre, Book> f = root
                    .fetch("books", JoinType.INNER);
            Join<Genre, Book> join = (Join<Genre, Book>) f;
            return join.getOn();
        };
    }
    private Specification<Genre> byId(Long id) {
        return (root, query, builder) -> builder.equal(root.get("id"), id);
    }
    private Specification<Genre> byGenre(String name) {
        return (root, query, builder) -> {
            return builder.equal(root.get("name"), name);
        };
    }
}