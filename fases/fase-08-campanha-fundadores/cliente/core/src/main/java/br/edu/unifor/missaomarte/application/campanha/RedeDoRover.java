package br.edu.unifor.missaomarte.application.campanha;

import br.edu.unifor.missaomarte.domain.Nave;
import br.edu.unifor.missaomarte.domain.ParadaDoRover;
import br.edu.unifor.missaomarte.domain.Posicao;
import java.util.List;

public final class RedeDoRover {
    private final List<ParadaDoRover> paradas;
    private Posicao bloqueadaNestePasso;

    public RedeDoRover(List<ParadaDoRover> paradas) {
        this.paradas = List.copyOf(paradas);
    }

    public boolean transportar(Nave nave) {
        Posicao atual = nave.getPosicao();
        if (atual.equals(bloqueadaNestePasso)) {
            return false;
        }
        bloqueadaNestePasso = null;
        for (ParadaDoRover parada : paradas) {
            if (parada.getPosicao().equals(atual)) {
                nave.moverPara(parada.getDestino());
                bloqueadaNestePasso = parada.getDestino();
                return true;
            }
        }
        return false;
    }
}
