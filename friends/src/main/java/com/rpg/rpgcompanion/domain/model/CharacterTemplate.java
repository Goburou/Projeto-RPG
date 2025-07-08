package com.rpg.rpgcompanion.domain.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonCreator; // Para deserialização JSON
import com.fasterxml.jackson.annotation.JsonProperty; // Para mapear propriedades JSON

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "character_templates")
@Data
@NoArgsConstructor
public class CharacterTemplate {
    @Id
    private String id; // Usaremos String para manter compatibilidade com IDs do Firebase/UUIDs
    private String name;

    @Column(columnDefinition = "jsonb") // JSON string of template structure
    private String dataJson;

    private String createdBy;
    private Instant createdAt;

    // Construtor personalizado para criação de novas entidades ou deserialização quando o ID é opcional.
    @JsonCreator
    public CharacterTemplate(
            @JsonProperty("id") String id,
            @JsonProperty("name") String name,
            @JsonProperty("dataJson") String dataJson,
            @JsonProperty("createdBy") String createdBy) {

        this.id = (id == null || id.isEmpty()) ? UUID.randomUUID().toString() : id;
        this.name = name;
        this.dataJson = dataJson;
        this.createdBy = createdBy;
        this.createdAt = Instant.now();
    }
}