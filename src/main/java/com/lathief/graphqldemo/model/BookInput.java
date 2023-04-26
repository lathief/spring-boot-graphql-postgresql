package com.lathief.graphqldemo.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookInput {
    private String title;
    private String isbn;
    private String description;
    private Long authorId;
    private Long publisherId;
}
