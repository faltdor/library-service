package com.faltdor.library.producer;

import com.faltdor.library.domain.LibraryEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;


@Component
@AllArgsConstructor
@Slf4j
public class LibraryEventProducer {

    private final KafkaTemplate<Integer, String> kafkaTemplate;

    private final ObjectMapper objectMapper;

    public SendResult<Integer, String> sendLibraryEventSynchronous( LibraryEvent libraryEvent ) throws JsonProcessingException, ExecutionException, InterruptedException, TimeoutException {

        final Integer key = libraryEvent.getLibraryEvent();
        final String value = objectMapper.writeValueAsString( libraryEvent.getBook() );

        try {
            return kafkaTemplate.sendDefault( key, value ).get(1, TimeUnit.SECONDS);
        } catch ( ExecutionException | InterruptedException ex ) {
            log.error( "Error sending the message and exception is: {}", ex );
            throw ex;
        } catch ( Exception ex ) {
            log.error( "Error sending the message and exception is: {}", ex );
            throw ex;
        }
    }

    public void sendLibraryEvent( LibraryEvent libraryEvent ) throws JsonProcessingException {

        final Integer key = libraryEvent.getLibraryEvent();
        final String value = objectMapper.writeValueAsString( libraryEvent.getBook() );
        kafkaTemplate.sendDefault( key, value )
                     .addCallback( result -> log.info( "Message sent SuccessFully for the key : {} and the value is {}, partition is {} ", key, value, result.getRecordMetadata().partition() ),
                           ex -> log.error( "Error sending the message and exception is: {}", ex ) );
    }
}
