package com.rpg.rpgcompanion.domain.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
// import lombok.AllArgsConstructor; // Removido para evitar ambiguidade com @JsonCreator
import com.fasterxml.jackson.annotation.JsonCreator; // Para deserialização JSON
import com.fasterxml.jackson.annotation.JsonProperty; // Para mapear propriedades JSON

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "game_maps")
@Data
@NoArgsConstructor
public class GameMap {
    @Id
    private String id; // Usaremos String para manter compatibilidade com IDs do Firebase/UUIDs
    private String name;

    @Column(columnDefinition = "TEXT") // Pode ser um JSON grande, use TEXT ou JSONB
    private String data; // JSON string of drawing commands

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "campaign_id")
    private Campaign campaign;

    private String ownerId; // O ID do usuário que é o Mestre (criador) do mapa
    private Instant updatedAt;

    // Construtor personalizado para criação de novas entidades ou deserialização quando o ID é opcional.
    @JsonCreator
    public GameMap(
            @JsonProperty("id") String id,
            @JsonProperty("name") String name,
            @JsonProperty("data") String data,
            @JsonProperty("campaign") Campaign campaign,
            @JsonProperty("ownerId") String ownerId) {

        this.id = (id == null || id.isEmpty()) ? UUID.randomUUID().toString() : id;
        this.name = name;
        this.data = data;
        this.campaign = campaign;
        this.ownerId = ownerId;
        this.updatedAt = Instant.now(); // Define o timestamp de criação/atualização
    }
}