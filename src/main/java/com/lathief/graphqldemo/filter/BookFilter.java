package com.lathief.graphqldemo.filter;

import lombok.Data;

@Data
public class BookFilter {
    private FilterField year;
    private FilterField price;
}
