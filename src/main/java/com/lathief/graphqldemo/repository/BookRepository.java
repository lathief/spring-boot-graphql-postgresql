package com.lathief.graphqldemo.repository;

import com.lathief.graphqldemo.model.Book;
import com.lathief.graphqldemo.model.Publisher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

public interface BookRepository extends JpaRepository<Book, Long>, JpaSpecificationExecutor<Book> {
    List<Book> findByTitleContaining(String title);
    Book findByTitle(String title);
}
