package com.lathief.graphqldemo.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookInput {
    private String title;
    private String isbn;
    private String description;
    private Integer year;
    private Integer price;
    private String genre;
    private Long authorId;
    private Long publisherId;
}
