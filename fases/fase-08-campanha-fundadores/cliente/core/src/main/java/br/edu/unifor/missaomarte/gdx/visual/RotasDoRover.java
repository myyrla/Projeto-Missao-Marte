package br.edu.unifor.missaomarte.gdx.visual;

import br.edu.unifor.missaomarte.domain.ParadaDoRover;
import br.edu.unifor.missaomarte.domain.Posicao;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Agrupa as duas paradas de cada par em uma unica rota visual. */
public final class RotasDoRover {
    private RotasDoRover() {
    }

    public static List<Rota> agrupar(List<ParadaDoRover> paradas) {
        Map<String, Rota> porPar = new LinkedHashMap<>();
        for (ParadaDoRover parada : paradas) {
            porPar.putIfAbsent(parada.getPar(),
                    new Rota(parada.getPar(), parada.getPosicao(), parada.getDestino()));
        }
        return List.copyOf(new ArrayList<>(porPar.values()));
    }

    public record Rota(String par, Posicao origem, Posicao destino) {
    }
}
