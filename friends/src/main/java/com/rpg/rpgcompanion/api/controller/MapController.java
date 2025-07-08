package com.rpg.rpgcompanion.api.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.rpg.rpgcompanion.application.service.MapService;
import com.rpg.rpgcompanion.domain.model.GameMap;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/campaigns/{campaignId}/maps")
@CrossOrigin(origins = "*")
public class MapController {

    private final MapService mapService;

    public MapController(MapService mapService) {
        this.mapService = mapService;
    }

    @PostMapping
    public ResponseEntity<String> createMap(@PathVariable String campaignId, @RequestBody GameMap map) {
        try {
            String id = mapService.saveMap(map, campaignId);
            return new ResponseEntity<>(id, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("Internal server error: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{mapId}")
    public ResponseEntity<String> updateMap(@PathVariable String campaignId, @PathVariable String mapId, @RequestBody GameMap map) {
        try {
            map.setId(mapId);
            mapService.saveMap(map, campaignId);
            return new ResponseEntity<>("Map updated successfully.", HttpStatus.OK);
        } catch (RuntimeException e) {
            // Tipo de ResponseEntity para String, pois retorna uma mensagem
            return new ResponseEntity<>(e.getMessage(), HttpStatus.FORBIDDEN);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("Internal server error: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{mapId}")
    public ResponseEntity<?> getMapById(@PathVariable String campaignId, @PathVariable String mapId) {
        try {
            Optional<GameMap> map = mapService.getMapById(mapId, campaignId);
            return map.map(value -> new ResponseEntity<GameMap>(value, HttpStatus.OK)) // Retorna GameMap
                    .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
        }
        // Se houver erro de serialização na saída, será tratado por um HttpMessageNotWritableException padrão.
        catch (RuntimeException e) {
            // Tipo de ResponseEntity para String
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<String>("Internal server error: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR); // Retorna String
        }
    }

    @GetMapping
    public ResponseEntity<?> getMapsByCampaign(@PathVariable String campaignId) {
        try {
            List<GameMap> maps = mapService.getMapsByCampaign(campaignId);
            return new ResponseEntity<List<GameMap>>(maps, HttpStatus.OK); // Retorna List<GameMap>
        } catch (RuntimeException e) {
            // CORRIGIDO: Tipo de ResponseEntity para String
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<String>("Internal server error: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR); // Retorna String
        }
    }

    @DeleteMapping("/{mapId}")
    public ResponseEntity<String> deleteMap(@PathVariable String campaignId, @PathVariable String mapId) {
        try {
            mapService.deleteMap(mapId, campaignId);
            return new ResponseEntity<>("Map deleted successfully.", HttpStatus.NO_CONTENT);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.FORBIDDEN);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("Internal server error: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}