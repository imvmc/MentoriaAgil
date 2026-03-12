package com.mentoria.agil.backend.service;

import com.mentoria.agil.backend.interfaces.service.MaterialServiceInterface;
import com.mentoria.agil.backend.dto.MaterialRequestDTO;
import com.mentoria.agil.backend.model.Material;
import com.mentoria.agil.backend.model.User;
import com.mentoria.agil.backend.repository.MaterialRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class MaterialService implements MaterialServiceInterface{
    
    private final MaterialRepository materialRepository;

    public MaterialService (MaterialRepository materialRepository){
        this.materialRepository = materialRepository;
    }

    @Transactional
    public Material criarMaterial(User mentor, MaterialRequestDTO dto){
        Material material = new Material();
        material.setTitulo(dto.getTitulo());
        material.setDescricao(dto.getDescricao());
        material.setConteudo(dto.getConteudo());
        material.setTipo(dto.getTipo());
        material.setMentor(mentor);

        return materialRepository.save(material);
    }
}
