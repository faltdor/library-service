package com.faltdor.library.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;


@AllArgsConstructor
@Builder
@Data
public class Book {
    private Integer bookId;

    private String bookName;

    private String bookAuthor;
}
