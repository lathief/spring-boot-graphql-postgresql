package com.lathief.graphqldemo.repository;

import com.lathief.graphqldemo.model.Author;
import com.lathief.graphqldemo.model.Publisher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface PublisherRepository extends JpaRepository<Publisher, Long>, JpaSpecificationExecutor<Publisher> {
    Publisher findByName(String name);
}
