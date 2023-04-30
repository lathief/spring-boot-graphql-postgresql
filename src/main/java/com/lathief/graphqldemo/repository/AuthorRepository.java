package com.lathief.graphqldemo.repository;

import com.lathief.graphqldemo.model.Author;
import com.lathief.graphqldemo.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

public interface AuthorRepository extends JpaRepository<Author, Long>, JpaSpecificationExecutor<Author> {
    Author findByName(String name);
}
