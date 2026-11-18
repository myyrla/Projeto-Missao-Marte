package br.edu.unifor.missaomarte.gdx.visual;

/** Estado testavel de um anel de pulso, independente do OpenGL. */
public final class AnimacaoDoPulso {
    private static final float DURACAO = 1.6f;

    private AnimacaoDoPulso() {
    }

    public static Estado calcular(float tempo, float atraso) {
        float fase = ((tempo + atraso) % DURACAO + DURACAO) % DURACAO / DURACAO;
        return new Estado(0.6f + 0.8f * fase, 0.6f * (1f - fase));
    }

    public record Estado(float escala, float alfa) {
    }
}
