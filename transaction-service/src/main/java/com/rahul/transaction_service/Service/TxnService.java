package com.rahul.transaction_service.Service;

import com.rahul.transaction_service.Dto.InitTxnPayload;
import com.rahul.transaction_service.Dto.TxnRequestDto;
import com.rahul.transaction_service.Dto.TxnStatusDto;
import com.rahul.transaction_service.Entity.Transaction;
import com.rahul.transaction_service.Entity.TransactionStatus;
import com.rahul.transaction_service.Repo.TxnRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

@Service
public class TxnService {

    private static Logger LOGGER = LoggerFactory.getLogger(TxnService.class);

    @Autowired
    private TxnRepo txnRepo;

    @Autowired
    private KafkaTemplate<String,Object> kafkaTemplate;

    @Value("${txn.init.topic}")
    private String txnInitTopic;


    @Transactional
    public String initTransaction(TxnRequestDto txnDto) throws ExecutionException, InterruptedException {

        //saving the transaction in db
        Transaction transaction = Transaction.builder()
                .txnId(UUID.randomUUID().toString())
                .fromUserId(txnDto.getFromUserId())
                .toUserId(txnDto.getToUserId())
                .amount(txnDto.getAmount())
                .comment(txnDto.getComment())
                .status(TransactionStatus.PENDING)
                .build();
        txnRepo.save(transaction);

        LOGGER.info("transaction initialized Id : {}",transaction.getTxnId());

        // pushing to kafka

        InitTxnPayload payload = InitTxnPayload.builder()
                .id(transaction.getId())
                .formUserId(txnDto.getFromUserId())
                .toUserId(txnDto.getToUserId())
                .amount(transaction.getAmount())
                .requestId(MDC.get("requestId"))
                .build();
        Future<SendResult<String,Object>> future  = kafkaTemplate.send(txnInitTopic,transaction.getFromUserId().toString(),payload);
        LOGGER.info("Pushed txnInitPayload to kafka: {}",future.get());


        return transaction.getTxnId();
    }

    public TxnStatusDto checkStatus(String txnId){
        Transaction transaction = txnRepo.findByTxnId(txnId);
        TxnStatusDto txnStatusDto = new TxnStatusDto();
        if (transaction != null){
            txnStatusDto.setStatus(transaction.getStatus().toString());
            txnStatusDto.setReason(transaction.getReason());
        }
        return txnStatusDto;

    }



}
