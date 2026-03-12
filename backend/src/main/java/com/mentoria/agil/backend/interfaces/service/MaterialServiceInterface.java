package com.mentoria.agil.backend.interfaces.service;

import com.mentoria.agil.backend.dto.MaterialRequestDTO;
import com.mentoria.agil.backend.model.Material;
import com.mentoria.agil.backend.model.User;

public interface MaterialServiceInterface {
    public Material criarMaterial(User mentor, MaterialRequestDTO dto);
}
