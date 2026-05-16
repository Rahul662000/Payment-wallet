package com.rahul.wallet_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.util.backoff.FixedBackOff;

@Configuration
@EnableKafka
public class KafkaConsumerConfig {

    @Bean
    public DefaultErrorHandler errorHandler(KafkaTemplate<Object, Object> template){

        /*
         * DeadLetterPublishingRecoverer
         *
         * If message processing fails even after retries,
         * this recoverer publishes failed message
         * to Dead Letter Topic (DLT).
         *
         * Helps avoid losing messages permanently.
         */
        DeadLetterPublishingRecoverer recoverer =
                new DeadLetterPublishingRecoverer(
                        template
                );
        /*
         * FixedBackOff
         *
         * 2000L = wait 2 seconds between retries
         *
         * 3 = retry 3 times
         *
         * Retry Flow:
         *
         * 1st failure
         * wait 2 sec
         *
         * 2nd retry
         * wait 2 sec
         *
         * 3rd retry
         * wait 2 sec
         *
         * If still failing:
         * send message to DLT
         */
        FixedBackOff backOff =
                new FixedBackOff(
                        2000L,
                        3
                );
        /*
         * DefaultErrorHandler
         *
         * Handles Kafka consumer exceptions.
         *
         * Responsibilities:
         * - retry failed messages
         * - apply backoff policy
         * - move permanently failed messages
         *   to Dead Letter Topic
         */
        return new DefaultErrorHandler(
                recoverer,
                backOff
        );
    }
}
