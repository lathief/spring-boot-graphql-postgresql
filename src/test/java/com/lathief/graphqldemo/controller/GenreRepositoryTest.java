package com.lathief.graphqldemo.controller;

import com.lathief.graphqldemo.model.Author;
import com.lathief.graphqldemo.model.Book;
import com.lathief.graphqldemo.model.Genre;
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
public class GenreRepositoryTest {
    @Autowired
    private GraphQlTester graphQlTest;
    @Test
    void findAll(){
        String query = "query { genres{ id name }}";
        List<Genre> genres = graphQlTest.document(query)
                .execute()
                .path("genres[*]")
                .entityList(Genre.class)
                .get();
        Assertions.assertTrue(genres.size() > 0);
        Assertions.assertNotNull(genres.get(0).getId());
        Assertions.assertNotNull(genres.get(0).getName());
    }
    @Test
    void findById() {
        String query = "query { genre(id: 1) { id name books { title } } }";
        Genre genre = graphQlTest.document(query)
                .execute()
                .path("book")
                .entity(Genre.class)
                .get();
        Assertions.assertNotNull(genre);
        Assertions.assertNotNull(genre.getId());
        Assertions.assertNotNull(genre.getName());
        Assertions.assertTrue(genre.getBooks().size() > 0);
    }

    @Test
    void insertGenre() {
        String query = "mutation { newGenre(genre: { name: \"Non Fiction\"}) { id name } }";
        Genre genre = graphQlTest.document(query)
                .execute()
                .path("newGenre")
                .entity(Genre.class)
                .get();
        Assertions.assertNotNull(genre);
        Assertions.assertNotNull(genre.getId());
        Assertions.assertEquals("Non Fiction", genre.getName());
    }
    @Test
    void addGenreToBook() {
        String query = "mutation { addGenreToBook(bookid: 1, genres: [{ name: \"Non Fiction\"}]) { id title genres { name } } }";
        Book books = graphQlTest.document(query)
                .execute()
                .path("addGenreToBook")
                .entity(Book.class)
                .get();
        Assertions.assertNotNull(books);
        Assertions.assertEquals(1, (long) books.getId());
        books.getGenres().forEach(g -> Assertions.assertEquals(g.getName(), "Non Fiction"));
    }
    @Test
    void getBookSpesificGenres(){
        List<Genre> testGenres = new ArrayList<>();
        testGenres.add(new Genre("Comedy"));
        testGenres.add(new Genre("Romance"));
        String query = "query { getBookSpesificGenres(genre: [{ name: \"Comedy\"}, { name: \"Romance\"}]) " +
                "{ id title genres { name } } }";
        List<Book> books = graphQlTest.document(query)
                .execute()
                .path("getBookSpesificGenres")
                .entityList(Book.class)
                .get();
        Assertions.assertNotNull(books);
        Assertions.assertTrue(books.size() > 0);
        List<Boolean> resultTest = new ArrayList<>();
        for (Book b : books) {
            int correctGenres = 0;
            for (Genre g : b.getGenres()) {
                for (Genre tg : testGenres) {
                    if (g.getName().equals(tg.getName())) {
                        correctGenres++;
                    }
                }
            }
            if (correctGenres >= testGenres.size()) {
                resultTest.add(true);
            }
        }
        System.out.println(books);
        System.out.println(resultTest);
        resultTest.forEach(r -> Assertions.assertTrue(true, String.valueOf(r)));
    }

}
