package com.rpg.rpgcompanion.domain.port;

import com.rpg.rpgcompanion.domain.model.GameMap;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MapRepository extends JpaRepository<GameMap, String> {
    List<GameMap> findByCampaignId(String campaignId);
    Optional<GameMap> findByIdAndOwnerId(String mapId, String ownerId);
}