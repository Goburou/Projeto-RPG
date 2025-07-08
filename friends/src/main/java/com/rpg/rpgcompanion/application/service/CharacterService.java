package com.rpg.rpgcompanion.application.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rpg.rpgcompanion.domain.model.Character;
import com.rpg.rpgcompanion.domain.model.Campaign;
import com.rpg.rpgcompanion.domain.port.CharacterRepository;
import com.rpg.rpgcompanion.domain.port.AuthService;
import com.rpg.rpgcompanion.domain.port.CampaignRepository; // Necessário para buscar a campanha
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
// import java.util.Map; // Removido import de Map desnecessário
import java.util.Objects;
import java.util.Optional;

@Service
public class CharacterService {

    private final CharacterRepository characterRepository;
    private final CampaignRepository campaignRepository; // Injetar CampaignRepository
    private final AuthService authService;
    private final ObjectMapper objectMapper; // Para converter Map<String, Object> para JSON string e vice-versa

    public CharacterService(CharacterRepository characterRepository, CampaignRepository campaignRepository, AuthService authService, ObjectMapper objectMapper) {
        this.characterRepository = characterRepository;
        this.campaignRepository = campaignRepository;
        this.authService = authService;
        this.objectMapper = objectMapper;
    }

    @Transactional
    public String saveCharacter(Character characterDto, String campaignId) throws JsonProcessingException {
        String currentUserId = authService.getCurrentUserId().orElseThrow(() -> new RuntimeException("User not authenticated"));
        Campaign campaign = campaignRepository.findById(campaignId)
                .orElseThrow(() -> new RuntimeException("Campaign not found."));

        Character character;

        // Se o DTO não tem ID ou o ID não existe no banco (é um novo personagem)
        if (characterDto.getId() == null || characterDto.getId().isEmpty() || !characterRepository.existsById(characterDto.getId())) {
            // Novo personagem - o construtor já gera o UUID
            character = new Character(
                    null, // ID será gerado no construtor Character(String id, ...)
                    characterDto.getName(),
                    characterDto.getSystem(),
                    characterDto.getAttributesJson(),
                    characterDto.getHp(),
                    characterDto.getAc(),
                    characterDto.getNotes(),
                    characterDto.getCustomFieldsJson(),
                    campaign,
                    currentUserId // Define o proprietário
            );
        } else {
            // Atualizar personagem existente
            character = characterRepository.findById(characterDto.getId())
                    .orElseThrow(() -> new RuntimeException("Character not found."));

            // Verificar permissão: Somente o owner ou o GM da campanha pode editar
            if (!Objects.equals(character.getOwnerId(), currentUserId) && !authService.isUserGm(currentUserId, campaignId)) {
                throw new RuntimeException("You do not have permission to edit this character.");
            }

            // Atualiza os campos do personagem existente
            character.setName(characterDto.getName());
            character.setSystem(characterDto.getSystem());
            character.setAttributesJson(characterDto.getAttributesJson());
            character.setHp(characterDto.getHp());
            character.setAc(characterDto.getAc());
            character.setNotes(characterDto.getNotes());
            character.setCustomFieldsJson(characterDto.getCustomFieldsJson());
            character.setCampaign(campaign); // Garante que a campanha está correta (não muda a FK diretamente, mas o objeto de Campaign)
        }
        return characterRepository.save(character).getId();
    }

    public Optional<Character> getCharacterById(String characterId, String campaignId) {
        String currentUserId = authService.getCurrentUserId().orElseThrow(() -> new RuntimeException("User not authenticated"));
        Optional<Character> characterOpt = characterRepository.findById(characterId);

        // Verifica se o personagem existe, pertence à campanha e se o usuário tem permissão para vê-lo
        return characterOpt.filter(c -> Objects.equals(c.getCampaign().getId(), campaignId) &&
                (Objects.equals(c.getOwnerId(), currentUserId) || authService.isUserGm(currentUserId, campaignId)));
    }

    public List<Character> getCharactersByCampaign(String campaignId) {
        String currentUserId = authService.getCurrentUserId().orElseThrow(() -> new RuntimeException("User not authenticated"));
        Campaign campaign = campaignRepository.findById(campaignId)
                .orElseThrow(() -> new RuntimeException("Campaign not found.")); // Garante que a campanha existe

        List<Character> allCharactersInCampaign = characterRepository.findByCampaignId(campaignId);

        // Se o usuário é GM, retorna todos os personagens da campanha.
        // Se for jogador, retorna apenas os personagens que ele possui.
        if (authService.isUserGm(currentUserId, campaignId)) {
            return allCharactersInCampaign;
        } else {
            return allCharactersInCampaign.stream()
                    .filter(c -> Objects.equals(c.getOwnerId(), currentUserId))
                    .toList();
        }
    }

    @Transactional
    public void deleteCharacter(String characterId, String campaignId) {
        String currentUserId = authService.getCurrentUserId().orElseThrow(() -> new RuntimeException("User not authenticated"));
        Character existingChar = characterRepository.findById(characterId)
                .orElseThrow(() -> new RuntimeException("Character not found."));

        if (!Objects.equals(existingChar.getCampaign().getId(), campaignId)) {
            throw new RuntimeException("Character does not belong to the specified campaign.");
        }

        // Apenas GM pode deletar qualquer personagem da campanha
        if (!authService.isUserGm(currentUserId, existingChar.getCampaign().getId())) {
            throw new RuntimeException("You do not have permission to delete this character.");
        }
        characterRepository.deleteById(characterId);
    }
}