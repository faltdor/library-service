package com.faltdor.library.producer;

import com.faltdor.library.controller.LibraryEventsControllerIntegrationTest;
import com.faltdor.library.domain.LibraryEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.util.concurrent.SettableListenableFuture;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.isA;


@ExtendWith( MockitoExtension.class )
public class LibraryEventProducerTest {

    @Mock
    KafkaTemplate<Integer, String> kafkaTemplate;

    @Spy
    ObjectMapper objectMapper = new ObjectMapper();

    @InjectMocks
    LibraryEventProducer libraryEventProducer;

    @Test
    void sendLibraryEvent() throws JsonProcessingException, InterruptedException, ExecutionException, TimeoutException {

        final LibraryEvent libraryEvent = LibraryEventsControllerIntegrationTest.buildLibraryEvent();

        SettableListenableFuture future = new SettableListenableFuture();
        future.set( new RuntimeException( "Exception calling Kafka" ) );
        Mockito.when( kafkaTemplate.sendDefault( libraryEvent.getLibraryEvent(), objectMapper.writeValueAsString( libraryEvent.getBook() ) ) ).thenReturn( future );

        assertThrows( Exception.class, () -> libraryEventProducer.sendLibraryEventSynchronous( libraryEvent ) );
    }
}