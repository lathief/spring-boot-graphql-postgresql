package com.lathief.graphqldemo;

import com.lathief.graphqldemo.model.Author;
import com.lathief.graphqldemo.model.Book;
import com.lathief.graphqldemo.model.Genre;
import com.lathief.graphqldemo.model.Publisher;
import com.lathief.graphqldemo.repository.AuthorRepository;
import com.lathief.graphqldemo.repository.BookRepository;
import com.lathief.graphqldemo.repository.GenreRepository;
import com.lathief.graphqldemo.repository.PublisherRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.ClientInfoStatus;
import java.util.*;
import java.util.stream.Collectors;

@Component
@Service
public class DatabaseSeeder implements ApplicationRunner {
    AuthorRepository authorRepository;
    BookRepository bookRepository;
    GenreRepository genreRepository;
    PublisherRepository publisherRepository;

    DatabaseSeeder(AuthorRepository authorRepository, BookRepository bookRepository, GenreRepository genreRepository,
                   PublisherRepository publisherRepository) {
        this.authorRepository = authorRepository;
        this.bookRepository = bookRepository;
        this.genreRepository = genreRepository;
        this.publisherRepository = publisherRepository;
    }
    @Override
    @Transactional
    public void run(ApplicationArguments args) throws Exception {
        List<Author> authors = new ArrayList<>();
        authors.add(new Author("Author 1"));
        authors.add(new Author("Author 2"));
        authors.add(new Author("Author 3"));
        for (Author a : authors) {
            if (authorRepository.findByName(a.getName()) == null) {
                authorRepository.save(a);
            }
        }
        List<Publisher> publishers = new ArrayList<>();
        publishers.add(new Publisher("Publisher A", "27895 Felipe Haven Suite 795"));
        publishers.add(new Publisher("Publisher B", "1496 Golf Course Drive"));
        publishers.add(new Publisher("Publisher C", "937 Edgewood Avenue"));
        for (Publisher p : publishers) {
            if (publisherRepository.findByName(p.getName()) == null) {
                publisherRepository.save(p);
            }
        }

        List<Genre> genres = initGenres();
        for (Genre g : genres) {
            if (genreRepository.findByName(g.getName()) == null) {
                genreRepository.save(g);
            }
        }
        List<Book> books = new ArrayList<>();
        books.add(new Book("Book A1", "1234", "Lorem Ipsum", 2013, 210000,
                new Author("Author 1"), new Publisher("Publisher A", "27895 Felipe Haven Suite 795"),
                null));
        books.add(new Book("Book A2", "1235", "Lorem Ipsum", 2014, 124000,
                new Author("Author 2"), new Publisher("Publisher B", "1496 Golf Course Drive"),
                null));
        books.add(new Book("Book A3", "1236", "Lorem Ipsum", 2015, 90000,
                new Author("Author 3"), new Publisher("Publisher C", "937 Edgewood Avenue"),
                null));
        books.add(new Book("Book A4", "1237", "Lorem Ipsum", 2016, 80000,
                new Author("Author 2"), new Publisher("Publisher B", "1496 Golf Course Drive"),
                null));
        books.add(new Book("Book A5", "1238", "Lorem Ipsum", 2017, 75000,
                new Author("Author 1"), new Publisher("Publisher C", "937 Edgewood Avenue"),
                null));

        for (Book book: books) {
            List<Genre> randomGenres = new ArrayList<>();
            List<Genre> getGenres = new ArrayList<>();
            Set<Genre> saveGenres = new HashSet<>();
            List<Genre> tempGenres = initGenres();
            int numberOfElements = 3;

            for (int i = 0; i < numberOfElements; i++) {
                Random rand = new Random();
                int randomIndex = rand.nextInt(tempGenres.size());
                randomGenres.add(tempGenres.get(randomIndex));
                tempGenres.remove(randomIndex);
            }

            if (publisherRepository.findByName(book.getPublisher().getName()) != null) {
                book.setPublisher(publisherRepository.findByName(book.getPublisher().getName()));
            }
            if (authorRepository.findByName(book.getAuthor().getName()) != null) {
                book.setAuthor(authorRepository.findByName(book.getAuthor().getName()));
            }
            for (Genre g: randomGenres) {
                if (genreRepository.findByName(g.getName()) != null) {
                    getGenres.add(genreRepository.findByName(g.getName()));
                } else {
                    genreRepository.save(g);
                    getGenres.add(genreRepository.findByName(g.getName()));
                }
            }
            if (bookRepository.findByTitle(book.getTitle()) == null) {
                bookRepository.save(book);
            }
            book.setGenres(getGenres);
            bookRepository.save(book);
        }
    }
    public List<Genre> initGenres() {
        List<Genre> genres = new ArrayList<>();
        genres.add(new Genre("Crime"));
        genres.add(new Genre("Comedy"));
        genres.add(new Genre("Biographical"));
        genres.add(new Genre("Fantasy"));
        genres.add(new Genre("Adventure"));
        genres.add(new Genre("Romance"));
        return genres;
    }
}
