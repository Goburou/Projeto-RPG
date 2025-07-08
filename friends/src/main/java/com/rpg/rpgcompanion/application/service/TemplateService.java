package com.rpg.rpgcompanion.application.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rpg.rpgcompanion.domain.model.CharacterTemplate;
import com.rpg.rpgcompanion.domain.port.TemplateRepository;
import com.rpg.rpgcompanion.domain.port.AuthService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
public class TemplateService {

    private final TemplateRepository templateRepository;
    private final AuthService authService;
    private final ObjectMapper objectMapper; // Para converter Map<String, Object> para JSON string e vice-versa

    public TemplateService(TemplateRepository templateRepository, AuthService authService, ObjectMapper objectMapper) {
        this.templateRepository = templateRepository;
        this.authService = authService;
        this.objectMapper = objectMapper;
    }

    @Transactional
    public String saveTemplate(CharacterTemplate templateDto) throws JsonProcessingException {
        String currentUserId = authService.getCurrentUserId().orElseThrow(() -> new RuntimeException("User not authenticated"));

        CharacterTemplate template;

        // Se o DTO não tem ID ou o ID não existe no banco (é um novo template)
        if (templateDto.getId() == null || templateDto.getId().isEmpty() || !templateRepository.existsById(templateDto.getId())) {
            // Novo template - o construtor já gera o UUID
            template = new CharacterTemplate(
                    null, // ID será gerado no construtor CharacterTemplate(String id, ...)
                    templateDto.getName(),
                    templateDto.getDataJson(),
                    currentUserId
            );
        } else {
            // Atualizar template existente
            template = templateRepository.findById(templateDto.getId())
                    .orElseThrow(() -> new RuntimeException("Template not found."));

            // Apenas o criador pode editar (simplificado)
            if (!template.getCreatedBy().equals(currentUserId)) {
                throw new RuntimeException("You do not have permission to edit this template.");
            }

            template.setName(templateDto.getName());
            template.setDataJson(templateDto.getDataJson());
            template.setCreatedAt(Instant.now()); // Atualiza timestamp de modificação
        }

        return templateRepository.save(template).getId();
    }

    public List<CharacterTemplate> getAllTemplates() {
        return templateRepository.findAll();
    }

    @Transactional
    public void deleteTemplate(String templateId) {
        String currentUserId = authService.getCurrentUserId().orElseThrow(() -> new RuntimeException("User not authenticated"));
        CharacterTemplate existingTemplate = templateRepository.findById(templateId)
                .orElseThrow(() -> new RuntimeException("Template not found."));

        // Apenas o criador pode deletar (simplificado)
        if (!existingTemplate.getCreatedBy().equals(currentUserId)) {
            throw new RuntimeException("You do not have permission to delete this template.");
        }

        templateRepository.deleteById(templateId);
    }
}