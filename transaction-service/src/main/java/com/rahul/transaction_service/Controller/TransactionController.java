package com.rahul.transaction_service.Controller;

import com.rahul.transaction_service.Dto.ApiResopnse;
import com.rahul.transaction_service.Dto.TxnRequestDto;
import com.rahul.transaction_service.Dto.TxnStatusDto;
import com.rahul.transaction_service.Service.TxnService;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/txn-service")
@Slf4j
public class TransactionController {

    @Autowired
    private TxnService txnService;

    @PostMapping("/init-txn")
    public ResponseEntity<ApiResopnse<String>> initTransaction(@RequestBody TxnRequestDto txnRequestDto) throws ExecutionException, InterruptedException {
        log.info("Initiating transaction fromUser={} toUser={} amount={} ",txnRequestDto.getFromUserId() , txnRequestDto.getToUserId() , txnRequestDto.getAmount());
        String txnId = txnService.initTransaction(txnRequestDto);
        ApiResopnse<String> resopnse = ApiResopnse.<String>builder()
                .success(true)
                .message("Transaction initiated successfully")
                .data(txnId)
                .timeStamo(LocalDateTime.now())
                .build();
        return ResponseEntity.accepted().body(resopnse);
    }

    @GetMapping("/check-txn/{txnId}")
    public ResponseEntity<ApiResopnse<TxnStatusDto>> checkStatus(@PathVariable String txnId){
        log.info("Checking transaction status txnId = {}" , txnId);
        TxnStatusDto txnStatusDto = txnService.checkStatus(txnId);
        ApiResopnse<TxnStatusDto> response = ApiResopnse.<TxnStatusDto>builder()
                .success(true)
                .message("Transaction status fetched successfully")
                .data(txnStatusDto)
                .timeStamo(LocalDateTime.now())
                .build();
        return ResponseEntity.ok(response);
    }
}
