package com.rpg.rpgcompanion.domain.port;

import com.rpg.rpgcompanion.domain.model.Character;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CharacterRepository extends JpaRepository<Character, String> {
    List<Character> findByCampaignId(String campaignId); // Busca por campaign_id
    Optional<Character> findByIdAndOwnerId(String characterId, String ownerId); // Para verificar permissões de edição
    List<Character> findByCampaignIdAndOwnerId(String campaignId, String ownerId); // Personagens de um usuário específico em uma campanha
}