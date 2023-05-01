package com.lathief.graphqldemo.repository;

import com.lathief.graphqldemo.model.Book;
import com.lathief.graphqldemo.model.Publisher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

public interface BookRepository extends JpaRepository<Book, Long>, JpaSpecificationExecutor<Book> {
    List<Book> findByTitleContaining(String title);
    Book findByTitle(String title);
    @Query(nativeQuery = true, value = "SELECT * FROM book WHERE publisher_id = ?1")
    List<Book> findBookByPublisher(Long id);
}
