package br.edu.unifor.missaomarte.gdx.visual;

import br.edu.unifor.missaomarte.domain.Astronauta;
import br.edu.unifor.missaomarte.domain.Engenheiro;
import br.edu.unifor.missaomarte.domain.Passageiro;
import br.edu.unifor.missaomarte.domain.Posicao;
import br.edu.unifor.missaomarte.domain.Professor;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/** Seleciona, para cada tipo, o passageiro no mapa mais proximo da nave. */
public final class MarcadoresDePassageiros {
    private MarcadoresDePassageiros() {
    }

    public static List<Marcador> selecionar(Posicao nave, List<? extends Passageiro> passageiros) {
        Map<Tipo, Marcador> maisProximos = new EnumMap<>(Tipo.class);
        for (Passageiro passageiro : passageiros) {
            Tipo tipo = Tipo.de(passageiro);
            if (tipo == null) {
                continue;
            }
            int distancia = distancia(nave, passageiro.getPosicao());
            Marcador atual = maisProximos.get(tipo);
            if (atual == null || distancia < atual.distancia()) {
                String unidade = distancia == 1 ? "CASA" : "CASAS";
                maisProximos.put(tipo, new Marcador(tipo, passageiro, distancia,
                        tipo.name() + " · " + distancia + " " + unidade));
            }
        }
        List<Marcador> resultado = new ArrayList<>();
        for (Tipo tipo : Tipo.values()) {
            Marcador marcador = maisProximos.get(tipo);
            if (marcador != null) {
                resultado.add(marcador);
            }
        }
        return List.copyOf(resultado);
    }

    private static int distancia(Posicao a, Posicao b) {
        return Math.abs(a.getX() - b.getX()) + Math.abs(a.getY() - b.getY());
    }

    public enum Tipo {
        ASTRONAUTA,
        PROFESSOR,
        ENGENHEIRO;

        public static Tipo de(Passageiro passageiro) {
            if (passageiro instanceof Astronauta) {
                return ASTRONAUTA;
            }
            if (passageiro instanceof Professor) {
                return PROFESSOR;
            }
            if (passageiro instanceof Engenheiro) {
                return ENGENHEIRO;
            }
            return null;
        }
    }

    public record Marcador(Tipo tipo, Passageiro passageiro, int distancia, String texto) {
    }
}
