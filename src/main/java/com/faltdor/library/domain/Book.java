package com.faltdor.library.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;


@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class Book {
    private Integer bookId;

    @NotNull
    @NotEmpty
    private String bookName;

    @NotNull
    @NotEmpty
    private String bookAuthor;
}
