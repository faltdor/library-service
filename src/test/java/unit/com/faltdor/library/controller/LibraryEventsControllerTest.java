package com.faltdor.library.controller;

import com.faltdor.library.domain.LibraryEvent;
import com.faltdor.library.producer.LibraryEventProducer;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest( LibraryEventsController.class )
@AutoConfigureMockMvc
public class LibraryEventsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private LibraryEventProducer libraryEventProducer;


    @Test
    void createLibraryEvent() throws Exception {

        // GIVEN
        final LibraryEvent libraryEvent = LibraryEventsControllerIntegrationTest.buildLibraryEvent();
        doNothing().when( libraryEventProducer ).sendLibraryEvent( libraryEvent );
        doNothing().when( libraryEventProducer ).sendLibraryEventToSpecificTopic( libraryEvent );

        //WHEN
        mockMvc.perform( post( "/v1/libraryevent" )
              .content( objectMapper.writeValueAsString( libraryEvent ) )
              .contentType( MediaType.APPLICATION_JSON ) )
               .andExpect( status().isCreated() );
        //THEN

        Mockito.verify( libraryEventProducer ).sendLibraryEvent( libraryEvent );
        Mockito.verify( libraryEventProducer ).sendLibraryEventToSpecificTopic( libraryEvent );
    }
}
