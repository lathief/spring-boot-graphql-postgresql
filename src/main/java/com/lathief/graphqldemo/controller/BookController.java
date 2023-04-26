package com.lathief.graphqldemo.controller;

import com.lathief.graphqldemo.model.*;
import com.lathief.graphqldemo.repository.AuthorRepository;
import com.lathief.graphqldemo.repository.BookRepository;
import com.lathief.graphqldemo.repository.PublisherRepository;
import graphql.schema.DataFetchingEnvironment;
import graphql.schema.DataFetchingFieldSelectionSet;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import javax.persistence.criteria.Fetch;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Controller
public class BookController {
    BookRepository bookRepository;
    PublisherRepository publisherRepository;
    AuthorRepository authorRepository;

    BookController(BookRepository bookRepository, PublisherRepository publisherRepository, AuthorRepository authorRepository) {
        this.bookRepository = bookRepository;
        this.publisherRepository = publisherRepository;
        this.authorRepository = authorRepository;
    }
//    newBook(book: BookInput!): Book
//    updateBook(id: ID!, book: BookInput!): Book
//    deleteBook(id: ID!): Boolean
    @QueryMapping
    public Iterable<Book> books(DataFetchingEnvironment environment) {
        DataFetchingFieldSelectionSet s = environment.getSelectionSet();
        List<Specification<Author>> specifications = new ArrayList<>();
        if (s.contains("author") && !s.contains("publisher"))
            return bookRepository.findAll(fetchAuthor());
        else if (s.contains("publisher") && !s.contains("author"))
            return bookRepository.findAll(fetchPublisher());
        else if (s.contains("publisher") && s.contains("author"))
            return bookRepository.findAll(fetchPublisher().and(fetchAuthor()));
        else
            return bookRepository.findAll();
    }
    @QueryMapping
    public Book book(@Argument Long id, DataFetchingEnvironment environment) {
        System.out.println(id);
        Specification<Book> spec = byId(id);
        DataFetchingFieldSelectionSet selectionSet = environment
                .getSelectionSet();
        if (selectionSet.contains("publisher"))
            spec = spec.and(fetchPublisher());
        if (selectionSet.contains("author"))
            spec = spec.and(fetchAuthor());
        return bookRepository.findOne(spec).orElseThrow(NoSuchElementException::new);
    }

    @MutationMapping
    public Book newBook(@Argument BookInput book) {
        Book bookSave = new Book();
        Author author = authorRepository.findById(book.getAuthorId()).get();
        Publisher publisher = publisherRepository.findById(book.getPublisherId()).get();
        bookSave.setAuthor(author);
        bookSave.setPublisher(publisher);
        bookSave.setTitle(book.getTitle());
        bookSave.setIsbn(book.getIsbn());
        bookSave.setDescription(book.getDescription());
        return bookRepository.save(bookSave);
    }

    @MutationMapping
    public Book updateBook(@Argument Long id, @Argument BookInput book) {
        if (!bookRepository.existsById(id)){
            return null;
        } if (!publisherRepository.existsById(id)){
            return null;
        } if (!authorRepository.existsById(id)){
            return null;
        }
        Book bookUpdate = bookRepository.findById(id).get();
        Author author = authorRepository.findById(book.getAuthorId()).get();
        Publisher publisher = publisherRepository.findById(book.getPublisherId()).get();
        if (book.getAuthorId() != null) {
            bookUpdate.setAuthor(authorRepository.findById(book.getAuthorId()).get());
        } if (book.getPublisherId() != null) {
            bookUpdate.setPublisher(publisherRepository.findById(book.getPublisherId()).get());
        } if (book.getTitle() != null) {
            bookUpdate.setTitle(book.getTitle());
        } if (book.getIsbn() != null) {
            bookUpdate.setIsbn(book.getIsbn());
        } if (book.getDescription() != null) {
            bookUpdate.setDescription(book.getDescription());
        }
        return bookRepository.save(bookUpdate);
    }

    @MutationMapping
    public Boolean deleteBook(@Argument Long id) {
        if (!bookRepository.existsById(id)){
            return false;
        }
        bookRepository.deleteById(id);
        return true;
    }

    private Specification<Book> fetchPublisher() {
        return (root, query, builder) -> {
            Fetch<Book, Publisher> f = root
                    .fetch("publisher", JoinType.LEFT);
            Join<Book, Publisher> join = (Join<Book, Publisher>) f;
            return join.getOn();
        };
    }
    private Specification<Book> fetchAuthor() {
        return (root, query, builder) -> {
            Fetch<Book, Author> f = root
                    .fetch("author", JoinType.LEFT);
            Join<Book, Author> join = (Join<Book, Author>) f;
            return join.getOn();
        };
    }
    private Specification<Book> byId(Long id) {
        return (root, query, builder) -> builder.equal(root.get("id"), id);
    }
}
