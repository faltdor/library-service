package com.faltdor.library.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;


@AllArgsConstructor
@Data
@Builder
public class LibraryEvent {

    private Integer libraryEvent;

    private Book book;
}
