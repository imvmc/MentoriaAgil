package com.mentoria.agil.backend.repository;

import com.mentoria.agil.backend.model.*;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface NotificacaoRepository extends JpaRepository<Notificacao, Long> {

    boolean existsBySessaoAndDestinatarioAndTipoAndCanal(
            Sessao sessao,
            User destinatario,
            TipoNotificacao tipo,
            CanalNotificacao canal
    );

    List<Notificacao> findByStatusAndTipoAndDataAgendadaLessThanEqual(
            StatusNotificacao status,
            TipoNotificacao tipo,
            LocalDateTime dataHora
    );
}