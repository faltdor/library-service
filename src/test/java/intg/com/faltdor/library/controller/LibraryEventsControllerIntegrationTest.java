package com.faltdor.library.controller;

import com.faltdor.library.domain.Book;
import com.faltdor.library.domain.LibraryEvent;
import com.faltdor.library.domain.LibraryEventType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;

import static org.junit.jupiter.api.Assertions.*;

import static com.faltdor.library.controller.TestConfig.EASY_RANDOM;


@SpringBootTest( webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT )
public class LibraryEventsControllerIntegrationTest {

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Test
    void createLibraryEvent() {
        //GIVEN
        final HttpEntity<LibraryEvent> httpEntity = buildLibraryEventHttpEntity();

        //WHEN
        final ResponseEntity<LibraryEvent> responseEntity = testRestTemplate.exchange( "/v1/libraryevent", HttpMethod.POST, httpEntity, LibraryEvent.class );
        //THEN
        assertEquals( HttpStatus.CREATED, responseEntity.getStatusCode() );
    }

    @Test
    void createLibraryEventSync() {
        //GIVEN
        final HttpEntity<LibraryEvent> httpEntity = buildLibraryEventHttpEntity();

        //WHEN
        final ResponseEntity<LibraryEvent> responseEntity = testRestTemplate.exchange( "/v1/libraryevent/sync", HttpMethod.POST, httpEntity, LibraryEvent.class );
        //THEN
        assertEquals( HttpStatus.CREATED, responseEntity.getStatusCode() );
    }

    private HttpEntity<LibraryEvent> buildLibraryEventHttpEntity() {

        final LibraryEvent libraryEvent = LibraryEvent.builder()
                                                      .libraryEvent( null )
                                                      .libraryEventType( LibraryEventType.NEW )
                                                      .book( Book.builder()
                                                                 .bookAuthor( EASY_RANDOM.nextObject( String.class ) )
                                                                 .bookId( EASY_RANDOM.nextInt() )
                                                                 .bookName( EASY_RANDOM.nextObject( String.class ) )
                                                                 .build() )
                                                      .build();
        final HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add( "content-type", MediaType.APPLICATION_JSON.toString() );
        return new HttpEntity<>( libraryEvent );
    }
}