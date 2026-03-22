package com.mentoria.agil.backend.service;

import com.mentoria.agil.backend.model.*;
import com.mentoria.agil.backend.repository.NotificacaoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class NotificacaoService {

    private static final Logger logger = LoggerFactory.getLogger(NotificacaoService.class);

    private final NotificacaoRepository notificacaoRepository;

    public NotificacaoService(NotificacaoRepository notificacaoRepository) {
        this.notificacaoRepository = notificacaoRepository;
    }

    @Transactional
    public void notificarAgendamento(User mentor, User mentorado, Sessao sessao) {
        enviarNotificacaoImediata(mentor, mentorado, sessao);
        agendarLembrete24h(mentor, mentorado, sessao);
    }

    private void enviarNotificacaoImediata(User mentor, User mentorado, Sessao sessao) {
        enviarSeValido(mentor, sessao, TipoNotificacao.AGENDAMENTO, montarMensagemMentor(mentor, mentorado, sessao), LocalDateTime.now());
        enviarSeValido(mentorado, sessao, TipoNotificacao.AGENDAMENTO, montarMensagemMentorado(mentor, mentorado, sessao), LocalDateTime.now());
    }

    private void agendarLembrete24h(User mentor, User mentorado, Sessao sessao) {
        LocalDateTime dataLembrete = sessao.getDataHoraInicio().minusHours(24);

        if (dataLembrete.isBefore(LocalDateTime.now())) {
            return;
        }

        criarPendenteSeValido(mentor, sessao, TipoNotificacao.LEMBRETE_24H, montarLembreteMentor(mentor, mentorado, sessao), dataLembrete);
        criarPendenteSeValido(mentorado, sessao, TipoNotificacao.LEMBRETE_24H, montarLembreteMentorado(mentor, mentorado, sessao), dataLembrete);
    }

    private void enviarSeValido(User destinatario, Sessao sessao, TipoNotificacao tipo, String mensagem, LocalDateTime dataEnvio) {
        if (!possuiEmailValido(destinatario)) {
            logger.warn("Usuário {} sem email válido para notificação {}", destinatario.getId(), tipo);
            return;
        }

        if (jaExisteNotificacao(sessao, destinatario, tipo)) {
            logger.info("Notificação duplicada evitada para usuário {} tipo {}", destinatario.getId(), tipo);
            return;
        }

        Notificacao notificacao = new Notificacao();
        notificacao.setSessao(sessao);
        notificacao.setDestinatario(destinatario);
        notificacao.setTipo(tipo);
        notificacao.setCanal(CanalNotificacao.EMAIL);
        notificacao.setStatus(StatusNotificacao.ENVIADA);
        notificacao.setDestino(destinatario.getEmail());
        notificacao.setMensagem(mensagem);
        notificacao.setDataAgendada(dataEnvio);
        notificacao.setDataEnvio(dataEnvio);

        notificacaoRepository.save(notificacao);

        logger.info("Enviando notificação por EMAIL para {}: {}", destinatario.getEmail(), mensagem);
    }

    private void criarPendenteSeValido(User destinatario, Sessao sessao, TipoNotificacao tipo, String mensagem, LocalDateTime dataAgendada) {
        if (!possuiEmailValido(destinatario)) {
            logger.warn("Usuário {} sem email válido para lembrete {}", destinatario.getId(), tipo);
            return;
        }

        if (jaExisteNotificacao(sessao, destinatario, tipo)) {
            logger.info("Lembrete duplicado evitado para usuário {} tipo {}", destinatario.getId(), tipo);
            return;
        }

        Notificacao notificacao = new Notificacao();
        notificacao.setSessao(sessao);
        notificacao.setDestinatario(destinatario);
        notificacao.setTipo(tipo);
        notificacao.setCanal(CanalNotificacao.EMAIL);
        notificacao.setStatus(StatusNotificacao.PENDENTE);
        notificacao.setDestino(destinatario.getEmail());
        notificacao.setMensagem(mensagem);
        notificacao.setDataAgendada(dataAgendada);

        notificacaoRepository.save(notificacao);

        logger.info("Lembrete 24h agendado para {} em {}", destinatario.getEmail(), dataAgendada);
    }

    @Scheduled(fixedRate = 60000)
    @Transactional
    public void processarLembretesPendentes() {
        List<Notificacao> pendentes = notificacaoRepository.findByStatusAndTipoAndDataAgendadaLessThanEqual(
                StatusNotificacao.PENDENTE,
                TipoNotificacao.LEMBRETE_24H,
                LocalDateTime.now()
        );

        for (Notificacao notificacao : pendentes) {
            notificacao.setStatus(StatusNotificacao.ENVIADA);
            notificacao.setDataEnvio(LocalDateTime.now());

            logger.info("Enviando lembrete 24h por EMAIL para {}: {}", notificacao.getDestino(), notificacao.getMensagem());
            notificacaoRepository.save(notificacao);
        }
    }

    private boolean jaExisteNotificacao(Sessao sessao, User destinatario, TipoNotificacao tipo) {
        return notificacaoRepository.existsBySessaoAndDestinatarioAndTipoAndCanal(
                sessao,
                destinatario,
                tipo,
                CanalNotificacao.EMAIL
        );
    }

    private boolean possuiEmailValido(User user) {
        return user.getEmail() != null && !user.getEmail().isBlank() && user.getEmail().contains("@");
    }

    private String montarMensagemMentor(User mentor, User mentorado, Sessao sessao) {
        return String.format(
                "Nova sessão agendada com %s em %s das %s às %s. Formato: %s. %s",
                mentorado.getName(),
                sessao.getDataHoraInicio().toLocalDate(),
                sessao.getDataHoraInicio().toLocalTime(),
                sessao.getDataHoraFim().toLocalTime(),
                sessao.getFormato(),
                obterDetalhesLocal(sessao)
        );
    }

    private String montarMensagemMentorado(User mentor, User mentorado, Sessao sessao) {
        return String.format(
                "Sessão agendada com %s em %s das %s às %s. Formato: %s. %s",
                mentor.getName(),
                sessao.getDataHoraInicio().toLocalDate(),
                sessao.getDataHoraInicio().toLocalTime(),
                sessao.getDataHoraFim().toLocalTime(),
                sessao.getFormato(),
                obterDetalhesLocal(sessao)
        );
    }

    private String montarLembreteMentor(User mentor, User mentorado, Sessao sessao) {
        return String.format(
                "Lembrete: sua sessão com %s acontecerá em 24h (%s às %s). %s",
                mentorado.getName(),
                sessao.getDataHoraInicio().toLocalDate(),
                sessao.getDataHoraInicio().toLocalTime(),
                obterDetalhesLocal(sessao)
        );
    }

    private String montarLembreteMentorado(User mentor, User mentorado, Sessao sessao) {
        return String.format(
                "Lembrete: sua sessão com %s acontecerá em 24h (%s às %s). %s",
                mentor.getName(),
                sessao.getDataHoraInicio().toLocalDate(),
                sessao.getDataHoraInicio().toLocalTime(),
                obterDetalhesLocal(sessao)
        );
    }

    private String obterDetalhesLocal(Sessao sessao) {
        if (sessao.getFormato() == com.mentoria.agil.backend.model.FormatoSessao.ONLINE) {
            return "Link da reunião: " + sessao.getLinkReuniao();
        } else {
            return "Endereço: " + sessao.getEndereco();
        }
    }
}
