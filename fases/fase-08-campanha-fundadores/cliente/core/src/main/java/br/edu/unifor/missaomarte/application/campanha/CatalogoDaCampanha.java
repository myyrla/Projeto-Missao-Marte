package br.edu.unifor.missaomarte.application.campanha;

import br.edu.unifor.missaomarte.domain.Dificuldade;
import br.edu.unifor.missaomarte.application.ModoDeJogo;
import java.util.List;

public final class CatalogoDaCampanha {
    private static final List<EtapaDaCampanha> ETAPAS = List.of(
            new EtapaDaCampanha(1, "Planície Utopia", Fundador.ASTRONAUTA, Dificuldade.FACIL, ModoDeJogo.DINAMICO,
                    new MecanicasDaEtapa(false, 0, false, 0)),
            new EtapaDaCampanha(2, "Cratera Jezero", Fundador.PROFESSOR, Dificuldade.MEDIO, ModoDeJogo.DINAMICO,
                    new MecanicasDaEtapa(true, 0, false, 1)),
            new EtapaDaCampanha(3, "Valles Marineris", Fundador.ENGENHEIRO, Dificuldade.DIFICIL, ModoDeJogo.DINAMICO,
                    new MecanicasDaEtapa(true, 2, true, 2)));

    private CatalogoDaCampanha() {
    }

    public static EtapaDaCampanha etapa(int numero) {
        if (numero < 1 || numero > ETAPAS.size()) {
            throw new IllegalArgumentException("A etapa deve estar entre 1 e 3.");
        }
        return ETAPAS.get(numero - 1);
    }

    static int etapaDoFundador(Fundador fundador) {
        return ETAPAS.stream()
                .filter(etapa -> etapa.fundador() == fundador)
                .mapToInt(EtapaDaCampanha::numero)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Fundador sem etapa na campanha."));
    }
}
