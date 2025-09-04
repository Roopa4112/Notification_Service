//package com.example.notificationservice.config;
//
//import com.example.notificationservice.dto.UserRegisteredEvent;
//import org.apache.kafka.clients.consumer.ConsumerConfig;
//import org.apache.kafka.common.serialization.StringDeserializer;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.kafka.annotation.EnableKafka;
//import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
//import org.springframework.kafka.core.ConsumerFactory;
//import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
//import org.springframework.kafka.support.serializer.JsonDeserializer;
//
//import java.util.HashMap;
//import java.util.Map;
//
//@Configuration
//@EnableKafka
//public class KafkaConsumerConfig {
//
//    @Bean
//    public ConsumerFactory<String, UserRegisteredEvent> userRegisteredConsumerFactory() {
//        JsonDeserializer<UserRegisteredEvent> deserializer = new JsonDeserializer<>(UserRegisteredEvent.class);
//        deserializer.addTrustedPackages("*"); // ✅ trust all packages
//        deserializer.setRemoveTypeHeaders(false);
//
//        Map<String, Object> props = new HashMap<>();
//        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
//        props.put(ConsumerConfig.GROUP_ID_CONFIG, "notification-service");
//        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
//        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class); // ✅ fixed
//
//        return new DefaultKafkaConsumerFactory<>(props, new StringDeserializer(), deserializer);
//    }
//
//    @Bean
//    public ConcurrentKafkaListenerContainerFactory<String, UserRegisteredEvent> userRegisteredKafkaListenerFactory() {
//        ConcurrentKafkaListenerContainerFactory<String, UserRegisteredEvent> factory =
//                new ConcurrentKafkaListenerContainerFactory<>();
//        factory.setConsumerFactory(userRegisteredConsumerFactory());
//        return factory;
//    }
//}
