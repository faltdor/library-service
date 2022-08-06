package com.faltdor.library.controller;

import com.faltdor.library.domain.Book;
import com.faltdor.library.domain.LibraryEvent;
import com.faltdor.library.domain.LibraryEventType;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.IntegerDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.context.TestPropertySource;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

import static com.faltdor.library.controller.TestConfig.EASY_RANDOM;


@SpringBootTest( webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT )
@EmbeddedKafka( topics = { "library-events" }, partitions = 3 )
@TestPropertySource( properties = { "spring.kafka.producer.bootstrap-servers=${spring.embedded.kafka.brokers}",
                                    "spring.kafka.admin.properties.bootstrap.servers=${spring.embedded.kafka.brokers}   " } )
public class LibraryEventsControllerIntegrationTest {

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private EmbeddedKafkaBroker embeddedKafkaBroker;

    private Consumer<Integer, String> consumer;

    @BeforeEach
    public void setUp() {

        final Map<String, Object> config = new HashMap<>( KafkaTestUtils.consumerProps( "group1", "true", embeddedKafkaBroker ) );
        consumer = new DefaultKafkaConsumerFactory<Integer, String>( config, new IntegerDeserializer(), new StringDeserializer() )
              .createConsumer();
        embeddedKafkaBroker.consumeFromAllEmbeddedTopics( consumer );
    }

    @AfterEach
    public void tearDown() {
        consumer.close();
    }

    @Test
    void createLibraryEvent() throws JsonProcessingException {
        //GIVEN
        final LibraryEvent libraryEvent = buildLibraryEvent();
        final HttpEntity<LibraryEvent> httpEntity = buildLibraryEventHttpEntity( libraryEvent );

        //WHEN
        final ResponseEntity<LibraryEvent> responseEntity = testRestTemplate.exchange( "/v1/libraryevent", HttpMethod.POST, httpEntity, LibraryEvent.class );
        //THEN
        assertEquals( HttpStatus.CREATED, responseEntity.getStatusCode() );


        final ConsumerRecord<Integer, String> consumerRecord = KafkaTestUtils.getSingleRecord( consumer, "library-events" );
        assertEquals( consumerRecord.value(), objectMapper.writeValueAsString( libraryEvent.getBook() ) );
    }

    @Test
    void createLibraryEventSync() throws JsonProcessingException {
        //GIVEN
        final LibraryEvent libraryEvent = buildLibraryEvent();
        final HttpEntity<LibraryEvent> httpEntity = buildLibraryEventHttpEntity( libraryEvent );

        //WHEN
        final ResponseEntity<LibraryEvent> responseEntity = testRestTemplate.exchange( "/v1/libraryevent/sync", HttpMethod.POST, httpEntity, LibraryEvent.class );
        //THEN
        assertEquals( HttpStatus.CREATED, responseEntity.getStatusCode() );
        final ConsumerRecord<Integer, String> consumerRecord = KafkaTestUtils.getSingleRecord( consumer, "library-events" );
        assertEquals( consumerRecord.value(), objectMapper.writeValueAsString( libraryEvent.getBook() ) );
    }

    private HttpEntity<LibraryEvent> buildLibraryEventHttpEntity( final LibraryEvent libraryEvent ) {

        final HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add( "content-type", MediaType.APPLICATION_JSON.toString() );
        return new HttpEntity<>( libraryEvent );
    }

    public static LibraryEvent buildLibraryEvent() {

        return LibraryEvent.builder()
                           .libraryEventType( LibraryEventType.NEW )
                           .book( Book.builder()
                                      .bookAuthor( EASY_RANDOM.nextObject( String.class ) )
                                      .bookId( EASY_RANDOM.nextInt() )
                                      .bookName( EASY_RANDOM.nextObject( String.class ) )
                                      .build() )
                           .build();
    }
}