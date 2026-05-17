package com.rahul.notification_service.Config;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rahul.notification_service.Dto.UserCreatedPayload;
import com.rahul.notification_service.Dto.WalletUpdatePayload;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

@Configuration
public class NotificationKafkaConsumerConfig {

    private static Logger LOGGER = LoggerFactory.getLogger(NotificationKafkaConsumerConfig.class);

    private static ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Autowired
    private JavaMailSender javaMailSender;

    @Value("${email.config.username}")
    private String mailSender ;

    @KafkaListener(topics = "${user.created.topic}", groupId = "email")
    public void consumeUserCreateTopic(ConsumerRecord<String, String> payload) throws JsonProcessingException {

        UserCreatedPayload userCreatedPayload = OBJECT_MAPPER.readValue(payload.value(), UserCreatedPayload.class);

        MDC.put("requestId", userCreatedPayload.getRequestId());
        LOGGER.info("Read from kafka 1 : {}", userCreatedPayload);

        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setFrom(mailSender);
        simpleMailMessage.setSubject("Welcome "+userCreatedPayload.getUserName()+" to Payment Wallet Service! \uD83C\uDF89\n" );
        simpleMailMessage.setText("Hi "+userCreatedPayload.getUserName() + "\n" +
                "Your wallet account has been successfully created.\n" +
                "You can now securely add money, make payments, and track your transactions with ease.\n" +
                "\n" +
                "\uD83D\uDCB3 Safe. Fast. Reliable.\n" +
                "\n" +
                "We’re glad to have you on board! \uD83D\uDE80");

        simpleMailMessage.setCc(mailSender);
        simpleMailMessage.setTo(userCreatedPayload.getUserEmail());
        javaMailSender.send(simpleMailMessage);
        LOGGER.info("Welcome email send to  : {}", userCreatedPayload.getUserEmail());
        MDC.clear();;
    }

    @KafkaListener(topics = "${wallet.updated.topic}",groupId = "walletUpdate")
    public void consumeWalletUpdateTopic(ConsumerRecord<String, String> payload) throws JsonProcessingException {
        WalletUpdatePayload walletUpdatePayload = OBJECT_MAPPER.readValue(payload.value(), WalletUpdatePayload.class);

        MDC.put("requestId", walletUpdatePayload.getRequestId());
        LOGGER.info("Read from kafka : {}", walletUpdatePayload);

        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setFrom(mailSender);
        simpleMailMessage.setSubject("Balance updated to Wallet" );
        simpleMailMessage.setText(
                "Dear USER,\n" +
                "\n" +
                "Your wallet has been updated successfully.\n" +
                "\uD83D\uDCCC Wallet Details:\n" +
                "- Current Balance: ₹ "+walletUpdatePayload.getBalance()+"\n" +
                "- Request ID: "+walletUpdatePayload.getRequestId()+"\n" +
                "\n" +
                "⚠\uFE0F If you did not authorize this activity, please contact Payment Wallet Support immediately.\n" +
                "\n" +
                "Thank you for using our Wallet Service.\n" +
                "\n" +
                "Best regards,  \n" +
                "PAYMENT-WALLET-SERVICE Team\n");

        simpleMailMessage.setCc(mailSender);
        simpleMailMessage.setTo(walletUpdatePayload.getUserEmail());
        javaMailSender.send(simpleMailMessage);
        LOGGER.info("Balance update  email send to  : {}", walletUpdatePayload.getUserEmail());
        MDC.clear();;
    }
}
