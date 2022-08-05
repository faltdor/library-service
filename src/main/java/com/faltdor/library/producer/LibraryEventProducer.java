package com.faltdor.library.producer;

import com.faltdor.library.domain.LibraryEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;


@Component
@Slf4j
public class LibraryEventProducer {

    @Autowired
    KafkaTemplate<Integer, String> kafkaTemplate;

    @Autowired
    ObjectMapper objectMapper;

    public void sendLibraryEvent( LibraryEvent libraryEvent ) throws JsonProcessingException {

        Integer key = libraryEvent.getLibraryEvent();
        String value = objectMapper.writeValueAsString( libraryEvent.getBook() );
        kafkaTemplate.sendDefault( key, value )
                     .addCallback( result -> log.info( "Message sent SuccessFully for the key : {} and the value is {}, partition is {} ", key, value, result.getRecordMetadata().partition() ),
                           ex -> log.error( "Error sending the message and exception is: {}", ex ) );
    }
}
