package com.rpg.rpgcompanion.domain.port;

import java.util.Optional;

// Porta para serviços de autenticação.
public interface AuthService {
    Optional<String> getCurrentUserId();
    boolean isUserGm(String userId, String campaignId);
}