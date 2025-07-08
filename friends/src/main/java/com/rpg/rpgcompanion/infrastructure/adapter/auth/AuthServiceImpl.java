package com.rpg.rpgcompanion.infrastructure.adapter.auth;


import com.rpg.rpgcompanion.domain.port.AuthService;
import com.rpg.rpgcompanion.domain.port.CampaignRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Optional;

@Service
public class AuthServiceImpl implements AuthService {

    private final CampaignRepository campaignRepository;

    public AuthServiceImpl(CampaignRepository campaignRepository) {
        this.campaignRepository = campaignRepository;
    }

    @Override
    public Optional<String> getCurrentUserId() {
        HttpServletRequest request = null;
        try {
            request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        } catch (IllegalStateException e) {
            // Pode ocorrer se não estiver em um contexto de requisição HTTP (ex: em testes)
            System.err.println("Not in an HTTP request context: " + e.getMessage());
            return Optional.of("test_system_user"); // Fallback para usuários de sistema/testes
        }

        String userId = request.getHeader("X-User-ID"); // Exemplo: um cabeçalho personalizado do frontend
        if (userId != null && !userId.isEmpty()) {
            return Optional.of(userId);
        }

        // Para demonstração, retorna um ID fixo se nenhum cabeçalho for encontrado.
        // Em produção, isso seria uma FALHA DE SEGURANÇA.
        return Optional.of("dummy_authenticated_user_id");
        // --- FIM DA SIMPLIFICAÇÃO ---
    }

    @Override
    public boolean isUserGm(String userId, String campaignId) {
        try {
            return campaignRepository.findById(campaignId)
                    .map(campaign -> campaign.getGmId().equals(userId))
                    .orElse(false);
        } catch (Exception e) {
            System.err.println("Error checking if user is GM: " + e.getMessage());
            return false;
        }
    }
}