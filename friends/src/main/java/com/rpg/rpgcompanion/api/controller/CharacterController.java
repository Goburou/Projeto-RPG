package com.rpg.rpgcompanion.api.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.rpg.rpgcompanion.application.service.CharacterService;
import com.rpg.rpgcompanion.domain.model.Character;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/campaigns/{campaignId}/characters")
@CrossOrigin(origins = "*")
public class CharacterController {

    private final CharacterService characterService;

    public CharacterController(CharacterService characterService) {
        this.characterService = characterService;
    }

    @PostMapping
    public ResponseEntity<String> createCharacter(@PathVariable String campaignId, @RequestBody Character character) {
        try {
            String id = characterService.saveCharacter(character, campaignId);
            return new ResponseEntity<>(id, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            // RuntimeException pode ser uma mensagem de erro de validação ou permissão
            return new ResponseEntity<>(e.getMessage(), HttpStatus.UNAUTHORIZED);
        } catch (JsonProcessingException e) {
            // Erro de processamento de JSON (ex: formato inválido para attributesJson)
            return new ResponseEntity<>("Invalid JSON data for character fields: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) { // Captura exceções genéricas
            e.printStackTrace(); // Logar o erro completo no console para depuração
            return new ResponseEntity<>("Internal server error: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{characterId}")
    public ResponseEntity<String> updateCharacter(@PathVariable String campaignId, @PathVariable String characterId, @RequestBody Character character) {
        try {
            character.setId(characterId); // Define o ID no DTO para que o serviço saiba qual atualizar
            characterService.saveCharacter(character, campaignId);
            return new ResponseEntity<>("Character updated successfully.", HttpStatus.OK);
        } catch (RuntimeException e) {
            // Tipo de ResponseEntity para String, pois retorna uma mensagem
            return new ResponseEntity<>(e.getMessage(), HttpStatus.FORBIDDEN);
        } catch (JsonProcessingException e) {
            return new ResponseEntity<>("Invalid JSON data for character fields: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("Internal server error: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{characterId}")
    public ResponseEntity<?> getCharacterById(@PathVariable String campaignId, @PathVariable String characterId) {
        try {
            Optional<Character> character = characterService.getCharacterById(characterId, campaignId);
            return character.map(value -> new ResponseEntity<Character>(value, HttpStatus.OK)) // Retorna Character
                    .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
        } catch (RuntimeException e) {
            // Tipo de ResponseEntity para String
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<String>("Internal server error: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR); // Retorna String
        }
    }

    @GetMapping
    public ResponseEntity<?> getCharactersByCampaign(@PathVariable String campaignId) {
        try {
            List<Character> characters = characterService.getCharactersByCampaign(campaignId);
            return new ResponseEntity<List<Character>>(characters, HttpStatus.OK); // Retorna List<Character>
        }
        // Os catch blocks já retornam ResponseEntity<String>, então o tipo genérico precisa ser flexível.
        // Ou você pode ter métodos separados para tratamento de erro que retornam ResponseEntity<String>.
        catch (RuntimeException e) {
            // Tipo de ResponseEntity para String
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<String>("Internal server error: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR); // Retorna String
        }
    }

    @DeleteMapping("/{characterId}")
    public ResponseEntity<String> deleteCharacter(@PathVariable String campaignId, @PathVariable String characterId) {
        try {
            characterService.deleteCharacter(characterId, campaignId);
            return new ResponseEntity<>("Character deleted successfully.", HttpStatus.NO_CONTENT);
        } catch (RuntimeException e) {
            // Tipo de ResponseEntity para String, pois retorna uma mensagem
            return new ResponseEntity<>(e.getMessage(), HttpStatus.FORBIDDEN);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("Internal server error: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}