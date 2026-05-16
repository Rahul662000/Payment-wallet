package com.rahul.user_service.Service;

import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class TokenBlackListService {

    private final Set<String> blacklistedTokens = ConcurrentHashMap.newKeySet();

    public void blackListToken(String token){
        blacklistedTokens.add(token);
    }

    public boolean isTokenBlackListed(String token){
        return blacklistedTokens.contains(token);
    }

}
