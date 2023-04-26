package com.lathief.graphqldemo.repository;

import com.lathief.graphqldemo.model.Book;
import com.lathief.graphqldemo.model.Publisher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

public interface BookRepository extends JpaRepository<Book, Long>, JpaSpecificationExecutor<Book> {

}
