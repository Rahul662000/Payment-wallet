package com.rahul.transaction_service.Repo;

import com.rahul.transaction_service.Entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TxnRepo extends JpaRepository<Transaction, Long> {

    Transaction findByTxnId(String txnId);
}
