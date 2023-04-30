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

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureGraphQlTester
public class AuthorControllerTest {
    @Autowired
    private GraphQlTester graphQlTest;

    @Test
    void authorsTest(){
        String query = "query { authors {id name}}";
        List<Author> authors = graphQlTest.document(query)
                .execute()
                .path("author[*]")
                .entityList(Author.class)
                .get();
        Assertions.assertTrue(authors.size() > 0);
        Assertions.assertNotNull(authors.get(0).getId());
        Assertions.assertNotNull(authors.get(0).getName());
    }
}
