package com.rpg.rpgcompanion.domain.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonCreator; // Para deserialização JSON
import com.fasterxml.jackson.annotation.JsonProperty; // Para mapear propriedades JSON

import java.util.UUID;

@Entity
@Table(name = "characters")
@Data
@NoArgsConstructor
public class Character {
    @Id
    private String id; // Usaremos String para manter compatibilidade com IDs do Firebase/UUIDs
    private String name;
    private String system;

    // Mapas em JPA geralmente são salvos como JSON.
    // Usaremos String para armazenar o JSON e faremos o parse/stringify manualmente na camada de serviço ou via um conversor JPA.
    @Column(columnDefinition = "jsonb") // Para PostgreSQL, armazena como JSON binário
    private String attributesJson; // Ex: {"strength": 10, "dexterity": 12}

    private int hp;
    private int ac;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Column(columnDefinition = "jsonb")
    private String customFieldsJson; // Ex: {"magias": "Bola de Fogo"}

    // Relacionamento com Campaign
    @ManyToOne(fetch = FetchType.LAZY) // Lazy loading para evitar carregar a campanha inteira toda vez
    @JoinColumn(name = "campaign_id") // Nome da coluna FK na tabela characters
    private Campaign campaign;

    private String ownerId; // O ID do usuário que é o proprietário (criador) da ficha

    // Construtor personalizado para criação de novas entidades ou deserialização quando o ID é opcional.
    // @JsonCreator e @JsonProperty ajudam Jackson a usar este construtor para deserialização de JSON.
    @JsonCreator
    public Character(
            @JsonProperty("id") String id, // @JsonProperty para mapear o campo 'id' do JSON
            @JsonProperty("name") String name,
            @JsonProperty("system") String system,
            @JsonProperty("attributesJson") String attributesJson,
            @JsonProperty("hp") int hp,
            @JsonProperty("ac") int ac,
            @JsonProperty("notes") String notes,
            @JsonProperty("customFieldsJson") String customFieldsJson,
            @JsonProperty("campaign") Campaign campaign, // Note que o campo é do tipo Campaign, não String para o ID
            @JsonProperty("ownerId") String ownerId) {

        // Se o ID não for fornecido no JSON ou for vazio, gera um novo UUID.
        // Isso é útil para requisições POST onde o cliente não envia um ID.
        this.id = (id == null || id.isEmpty()) ? UUID.randomUUID().toString() : id;
        this.name = name;
        this.system = system;
        this.attributesJson = attributesJson;
        this.hp = hp;
        this.ac = ac;
        this.notes = notes;
        this.customFieldsJson = customFieldsJson;
        this.campaign = campaign;
        this.ownerId = ownerId;
    }
}