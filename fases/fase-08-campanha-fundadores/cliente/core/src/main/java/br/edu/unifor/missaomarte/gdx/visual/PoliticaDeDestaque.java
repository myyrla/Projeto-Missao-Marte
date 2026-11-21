package br.edu.unifor.missaomarte.gdx.visual;

import br.edu.unifor.missaomarte.domain.Astronauta;
import br.edu.unifor.missaomarte.domain.Engenheiro;
import br.edu.unifor.missaomarte.domain.Inimigo;
import br.edu.unifor.missaomarte.domain.Passageiro;
import br.edu.unifor.missaomarte.domain.Posicao;
import br.edu.unifor.missaomarte.domain.Professor;
import br.edu.unifor.missaomarte.gdx.layout.RetanguloDeTela;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/** Decide os destaques da partida sem conhecer libGDX nem executar regras do jogo. */
public final class PoliticaDeDestaque {
    private static final float OPACIDADE_RESGATADO = 0.60f;

    private PoliticaDeDestaque() {
    }

    public static Resultado decidir(Posicao nave, List<? extends Passageiro> passageiros,
            Tipo tipoDoFundador, Set<Tipo> tiposResgatados, List<Inimigo> inimigos) {
        List<Passageiro> pendentes = new ArrayList<>();
        Map<Passageiro, Float> opacidades = new HashMap<>();
        for (Passageiro passageiro : passageiros) {
            Tipo tipo = Tipo.de(passageiro);
            boolean resgatado = tipo != null && tiposResgatados.contains(tipo);
            opacidades.put(passageiro, resgatado ? OPACIDADE_RESGATADO : 1f);
            if (!resgatado && tipo != null) {
                pendentes.add(passageiro);
            }
        }

        List<Passageiro> preferidos = tipoDoFundador == null ? List.of()
                : pendentes.stream().filter(p -> Tipo.de(p) == tipoDoFundador).toList();
        Passageiro fundador = preferidos.stream()
                .filter(p -> ehFundadorDoTipo(p, tipoDoFundador))
                .findFirst()
                .orElse(null);
        List<Passageiro> candidatos = fundador != null ? List.of(fundador)
                : preferidos.isEmpty() ? pendentes : preferidos;
        Passageiro alvo = candidatos.stream()
                .min(Comparator.comparingInt(p -> distancia(nave, p.getPosicao())))
                .orElse(null);

        Map<Inimigo, Float> zonas = new HashMap<>();
        for (Inimigo inimigo : inimigos) {
            int distancia = distancia(nave, inimigo.getPosicao());
            zonas.put(inimigo, distancia > 3 ? 0f : (4f - distancia) / 4f);
        }

        AlvoDoObjetivo alvoDoObjetivo = alvo == null ? null
                : new AlvoDoObjetivo(alvo, distancia(nave, alvo.getPosicao()));
        String texto = alvoDoObjetivo == null ? "" : textoDaEtiqueta(alvoDoObjetivo);
        return new Resultado(alvoDoObjetivo, alvo, texto, opacidades, zonas, null);
    }

    public static Optional<RetanguloDeTela> posicionarEtiqueta(RetanguloDeTela alvo,
            float largura, float altura, float margem, RetanguloDeTela areaVisivel,
            List<RetanguloDeTela> areasDoHud) {
        float xCentralizado = alvo.centroX() - largura / 2f;
        float yCentralizado = alvo.centroY() - altura / 2f;
        List<RetanguloDeTela> candidatos = List.of(
                new RetanguloDeTela(xCentralizado, alvo.topo() + margem, largura, altura),
                new RetanguloDeTela(xCentralizado, alvo.y() - margem - altura, largura, altura),
                new RetanguloDeTela(alvo.direita() + margem, yCentralizado, largura, altura),
                new RetanguloDeTela(alvo.x() - margem - largura, yCentralizado, largura, altura));
        return candidatos.stream()
                .filter(area -> area.estaDentroDe(areaVisivel))
                .filter(area -> areasDoHud.stream().noneMatch(area::sobrepoe))
                .findFirst();
    }

    public static List<RetanguloDeTela> areasEvitadas(List<RetanguloDeTela> areasDoHud,
            RetanguloDeTela nave) {
        List<RetanguloDeTela> areas = new ArrayList<>(areasDoHud);
        areas.add(nave);
        return List.copyOf(areas);
    }

    private static String textoDaEtiqueta(AlvoDoObjetivo alvo) {
        return Tipo.de(alvo.passageiro()).name() + " · " + alvo.distancia() + " "
                + (alvo.distancia() == 1 ? "CASA" : "CASAS");
    }

    private static int distancia(Posicao a, Posicao b) {
        return Math.abs(a.getX() - b.getX()) + Math.abs(a.getY() - b.getY());
    }

    private static boolean ehFundadorDoTipo(Passageiro passageiro, Tipo tipo) {
        String nome = tipo == Tipo.ASTRONAUTA ? "Astronauta"
                : tipo == Tipo.PROFESSOR ? "Professor" : "Engenheiro";
        return passageiro.getNome().equals("Fundador " + nome);
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

    public record AlvoDoObjetivo(Passageiro passageiro, int distancia) {
    }

    public static final class Resultado {
        private final AlvoDoObjetivo alvoDoObjetivo;
        private final Passageiro alvo;
        private final String textoDaEtiqueta;
        private final Map<Passageiro, Float> opacidades;
        private final Map<Inimigo, Float> zonas;
        private final RetanguloDeTela etiqueta;

        private Resultado(AlvoDoObjetivo alvoDoObjetivo, Passageiro alvo, String textoDaEtiqueta,
                Map<Passageiro, Float> opacidades, Map<Inimigo, Float> zonas, RetanguloDeTela etiqueta) {
            this.alvoDoObjetivo = alvoDoObjetivo;
            this.alvo = alvo;
            this.textoDaEtiqueta = textoDaEtiqueta;
            this.opacidades = Map.copyOf(opacidades);
            this.zonas = Map.copyOf(zonas);
            this.etiqueta = etiqueta;
        }

        public Optional<Passageiro> alvo() {
            return Optional.ofNullable(alvo);
        }

        public Optional<AlvoDoObjetivo> alvoDoObjetivo() {
            return Optional.ofNullable(alvoDoObjetivo);
        }

        public String textoDaEtiqueta() {
            return textoDaEtiqueta;
        }

        public float opacidade(Passageiro passageiro) {
            return opacidades.getOrDefault(passageiro, 1f);
        }

        public float opacidadeDaZona(Inimigo inimigo) {
            return zonas.getOrDefault(inimigo, 0f);
        }

        public Optional<RetanguloDeTela> etiqueta() {
            return Optional.ofNullable(etiqueta);
        }

        public Resultado comEtiqueta(RetanguloDeTela novaEtiqueta) {
            return new Resultado(alvoDoObjetivo, alvo, textoDaEtiqueta, opacidades, zonas, novaEtiqueta);
        }

        public Resultado semAlvoDestacado() {
            return new Resultado(alvoDoObjetivo, null, "", opacidades, zonas, null);
        }
    }
}
