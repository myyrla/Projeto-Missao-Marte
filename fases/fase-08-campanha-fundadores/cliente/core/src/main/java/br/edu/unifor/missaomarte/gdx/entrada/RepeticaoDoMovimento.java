package br.edu.unifor.missaomarte.gdx.entrada;

import br.edu.unifor.missaomarte.application.Acao;
import br.edu.unifor.missaomarte.gdx.RepetidorDeTecla;
import java.util.Optional;

/** Mantem a repeticao classica de movimento igual para teclado e controles de toque. */
public final class RepeticaoDoMovimento {
    private static final float ATRASO_INICIAL = 0.28f;
    private static final float INTERVALO = 0.14f;

    private final RepetidorDeTecla repetidor = new RepetidorDeTecla(ATRASO_INICIAL, INTERVALO);
    private Acao acao = Acao.INVALIDA;

    public void iniciar(Acao novaAcao) {
        if (!ehMovimento(novaAcao)) {
            throw new IllegalArgumentException("A repeticao aceita apenas movimentos.");
        }
        acao = novaAcao;
        repetidor.iniciar();
    }

    public void parar() {
        acao = Acao.INVALIDA;
        repetidor.parar();
    }

    public Optional<Acao> atualizar(float delta) {
        return repetidor.atualizar(delta) && ehMovimento(acao)
                ? Optional.of(acao) : Optional.empty();
    }

    private static boolean ehMovimento(Acao candidata) {
        return candidata == Acao.CIMA || candidata == Acao.BAIXO
                || candidata == Acao.ESQUERDA || candidata == Acao.DIREITA;
    }
}
