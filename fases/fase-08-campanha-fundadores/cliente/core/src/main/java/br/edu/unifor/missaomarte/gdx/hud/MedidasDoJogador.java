package br.edu.unifor.missaomarte.gdx.hud;

/** Métricas tipográficas ativas do cartão do jogador. */
public record MedidasDoJogador(float alturaDoTitulo, float alturaDaLinha,
                                float larguraDoRotuloDoCasco) {
    public static MedidasDoJogador padrao(float escala) {
        return new MedidasDoJogador(14f * escala, 14f * escala, 42f * escala);
    }
}
