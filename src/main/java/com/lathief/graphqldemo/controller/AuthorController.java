package com.lathief.graphqldemo.controller;

import com.lathief.graphqldemo.model.Author;
import com.lathief.graphqldemo.model.AuthorInput;
import com.lathief.graphqldemo.model.Book;
import com.lathief.graphqldemo.model.Genre;
import com.lathief.graphqldemo.repository.AuthorRepository;
import com.lathief.graphqldemo.repository.BookRepository;
import graphql.schema.DataFetchingEnvironment;
import graphql.schema.DataFetchingFieldSelectionSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;

import javax.persistence.criteria.Fetch;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Controller
public class AuthorController {
    AuthorRepository authorRepository;
    BookRepository bookRepository;

    AuthorController(AuthorRepository authorRepository, BookRepository bookRepository){
        this.authorRepository = authorRepository;
        this.bookRepository = bookRepository;
    }

    @QueryMapping
    public Iterable<Author> authors(DataFetchingEnvironment environment) {
        DataFetchingFieldSelectionSet s = environment.getSelectionSet();
        List<Specification<Author>> specifications = new ArrayList<>();
        if (s.contains("books")){
            List<Author> temp = authorRepository.findAll(fetchBook());
            Set<String> set = new HashSet<>(temp.size());
            temp.removeIf(p -> !set.add(p.getName()));
            return temp;
        }
        else
            return authorRepository.findAll();
    }

    @QueryMapping
    public Author author(@Argument Long id, DataFetchingEnvironment environment) {
        Specification<Author> spec = byId(id);
        DataFetchingFieldSelectionSet s = environment.getSelectionSet();
        List<Specification<Author>> specifications = new ArrayList<>();
        if (s.contains("books"))
            spec = spec.and(fetchBook());
        return authorRepository.findOne(spec).orElseThrow();
    }

    @MutationMapping
    public Author newAuthor(@Argument AuthorInput author) {
        Author authorSave = new Author();
        authorSave.setName(author.getName());
        return authorRepository.save(authorSave);
    }

    @MutationMapping
    public Author updateAuthor(@Argument Long id, @Argument AuthorInput author) {
        if (!authorRepository.existsById(id)){
            return null;
        }
        Author authorSave = authorRepository.findById(id).get();
        authorSave.setName(author.getName());
        return authorRepository.save(authorSave);
    }

    @MutationMapping
    public Boolean deleteAuthor(@Argument Long id) {
        if (!authorRepository.existsById(id)){
            return false;
        }
        authorRepository.deleteById(id);
        return true;
    }

    private Specification<Author> fetchBook() {
        return (root, query, builder) -> {
            Fetch<Author, Book> f = root
                    .fetch("books", JoinType.LEFT);
            Join<Author, Book> join = (Join<Author, Book>) f;
            return join.getOn();
        };
    }
    private Specification<Author> byId(Long id) {
        return (root, query, builder) -> builder.equal(root.get("id"), id);
    }

}
