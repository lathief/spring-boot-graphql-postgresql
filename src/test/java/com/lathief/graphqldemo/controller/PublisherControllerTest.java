package com.lathief.graphqldemo.controller;

import com.lathief.graphqldemo.model.Book;
import com.lathief.graphqldemo.model.Publisher;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.graphql.tester.AutoConfigureGraphQlTester;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.graphql.test.tester.GraphQlTester;

import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureGraphQlTester
public class PublisherControllerTest {
    @Autowired
    private GraphQlTester graphQlTest;
    @Test
    void findAll(){
        String query = "query { publishers{id name }}";
        List<Publisher> publishers = graphQlTest.document(query)
                .execute()
                .path("publishers[*]")
                .entityList(Publisher.class)
                .get();
        Assertions.assertTrue(publishers.size() > 0);
        Assertions.assertNotNull(publishers.get(0).getId());
        Assertions.assertNotNull(publishers.get(0).getName());
    }
    @Test
    void findById() {
        String query = "query { publisher(id: 1) { id name books { title } } }";
        Publisher publisher = graphQlTest.document(query)
                .execute()
                .path("publisher")
                .entity(Publisher.class)
                .get();
        Assertions.assertNotNull(publisher);
        Assertions.assertNotNull(publisher.getId());
        Assertions.assertNotNull(publisher.getName());
        Assertions.assertTrue(publisher.getBooks().size() > 0);
    }
    @Test
    void insertPublisher() {
        String query = "mutation { newPublisher(publisher: { name: \"Test Publisher\", })" +
                " { id name books { title } } }";
        Publisher publisher = graphQlTest.document(query)
                .execute()
                .path("newPublisher")
                .entity(Publisher.class)
                .get();
        Assertions.assertNotNull(publisher);
        Assertions.assertNotNull(publisher.getId());
        Assertions.assertEquals("Test Publisher", publisher.getName());
    }
    @Test
    void updatePublisher() {
        String query = "mutation { updatePublisher(id: 1, publisher: { name: \"Update Publisher\", })" +
                " { id name } }";
        Publisher publisher = graphQlTest.document(query)
                .execute()
                .path("updatePublisher")
                .entity(Publisher.class)
                .get();
        Assertions.assertNotNull(publisher);
        Assertions.assertNotNull(publisher.getId());
        Assertions.assertEquals("Update Publisher", publisher.getName());
    }

    @Test
    void deletePublisher() {
        String query = "mutation { deletePublisher(id: 2) }";
        Boolean result = graphQlTest.document(query)
                .execute()
                .path("deletePublisher")
                .entity(Boolean.class)
                .get();
        Assertions.assertTrue(result);
    }
}
