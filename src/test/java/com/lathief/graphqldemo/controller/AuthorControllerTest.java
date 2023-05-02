package com.lathief.graphqldemo.controller;

import com.lathief.graphqldemo.model.Author;
import org.checkerframework.checker.units.qual.A;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.graphql.tester.AutoConfigureGraphQlTester;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.graphql.test.tester.GraphQlTester;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureGraphQlTester
public class AuthorControllerTest {
    @Autowired
    private GraphQlTester graphQlTest;

    @Test
    void findAll(){
        String query = "query { authors {id name}}";
        List<Author> authors = graphQlTest.document(query)
                .execute()
                .path("authors[*]")
                .entityList(Author.class)
                .get();
        Assertions.assertTrue(authors.size() > 0);
        Assertions.assertNotNull(authors.get(0).getId());
        Assertions.assertNotNull(authors.get(0).getName());
    }
    @Test
    void findById() {
        String query = "query { author(id: 1) { id name books { title } } }";
        Author author = graphQlTest.document(query)
                .execute()
                .path("author")
                .entity(Author.class)
                .get();
        Assertions.assertNotNull(author);
        Assertions.assertNotNull(author.getId());
        Assertions.assertNotNull(author.getName());
        Assertions.assertNotNull(author.getBooks());
        Assertions.assertTrue(author.getBooks().size() > 0);
    }
    @Test
    void insertAuthor() {
        String query = "mutation { newAuthor(author: { name: \"Test Author\"}) { id name } }";
        Author author = graphQlTest.document(query)
                .execute()
                .path("newAuthor")
                .entity(Author.class)
                .get();
        Assertions.assertNotNull(author);
        Assertions.assertNotNull(author.getId());
        Assertions.assertEquals("Test Author", author.getName());
    }
    @Test
    void updateAuthor() {
        String query = "mutation { updateAuthor(id: 1, author: { name: \"Update Author\"}) { id name } }";
        Author author = graphQlTest.document(query)
                .execute()
                .path("updateAuthor")
                .entity(Author.class)
                .get();
        Assertions.assertNotNull(author);
        Assertions.assertNotNull(author.getId());
        Assertions.assertEquals("Update Author", author.getName());
    }

    @Test
    void deleteAuthor() {
        String query = "mutation { deleteAuthor(id: 2) }";
        Boolean result = graphQlTest.document(query)
                .execute()
                .path("deleteAuthor")
                .entity(Boolean.class)
                .get();
        Assertions.assertTrue(result);
    }
}
