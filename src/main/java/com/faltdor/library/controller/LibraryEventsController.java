package com.faltdor.library.controller;

import com.faltdor.library.domain.LibraryEvent;
import com.faltdor.library.domain.LibraryEventType;
import com.faltdor.library.producer.LibraryEventProducer;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;


@RestController
@AllArgsConstructor
public class LibraryEventsController {

    private final LibraryEventProducer libraryEventProducer;

    @PostMapping( "/v1/libraryevent" )
    public ResponseEntity<LibraryEvent> createLibraryEvent( @RequestBody @Valid LibraryEvent libraryEvent ) throws JsonProcessingException {

        libraryEvent.setLibraryEventType( LibraryEventType.NEW );
        libraryEventProducer.sendLibraryEvent( libraryEvent );
        libraryEventProducer.sendLibraryEventToSpecificTopic( libraryEvent );

        return ResponseEntity.status( HttpStatus.CREATED ).body( libraryEvent );
    }

    @PostMapping( "/v1/libraryevent/sync" )
    public ResponseEntity<LibraryEvent> createLibraryEventSync( @RequestBody LibraryEvent libraryEvent ) throws JsonProcessingException, ExecutionException, InterruptedException, TimeoutException {

        libraryEventProducer.sendLibraryEventSynchronous( libraryEvent );

        return ResponseEntity.status( HttpStatus.CREATED ).body( libraryEvent );
    }



    @PutMapping( "/v1/libraryevent" )
    public ResponseEntity<?> updateLibraryEvent( @RequestBody @Valid LibraryEvent libraryEvent ) throws JsonProcessingException {


        if(libraryEvent.getLibraryEvent() == null){
            return ResponseEntity.status( HttpStatus.BAD_REQUEST ).body( "Library event id cannot be null" );
        }

        libraryEvent.setLibraryEventType( LibraryEventType.UPDATE );
        libraryEventProducer.sendLibraryEvent( libraryEvent );
        libraryEventProducer.sendLibraryEventToSpecificTopic( libraryEvent );

        return ResponseEntity.status( HttpStatus.OK ).body( libraryEvent );
    }
}
