package com.vastidev.coopvotes.sessaoVotacao.application.service;

import com.vastidev.coopvotes.associado.application.service.AssociadoService;
import com.vastidev.coopvotes.pauta.application.service.PautaService;
import com.vastidev.coopvotes.pauta.domain.Pauta;
import com.vastidev.coopvotes.sessaoVotacao.application.api.*;
import com.vastidev.coopvotes.sessaoVotacao.domain.PublicadorResultadoSessao;
import com.vastidev.coopvotes.sessaoVotacao.domain.SessaoVotacao;
import com.vastidev.coopvotes.sessaoVotacao.domain.VotoPauta;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Log4j2
@Service
@RequiredArgsConstructor
public class SessaoVotacaoApplicationService implements SessaoVotacaoService {
    private final SessaoVotacaoRepository sessaoVotacaoRepository;
    private final PautaService pautaService;
    private final AssociadoService associadoService;
    private final PublicadorResultadoSessao publicadorResultadoSessao;

    @Override
    public SessaoAberturaResponse abreSessao(SessaoAberturaRequest sessaoAberturaRequest) {
        log.info("[start] SessaoVotacaoApplicationService - abreSessao ");
        Pauta pauta = pautaService.getPautaPorId(sessaoAberturaRequest.getIdPauta());
        SessaoVotacao sessaoVotacao = sessaoVotacaoRepository.salva(new SessaoVotacao(sessaoAberturaRequest, pauta));
        log.info("[finish] SessaoVotacaoApplicationService - abreSessao ");
        return new SessaoAberturaResponse(sessaoVotacao);
    }

    @Override
    public VotoResponse recebeVoto(UUID idSessao, VotoRequest novoVoto) {
        log.info("[start] SessaoVotacaoApplicationService -  recebeVoto");
        SessaoVotacao sessao = sessaoVotacaoRepository.buscaPorId(idSessao);
        VotoPauta voto = sessao.recebeVoto(novoVoto, associadoService, publicadorResultadoSessao);
        sessaoVotacaoRepository.salva(sessao);
        log.info("[finish] SessaoVotacaoApplicationService -  recebeVoto");
        return new VotoResponse(voto) ;
    }

    @Override
    public ResultadoSessaoResponse obtemResultado(UUID idSessao) {
        log.info("[start] SessaoVotacaoApplicationService - obtemResultado ");
        SessaoVotacao sessao = sessaoVotacaoRepository.buscaPorId(idSessao);
        ResultadoSessaoResponse resultado = sessao.obtemResultado(publicadorResultadoSessao);
        sessaoVotacaoRepository.salva(sessao);
        log.info("[finish] SessaoVotacaoApplicationService - obtemResultado ");
        return resultado;
    }
}
