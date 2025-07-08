package com.rpg.rpgcompanion.domain.port;

import com.rpg.rpgcompanion.domain.model.CharacterTemplate; // CORRIGIDO: Usar CharacterTemplate
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TemplateRepository extends JpaRepository<CharacterTemplate, String> {
}