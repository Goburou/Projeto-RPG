package com.rpg.rpgcompanion.application.service;

import com.rpg.rpgcompanion.domain.model.GameMap;
import com.rpg.rpgcompanion.domain.model.Campaign;
import com.rpg.rpgcompanion.domain.port.MapRepository;
import com.rpg.rpgcompanion.domain.port.AuthService;
import com.rpg.rpgcompanion.domain.port.CampaignRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class MapService {

    private final MapRepository mapRepository;
    private final CampaignRepository campaignRepository;
    private final AuthService authService;

    public MapService(MapRepository mapRepository, CampaignRepository campaignRepository, AuthService authService) {
        this.mapRepository = mapRepository;
        this.campaignRepository = campaignRepository;
        this.authService = authService;
    }

    @Transactional
    public String saveMap(GameMap mapDto, String campaignId) {
        String currentUserId = authService.getCurrentUserId().orElseThrow(() -> new RuntimeException("User not authenticated"));
        Campaign campaign = campaignRepository.findById(campaignId)
                .orElseThrow(() -> new RuntimeException("Campaign not found."));

        // Apenas o GM pode salvar/editar mapas
        if (!authService.isUserGm(currentUserId, campaignId)) {
            throw new RuntimeException("You do not have permission to save/edit maps. Only the GM can.");
        }

        GameMap map;
        // Se o DTO não tem ID ou o ID não existe no banco (é um novo mapa)
        if (mapDto.getId() == null || mapDto.getId().isEmpty() || !mapRepository.existsById(mapDto.getId())) {
            // Novo mapa - o construtor já gera o UUID
            map = new GameMap(
                    null, // ID será gerado no construtor GameMap(String id, ...)
                    mapDto.getName(),
                    mapDto.getData(),
                    campaign,
                    currentUserId
            );
        } else {
            // Atualizar mapa existente
            map = mapRepository.findById(mapDto.getId())
                    .orElseThrow(() -> new RuntimeException("Map not found."));

            // Garante que o GM está atualizando um mapa da sua campanha
            if (!Objects.equals(map.getCampaign().getId(), campaignId)) {
                throw new RuntimeException("Map does not belong to the specified campaign.");
            }

            map.setName(mapDto.getName());
            map.setData(mapDto.getData());
            map.setUpdatedAt(Instant.now()); // Atualiza o timestamp
            map.setCampaign(campaign); // Garante que a campanha está correta (não muda a FK diretamente, mas o objeto de Campaign)
        }
        return mapRepository.save(map).getId();
    }

    public Optional<GameMap> getMapById(String mapId, String campaignId) {
        String currentUserId = authService.getCurrentUserId().orElseThrow(() -> new RuntimeException("User not authenticated"));
        Optional<GameMap> map = mapRepository.findById(mapId);
        // Garante que o mapa existe e pertence à campanha
        return map.filter(m -> Objects.equals(m.getCampaign().getId(), campaignId)); // Todos na campanha podem ver.
    }

    public List<GameMap> getMapsByCampaign(String campaignId) {
        // Todos os usuários na campanha podem ver os mapas.
        return mapRepository.findByCampaignId(campaignId);
    }

    @Transactional
    public void deleteMap(String mapId, String campaignId) {
        String currentUserId = authService.getCurrentUserId().orElseThrow(() -> new RuntimeException("User not authenticated"));
        GameMap existingMap = mapRepository.findById(mapId)
                .orElseThrow(() -> new RuntimeException("Map not found."));

        if (!Objects.equals(existingMap.getCampaign().getId(), campaignId)) {
            throw new RuntimeException("Map does not belong to the specified campaign.");
        }

        // Apenas GM pode deletar
        if (!authService.isUserGm(currentUserId, existingMap.getCampaign().getId())) {
            throw new RuntimeException("You do not have permission to delete this map.");
        }
        mapRepository.deleteById(mapId);
    }
}