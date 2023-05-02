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

import static org.junit.jupiter.api.Assertions.assertTrue;

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
        assertTrue(books.size() > 0);
        Assertions.assertNotNull(books.get(0).getId());
        Assertions.assertNotNull(books.get(0).getTitle());
        Assertions.assertNotNull(books.get(0).getDescription());
    }
    @Test
    void findById() {
        String query = "query { book(id: 1) { id title description genres { name } } }";
        Book book = graphQlTest.document(query)
                .execute()
                .path("book")
                .entity(Book.class)
                .get();
        Assertions.assertNotNull(book);
        Assertions.assertNotNull(book.getId());
        Assertions.assertNotNull(book.getTitle());
        Assertions.assertNotNull(book.getDescription());
        assertTrue(book.getGenres().size() > 0);
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
        assertTrue(result);
    }

    @Test
    void searchBooks(){
        String query = "query { searchBooks(title:\"Book\"){ title isbn description } }";
        List<Book> books = graphQlTest.document(query)
                .execute()
                .path("searchBooks")
                .entityList(Book.class)
                .get();
        Assertions.assertNotNull(books);
        assertTrue(books.size() > 0);
        books.forEach(b -> assertTrue(b.getTitle().contains("Book")));
    }
    @Test
    void booksWithFilter(){
        //get all books with year greater than "2015"
        String query = "query {" +
                "    booksWithFilter(" +
                "        filter:{" +
                "            year:{" +
                "                operator:\"gt\"" +
                "                value:\"2015\"" +
                "            }" +
                "        }" +
                "    ) {" +
                "        title" +
                "        isbn" +
                "        year" +
                "        description" +
                "    }" +
                "}";
        List<Book> books = graphQlTest.document(query)
                .execute()
                .path("booksWithFilter")
                .entityList(Book.class)
                .get();
        Assertions.assertNotNull(books);
        assertTrue(books.size() > 0);
        books.forEach(b -> assertTrue(b.getYear() > 2015));
    }
}
