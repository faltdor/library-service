package com.faltdor.library.controller;

import com.faltdor.library.domain.LibraryEvent;
import com.faltdor.library.producer.LibraryEventProducer;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;


@RestController
@AllArgsConstructor
public class LibraryEventsController {

    private final LibraryEventProducer libraryEventProducer;

    @PostMapping( "v1/libraryevent" )
    public ResponseEntity<LibraryEvent> createLibraryEvent( @RequestBody LibraryEvent libraryEvent ) throws JsonProcessingException {

        libraryEventProducer.sendLibraryEvent( libraryEvent );

        return ResponseEntity.status( HttpStatus.CREATED ).body( libraryEvent );
    }

    @PostMapping( "v1/libraryevent/sync" )
    public ResponseEntity<LibraryEvent> createLibraryEventSync( @RequestBody LibraryEvent libraryEvent ) throws JsonProcessingException, ExecutionException, InterruptedException, TimeoutException {

        libraryEventProducer.sendLibraryEventSynchronous( libraryEvent );

        return ResponseEntity.status( HttpStatus.CREATED ).body( libraryEvent );
    }
}
