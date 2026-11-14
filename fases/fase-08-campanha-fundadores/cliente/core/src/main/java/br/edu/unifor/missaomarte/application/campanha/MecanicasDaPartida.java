package br.edu.unifor.missaomarte.application.campanha;

import br.edu.unifor.missaomarte.application.EventoJogo;
import br.edu.unifor.missaomarte.domain.EstacaoNami;
import br.edu.unifor.missaomarte.domain.Missao;
import br.edu.unifor.missaomarte.domain.Posicao;
import java.util.ArrayList;
import java.util.List;

public final class MecanicasDaPartida {
    private final Missao missao;
    private final RedeDoRover redeDoRover;
    private final TempestadeDeAreia tempestade;

    private MecanicasDaPartida(Missao missao, RedeDoRover redeDoRover, TempestadeDeAreia tempestade) {
        this.missao = missao;
        this.redeDoRover = redeDoRover;
        this.tempestade = tempestade;
    }

    public static MecanicasDaPartida nenhuma(Missao missao) {
        return new MecanicasDaPartida(missao, new RedeDoRover(List.of()), null);
    }

    public static MecanicasDaPartida para(Missao missao, boolean comTempestade) {
        return new MecanicasDaPartida(missao, new RedeDoRover(missao.getParadasDoRover()),
                comTempestade ? TempestadeDeAreia.padrao() : null);
    }

    public List<EventoJogo> aposMovimentoDaNave() {
        List<EventoJogo> eventos = new ArrayList<>();
        if (redeDoRover.transportar(missao.getNave())) {
            eventos.add(EventoJogo.ROVER_UTILIZADO);
        }
        for (EstacaoNami nami : missao.getEstacoesNami()) {
            if (nami.ativar(missao.getNave())) {
                eventos.add(EventoJogo.NAMI_ATIVADA);
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
