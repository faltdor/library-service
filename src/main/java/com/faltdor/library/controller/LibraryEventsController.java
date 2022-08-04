package com.faltdor.library.controller;

import com.faltdor.library.domain.LibraryEvent;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class LibraryEventsController {

    @PostMapping( "v1/libraryevent" )
    public ResponseEntity<LibraryEvent> createLibraryEvent( @RequestBody LibraryEvent libraryEvent ) {

        return ResponseEntity.status( HttpStatus.CREATED ).body( libraryEvent );
    }
}
