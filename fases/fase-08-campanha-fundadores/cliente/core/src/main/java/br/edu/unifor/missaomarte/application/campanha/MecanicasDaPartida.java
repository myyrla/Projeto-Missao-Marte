package br.edu.unifor.missaomarte.application.campanha;

import br.edu.unifor.missaomarte.application.EventoJogo;
import br.edu.unifor.missaomarte.domain.EstacaoNami;
import br.edu.unifor.missaomarte.domain.EstacaoNami.ResultadoDaAtivacao;
import br.edu.unifor.missaomarte.domain.Missao;
import br.edu.unifor.missaomarte.domain.Posicao;
import java.util.ArrayList;
import java.util.List;

public final class MecanicasDaPartida {
    private final Missao missao;
    private final TempestadeDeAreia tempestade;

    private MecanicasDaPartida(Missao missao, TempestadeDeAreia tempestade) {
        this.missao = missao;
        this.tempestade = tempestade;
    }

    public static MecanicasDaPartida nenhuma(Missao missao) {
        return new MecanicasDaPartida(missao, null);
    }

    public static MecanicasDaPartida para(Missao missao, boolean comTempestade) {
        return new MecanicasDaPartida(missao, comTempestade ? TempestadeDeAreia.padrao() : null);
    }

    public List<EventoJogo> aposMovimentoDaNave() {
        List<EventoJogo> eventos = new ArrayList<>();
        for (EstacaoNami nami : missao.getEstacoesNami()) {
            ResultadoDaAtivacao resultado = nami.tentarAtivar(missao.getNave());
            if (resultado == ResultadoDaAtivacao.REPAROU) {
                eventos.add(EventoJogo.NAMI_ATIVADA);
            } else if (resultado == ResultadoDaAtivacao.CASCO_COMPLETO) {
                eventos.add(EventoJogo.NAMI_CASCO_COMPLETO);
            }
        }
        return eventos;
    }

    public void avancar(float delta) {
        if (tempestade != null) {
            tempestade.avancar(delta);
        }
    }

    public boolean estaVisivel(Posicao alvo) {
        return tempestade == null || tempestade.estaVisivel(missao.getNave().getPosicao(), alvo);
    }

    public boolean temTempestade() {
        return tempestade != null;
    }
}
