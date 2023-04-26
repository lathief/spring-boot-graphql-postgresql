package com.lathief.graphqldemo.controller;

import com.lathief.graphqldemo.model.*;
import com.lathief.graphqldemo.repository.PublisherRepository;
import graphql.schema.DataFetchingEnvironment;
import graphql.schema.DataFetchingFieldSelectionSet;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import javax.persistence.criteria.Fetch;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import java.util.ArrayList;
import java.util.List;

@Controller
public class PublisherController {
    PublisherRepository publisherRepository;

    PublisherController(PublisherRepository publisherRepository) {
        this.publisherRepository = publisherRepository;
    }
    @QueryMapping
    public Iterable<Publisher> publishers(DataFetchingEnvironment environment) {
        DataFetchingFieldSelectionSet s = environment.getSelectionSet();
        List<Specification<Author>> specifications = new ArrayList<>();
        if (s.contains("books"))
            return publisherRepository.findAll((Sort) fetchBook());
        else
            return publisherRepository.findAll();
    }

    @QueryMapping
    public Publisher publisher(@Argument Long id, DataFetchingEnvironment environment) {
        Specification<Publisher> spec = byId(id);
        DataFetchingFieldSelectionSet s = environment.getSelectionSet();
        List<Specification<Author>> specifications = new ArrayList<>();
        if (s.contains("books"))
            spec = spec.and(fetchBook());
        return publisherRepository.findOne(spec).orElseThrow();
    }

    @MutationMapping
    public Publisher newPublisher(@Argument PublisherInput publisher) {
        Publisher publisherSave = new Publisher();
        publisherSave.setName(publisher.getName());
        publisherSave.setAddress(publisher.getAddress());
        return publisherRepository.save(publisherSave);
    }

    @MutationMapping
    public Publisher updatePublisher(@Argument Long id, @Argument PublisherInput publisher) {
        if (!publisherRepository.existsById(id)){
            return null;
        }
        Publisher publisherSave = publisherRepository.findById(id).get();
        if (publisher.getAddress() != null) {
            publisherSave.setAddress(publisher.getAddress());
        } if (publisher.getName() != null) {
            publisherSave.setName(publisher.getName());
        }
        return publisherRepository.save(publisherSave);
    }

    @MutationMapping
    public Boolean deletePublisher(@Argument Long id) {
        if (!publisherRepository.existsById(id)){
            return false;
        }
        publisherRepository.deleteById(id);
        return true;
    }

    private Specification<Publisher> fetchBook() {
        return (root, query, builder) -> {
            Fetch<Publisher, Book> f = root
                    .fetch("books", JoinType.LEFT);
            Join<Publisher, Book> join = (Join<Publisher, Book>) f;
            return join.getOn();
        };
    }
    private Specification<Publisher> byId(Long id) {
        return (root, query, builder) -> builder.equal(root.get("id"), id);
    }
}
