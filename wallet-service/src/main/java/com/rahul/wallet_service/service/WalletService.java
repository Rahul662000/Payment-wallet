package com.rahul.wallet_service.service;

import com.rahul.wallet_service.Dtos.InitTxnPayload;
import com.rahul.wallet_service.Dtos.TxnCompletedPayload;
import com.rahul.wallet_service.Dtos.WalletUpdatePayload;
import com.rahul.wallet_service.entity.Wallet;
import com.rahul.wallet_service.repo.WalletRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.Future;

@Service
public class WalletService {
    private static Logger LOGGER = LoggerFactory.getLogger(WalletService.class);

    @Value("${txn.completed.topic}")
    private String txnCompletedTopic;//topic to send to notification to the user
    @Value("${wallet.updated.topic}") //topic to send the updated txn status to txn-service
    private String walletUpdatedTopic;

    @Autowired
    private WalletRepo walletRepo;
    @Autowired
    private KafkaTemplate kafkaTemplate;

    @Transactional
    public void walletTxn(InitTxnPayload payload) {
        Wallet fromWallet = walletRepo.findByUserId(payload.getFormUserId());
        TxnCompletedPayload completedPayload = new TxnCompletedPayload();
        completedPayload.setId(payload.getId());
        completedPayload.setRequestId(payload.getRequestId());
        if (fromWallet.getBalance() < payload.getAmount()) {
            completedPayload.setSuccess(false);
            completedPayload.setReason("LOW BALANCE");
        } else {
            Wallet toWallet = walletRepo.findByUserId(payload.getToUserId());
            fromWallet.setBalance(fromWallet.getBalance() - payload.getAmount());
            toWallet.setBalance(toWallet.getBalance() + payload.getAmount());

            completedPayload.setSuccess(true);
            completedPayload.setReason("SUFFICIENT FUND");


            WalletUpdatePayload walletpayload1 = new WalletUpdatePayload(
                    fromWallet.getUserEmail(),
                    fromWallet.getBalance(),
                    payload.getRequestId()
            );

            WalletUpdatePayload walletpayload2 = new WalletUpdatePayload(
                    toWallet.getUserEmail(),
                    toWallet.getBalance(),
                    payload.getRequestId()
            );


            Future<SendResult<String, Object>> walletUpdateFuture1 = kafkaTemplate.send(
                    walletUpdatedTopic,
                    walletpayload1.getUserEmail(),
                    walletpayload1
            );

            LOGGER.info("pushed walletUpdate to kafka 1 {}", walletpayload1);
            Future<SendResult<String, Object>> walletUpdateFuture2 = kafkaTemplate.send(
                    walletUpdatedTopic,
                    walletpayload2.getUserEmail(),
                    walletpayload2
            );

            LOGGER.info("pushed walletUpdate to kafka 2 {}", walletpayload2);
            Future<SendResult<String, Object>> future = kafkaTemplate.send(
                    txnCompletedTopic,
                    payload.getFormUserId().toString(),
                    completedPayload

            );
            LOGGER.info("pushed txnCompleted payload  to kafka {}", completedPayload);

        }
        LOGGER.info("txn done {}", completedPayload);
    }
}
