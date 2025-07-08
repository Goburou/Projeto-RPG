package com.rpg.rpgcompanion.domain.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
// import lombok.AllArgsConstructor; // Removido para evitar ambiguidade com @JsonCreator
import com.fasterxml.jackson.annotation.JsonCreator; // Para deserialização JSON
import com.fasterxml.jackson.annotation.JsonProperty; // Para mapear propriedades JSON

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "campaigns")
@Data
@NoArgsConstructor
public class Campaign {
    @Id
    private String id; // Usaremos String para manter compatibilidade com IDs do Firebase/UUIDs
    private String name;
    private String gmId; // ID do usuário Mestre
    private Instant createdAt;

    // Relacionamentos bidirecionais (opcional, mas útil para carregar relacionados)
    // Usamos `mappedBy` para indicar que a tabela `characters` tem a FK `campaign_id`
    @OneToMany(mappedBy = "campaign", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Character> characters;

    @OneToMany(mappedBy = "campaign", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<GameMap> maps;

    // Construtor personalizado para criação de novas entidades ou deserialização quando o ID é opcional.
    @JsonCreator
    public Campaign(
            @JsonProperty("id") String id,
            @JsonProperty("name") String name,
            @JsonProperty("gmId") String gmId) {

        this.id = (id == null || id.isEmpty()) ? UUID.randomUUID().toString() : id;
        this.name = name;
        this.gmId = gmId;
        this.createdAt = Instant.now();
    }
}