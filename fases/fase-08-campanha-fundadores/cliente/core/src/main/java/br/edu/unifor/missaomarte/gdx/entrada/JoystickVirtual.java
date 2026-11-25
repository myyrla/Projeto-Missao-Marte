package br.edu.unifor.missaomarte.gdx.entrada;

import br.edu.unifor.missaomarte.application.Acao;
import java.util.ArrayList;
import java.util.List;

/**
 * Traduz o arrasto de um unico ponteiro em comandos de movimento, sem depender do libGDX.
 * A base permanece fixa, o botao visual e limitado ao seu interior e a zona morta ocupa 25% do raio.
 */
public final class JoystickVirtual {
    private static final float PROPORCAO_DA_ZONA_MORTA = 0.25f;

    public enum TipoDeComando { EXECUTAR, PRESSIONAR, SOLTAR }

    public record Comando(TipoDeComando tipo, Acao acao) {
    }

    public record Resultado(boolean tratado, List<Comando> comandos) {
        private static Resultado ignorado() {
            return new Resultado(false, List.of());
        }

        private static Resultado tratado(List<Comando> comandos) {
            return new Resultado(true, List.copyOf(comandos));
        }
    }

    private final float centroX;
    private final float centroY;
    private final float raio;
    private final float raioDoBotao;
    private final boolean dinamico;

    private int ponteiroAtivo = -1;
    private float botaoX;
    private float botaoY;
    private Acao direcao = Acao.INVALIDA;

    public JoystickVirtual(float centroX, float centroY, float raio, float raioDoBotao, boolean dinamico) {
        if (raio <= 0f || raioDoBotao < 0f || raioDoBotao > raio) {
            throw new IllegalArgumentException("Os raios do joystick precisam ser validos.");
        }
        this.centroX = centroX;
        this.centroY = centroY;
        this.raio = raio;
        this.raioDoBotao = raioDoBotao;
        this.dinamico = dinamico;
        this.botaoX = centroX;
        this.botaoY = centroY;
    }

    public Resultado pressionar(int ponteiro, float x, float y) {
        if (ponteiroAtivo >= 0 || !contem(x, y)) {
            return Resultado.ignorado();
        }
        ponteiroAtivo = ponteiro;
        return atualizar(x, y);
    }

    public Resultado arrastar(int ponteiro, float x, float y) {
        if (ponteiro != ponteiroAtivo) {
            return Resultado.ignorado();
        }
        return atualizar(x, y);
    }

    public Resultado soltar(int ponteiro) {
        if (ponteiro != ponteiroAtivo) {
            return Resultado.ignorado();
        }
        List<Comando> comandos = new ArrayList<>();
        if (ehMovimento(direcao)) {
            comandos.add(new Comando(TipoDeComando.SOLTAR, direcao));
        }
        ponteiroAtivo = -1;
        direcao = Acao.INVALIDA;
        botaoX = centroX;
        botaoY = centroY;
        return Resultado.tratado(comandos);
    }

    public boolean contem(float x, float y) {
        float dx = x - centroX;
        float dy = y - centroY;
        return dx * dx + dy * dy <= raio * raio;
    }

    private Resultado atualizar(float x, float y) {
        float dx = x - centroX;
        float dy = y - centroY;
        atualizarBotao(dx, dy);
        Acao novaDirecao = direcaoDe(dx, dy);
        if (novaDirecao == direcao) {
            return Resultado.tratado(List.of());
        }

        List<Comando> comandos = new ArrayList<>(2);
        if (ehMovimento(direcao)) {
            comandos.add(new Comando(TipoDeComando.SOLTAR, direcao));
        }
        direcao = novaDirecao;
        if (ehMovimento(novaDirecao)) {
            comandos.add(new Comando(dinamico ? TipoDeComando.PRESSIONAR : TipoDeComando.EXECUTAR,
                    novaDirecao));
        }
        return Resultado.tratado(comandos);
    }

    private void atualizarBotao(float dx, float dy) {
        float distancia = (float) Math.sqrt(dx * dx + dy * dy);
        float alcance = Math.max(0f, raio - raioDoBotao);
        if (distancia <= alcance || distancia == 0f) {
            botaoX = centroX + dx;
            botaoY = centroY + dy;
            return;
        }
        float fator = alcance / distancia;
        botaoX = centroX + dx * fator;
        botaoY = centroY + dy * fator;
    }

    private Acao direcaoDe(float dx, float dy) {
        float distanciaAoQuadrado = dx * dx + dy * dy;
        float zonaMorta = raio * PROPORCAO_DA_ZONA_MORTA;
        if (distanciaAoQuadrado <= zonaMorta * zonaMorta) {
            return Acao.INVALIDA;
        }
        if (Math.abs(dx) >= Math.abs(dy)) {
            return dx >= 0f ? Acao.DIREITA : Acao.ESQUERDA;
        }
        return dy >= 0f ? Acao.CIMA : Acao.BAIXO;
    }

    private static boolean ehMovimento(Acao acao) {
        return acao == Acao.CIMA || acao == Acao.BAIXO
                || acao == Acao.ESQUERDA || acao == Acao.DIREITA;
    }

    public float centroX() { return centroX; }
    public float centroY() { return centroY; }
    public float raio() { return raio; }
    public float raioDoBotao() { return raioDoBotao; }
    public float botaoX() { return botaoX; }
    public float botaoY() { return botaoY; }
    public Acao direcao() { return direcao; }
    public int ponteiroAtivo() { return ponteiroAtivo; }
}
