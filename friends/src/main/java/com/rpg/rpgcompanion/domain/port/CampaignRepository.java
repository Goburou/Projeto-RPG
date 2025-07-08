package com.rpg.rpgcompanion.domain.port;

import com.rpg.rpgcompanion.domain.model.Campaign;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


// Porta para operações de persistência de Campaign.
@Repository
public interface CampaignRepository extends JpaRepository<Campaign, String> {
}