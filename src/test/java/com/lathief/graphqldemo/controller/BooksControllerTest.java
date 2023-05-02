package com.lathief.graphqldemo.controller;

import com.lathief.graphqldemo.model.Book;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.graphql.tester.AutoConfigureGraphQlTester;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.graphql.test.tester.GraphQlTester;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureGraphQlTester
public class BooksControllerTest {
    @Autowired
    private GraphQlTester graphQlTest;
    @Test
    void findAll(){
        String query = "query { books{id title description }}";
        List<Book> books = graphQlTest.document(query)
                .execute()
                .path("books[*]")
                .entityList(Book.class)
                .get();
        Assertions.assertTrue(books.size() > 0);
        Assertions.assertNotNull(books.get(0).getId());
        Assertions.assertNotNull(books.get(0).getTitle());
        Assertions.assertNotNull(books.get(0).getDescription());
    }
    @Test
    void findById() {
        String query = "query { book(id: 1) { id title description genres { name } } }";
        Book Book = graphQlTest.document(query)
                .execute()
                .path("book")
                .entity(Book.class)
                .get();
        Assertions.assertNotNull(Book);
        Assertions.assertNotNull(Book.getId());
        Assertions.assertNotNull(Book.getTitle());
        Assertions.assertNotNull(Book.getDescription());
        Assertions.assertTrue(Book.getGenres().size() > 0);
    }
    @Test
    void insertBook() {
        String query = "mutation { newBook(book: { title: \"Test Book\", isbn: \"12345\", authorId:1, publisherId:1 })" +
                " { id title isbn author { name } publisher { name } } }";
        Book book = graphQlTest.document(query)
                .execute()
                .path("newBook")
                .entity(Book.class)
                .get();
        Assertions.assertNotNull(book);
        Assertions.assertNotNull(book.getId());
        Assertions.assertEquals("Test Book", book.getTitle());
        Assertions.assertEquals("12345", book.getIsbn());
    }
    @Test
    void updateBook() {
        String query = "mutation { updateBook(id: 1, book: { title: \"Update Book\", isbn:\"12349\",authorId:1, publisherId:2})" +
                " { id title isbn } }";
        Book book = graphQlTest.document(query)
                .execute()
                .path("updateBook")
                .entity(Book.class)
                .get();
        Assertions.assertNotNull(book);
        Assertions.assertNotNull(book.getId());
        Assertions.assertEquals("Update Book", book.getTitle());
        Assertions.assertEquals("12349", book.getIsbn());
    }

    @Test
    void deleteBook() {
        String query = "mutation { deleteBook(id: 2) }";
        Boolean result = graphQlTest.document(query)
                .execute()
                .path("deleteBook")
                .entity(Boolean.class)
                .get();
        Assertions.assertTrue(result);
    }
}
