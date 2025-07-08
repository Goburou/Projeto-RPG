package com.rpg.rpgcompanion.application.service;

import com.rpg.rpgcompanion.domain.model.Campaign;
import com.rpg.rpgcompanion.domain.port.CampaignRepository;
import com.rpg.rpgcompanion.domain.port.AuthService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class CampaignService {

    private final CampaignRepository campaignRepository;
    private final AuthService authService;

    public CampaignService(CampaignRepository campaignRepository, AuthService authService) {
        this.campaignRepository = campaignRepository;
        this.authService = authService;
    }

    @Transactional
    public String createCampaign(String name) {
        String currentUserId = authService.getCurrentUserId().orElseThrow(() -> new RuntimeException("User not authenticated"));
        Campaign newCampaign = new Campaign(null, name, currentUserId); // ID gerado no construtor Campaign(String id, ...)
        return campaignRepository.save(newCampaign).getId();
    }

    public List<Campaign> getAllCampaigns() {
        return campaignRepository.findAll();
    }

    public Optional<Campaign> getCampaignById(String campaignId) {
        return campaignRepository.findById(campaignId);
    }
}