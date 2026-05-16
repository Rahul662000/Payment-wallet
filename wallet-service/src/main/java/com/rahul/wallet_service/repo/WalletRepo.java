package com.rahul.wallet_service.repo;

import com.rahul.wallet_service.entity.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WalletRepo extends JpaRepository<Wallet , Long> {
    Wallet findByUserId(Long userId);
}
