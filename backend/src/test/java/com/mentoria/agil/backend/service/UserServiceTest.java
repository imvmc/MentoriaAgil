package com.mentoria.agil.backend.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.mentoria.agil.backend.dto.response.MentorResponseDTO;
import com.mentoria.agil.backend.model.Role;
import com.mentoria.agil.backend.model.User;
import com.mentoria.agil.backend.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    // 1º Teste: O que nós já tínhamos feito e passou!
    @Test
    public void deveListarMentoresSemFiltros() {
        User mentor = new User();
        mentor.setName("Ricardo Oliveira");
        mentor.setEmail("ricardo@email.com");
        mentor.setPassword("123");
        mentor.setRole(Role.MENTOR);

        when(userRepository.findMentores(Role.MENTOR, null, null, null))
            .thenReturn(Arrays.asList(mentor));

        List<MentorResponseDTO> resultado = userService.listarMentores(null, null, null);

        assertEquals(1, resultado.size());
    }

    // 2º Teste: Testando se o filtro de Especialidade funciona
    @Test
    public void deveListarMentoresComFiltroDeEspecialidade() {
        User mentor = new User();
        mentor.setName("Ana Souza");
        mentor.setEmail("ana@email.com");
        mentor.setPassword("123");
        mentor.setRole(Role.MENTOR);

        when(userRepository.findMentores(Role.MENTOR, "Java", null, null))
            .thenReturn(Arrays.asList(mentor));

        List<MentorResponseDTO> resultado = userService.listarMentores("Java", null, null);

        assertEquals(1, resultado.size());
    }

    // 3º Teste: Testando quando o filtro não encontra ninguém
    @Test
    public void deveRetornarListaVaziaQuandoNenhumMentorEncontrado() {
        
        when(userRepository.findMentores(Role.MENTOR, "Python", null, null))
            .thenReturn(Collections.emptyList());

        List<MentorResponseDTO> resultado = userService.listarMentores("Python", null, null);

        assertTrue(resultado.isEmpty());
    }
}