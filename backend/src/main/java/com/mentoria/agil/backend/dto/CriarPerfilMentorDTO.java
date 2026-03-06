package com.mentoria.agil.backend.dto;

import jakarta.validation.constraints.NotBlank;

public class CriarPerfilMentorDTO {

    @NotBlank(message = "Especialização é obrigatória")
    private String especializacao;

    @NotBlank(message = "Experiências são obrigatórias")
    private String experiencias;

    private String formacao;

    public CriarPerfilMentorDTO() {}

    public String getEspecializacao() {
        return especializacao;
    }

    public void setEspecializacao(String especializacao) {
        this.especializacao = especializacao;
    }

    public String getExperiencias() {
        return experiencias;
    }

    public void setExperiencias(String experiencias) {
        this.experiencias = experiencias;
    }

    public String getFormacao() {
        return formacao;
    }

    public void setFormacao(String formacao) {
        this.formacao = formacao;
    }
}