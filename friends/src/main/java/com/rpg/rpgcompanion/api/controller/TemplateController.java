package com.rpg.rpgcompanion.api.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rpg.rpgcompanion.application.service.TemplateService;
import com.rpg.rpgcompanion.domain.model.CharacterTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/templates")
@CrossOrigin(origins = "*")
public class TemplateController {

    private final TemplateService templateService;

    public TemplateController(TemplateService templateService) {
        this.templateService = templateService;
    }

    @PostMapping
    public ResponseEntity<String> createTemplate(@RequestBody CharacterTemplate template) {
        try {
            String id = templateService.saveTemplate(template);
            return new ResponseEntity<>(id, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.UNAUTHORIZED);
        } catch (JsonProcessingException e) {
            return new ResponseEntity<>("Invalid JSON data for template: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("Internal server error: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{templateId}")
    public ResponseEntity<String> updateTemplate(@PathVariable String templateId, @RequestBody CharacterTemplate template) {
        try {
            template.setId(templateId);
            templateService.saveTemplate(template);
            return new ResponseEntity<>("Template updated successfully.", HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.UNAUTHORIZED);
        } catch (JsonProcessingException e) {
            return new ResponseEntity<>("Invalid JSON data for template: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("Internal server error: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping
    public ResponseEntity<List<CharacterTemplate>> getAllTemplates() {
        try {
            List<CharacterTemplate> templates = templateService.getAllTemplates();
            return new ResponseEntity<>(templates, HttpStatus.OK);
        }
        // Se houver erro de serialização na saída, será tratado por um HttpMessageNotWritableException padrão.
        catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{templateId}")
    public ResponseEntity<String> deleteTemplate(@PathVariable String templateId) {
        try {
            templateService.deleteTemplate(templateId);
            return new ResponseEntity<>("Template deleted successfully.", HttpStatus.NO_CONTENT);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.FORBIDDEN);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("Internal server error: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}