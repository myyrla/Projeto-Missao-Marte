package br.edu.unifor.missaomarte.gdx.entrada;

import br.edu.unifor.missaomarte.application.Acao;
import br.edu.unifor.missaomarte.gdx.layout.RetanguloDeTela;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Controles virtuais da partida: joystick ou setas, acao principal, pausa, som e abortar.
 * <p>O retrato usa joystick a esquerda e botoes circulares a direita. Na paisagem, cada grupo ocupa sua coluna lateral.
 * O tamanho tocavel respeita o alvo minimo informado em unidades do mundo.
 * <p>A classe calcula o layout e roteia ponteiros sem depender do libGDX; o desenho fica em outro objeto.
 */
public final class ControleDeToque {
    public static final String CIMA = "cima";
    public static final String BAIXO = "baixo";
    public static final String ESQUERDA = "esquerda";
    public static final String DIREITA = "direita";
    public static final String EMBARCAR = "embarcar";
    public static final String ATIRAR = "atirar";
    public static final String PAUSA = "pausa";
    public static final String SOM = "som";
    public static final String ABORTAR = "abortar";

    private static final float MARGEM = 12f;
    private static final float ESPACO_ENTRE_CIRCULOS = 16f;
    private static final float MARGEM_INTERNA_DO_ROTULO = 10f;

    private final List<BotaoVirtual> botoes;
    private final JoystickVirtual joystick;
    private final Map<String, Float> escalasDosRotulos;

    public record ResultadoDaPressao(boolean tratado, Optional<String> botao,
                                     List<JoystickVirtual.Comando> comandos) {
        private static ResultadoDaPressao ignorado() {
            return new ResultadoDaPressao(false, Optional.empty(), List.of());
        }
    }

    public record MedidaDoRotulo(float largura, float altura) {
        public MedidaDoRotulo {
            largura = Math.max(0f, largura);
            altura = Math.max(0f, altura);
        }
    }

    public record MedidasDosRotulos(Map<String, MedidaDoRotulo> porId) {
        private static final MedidaDoRotulo VAZIA = new MedidaDoRotulo(0f, 0f);

        public MedidasDosRotulos {
            porId = Map.copyOf(porId);
        }

        public MedidaDoRotulo de(String id) {
            return porId.getOrDefault(id, VAZIA);
        }

        public static MedidasDosRotulos vazias() {
            return new MedidasDosRotulos(Map.of());
        }
    }

    /**
     * @param dinamico true no modo dinamico (tem tiro e pausa); false no classico (tem embarcar, como na Fase 6)
     * @param alvoMinimo menor lado aceitavel de um botao, em unidades do mundo
     */
    public ControleDeToque(RetanguloDeTela area, boolean emRetrato, boolean dinamico, float alvoMinimo) {
        this(area, emRetrato, dinamico, alvoMinimo, MedidasDosRotulos.vazias());
    }

    public ControleDeToque(RetanguloDeTela area, boolean emRetrato, boolean dinamico,
            float alvoMinimo, MedidasDosRotulos medidas) {
        this(areaDoMovimento(area, emRetrato), areaDasAcoes(area, emRetrato),
                emRetrato, dinamico, alvoMinimo, medidas);
    }

    public ControleDeToque(RetanguloDeTela areaDoMovimento, RetanguloDeTela areaDasAcoes,
            boolean emRetrato, boolean dinamico, float alvoMinimo, MedidasDosRotulos medidas) {
        List<BotaoVirtual> lista = new ArrayList<>();
        if (emRetrato) {
            joystick = montarEmRetrato(lista, areaDoMovimento, dinamico, alvoMinimo, medidas);
        } else {
            joystick = montarJoystickNaArea(areaDoMovimento, dinamico, alvoMinimo);
            montarAcoesEmPaisagem(lista, areaDasAcoes, dinamico, alvoMinimo, medidas);
        }
        botoes = Collections.unmodifiableList(lista);
        escalasDosRotulos = calcularEscalasDosRotulos(lista, medidas);
    }

    private static RetanguloDeTela areaDoMovimento(RetanguloDeTela area, boolean emRetrato) {
        return emRetrato ? area : new RetanguloDeTela(
                area.x(), area.y(), area.largura() / 2f, area.altura());
    }

    private static RetanguloDeTela areaDasAcoes(RetanguloDeTela area, boolean emRetrato) {
        return emRetrato ? area : new RetanguloDeTela(
                area.centroX(), area.y(), area.largura() / 2f, area.altura());
    }

    private static JoystickVirtual montarEmRetrato(List<BotaoVirtual> lista, RetanguloDeTela area,
            boolean dinamico, float alvoMinimo, MedidasDosRotulos medidas) {
        float margem = Math.max(MARGEM, alvoMinimo / 5f);
        float larguraDaMetade = area.largura() / 2f;
        float raioMaximo = Math.min((larguraDaMetade - 2f * margem) / 2f,
                (area.altura() - 2f * margem) / 2f);
        float raio = Math.max(alvoMinimo / 2f, Math.min(112f, raioMaximo));
        float centroX = area.x() + margem + raio;
        float centroY = area.centroY();
        JoystickVirtual joystick = new JoystickVirtual(
                centroX, centroY, raio, Math.max(22f, raio * 0.38f), dinamico);

        List<String> idsSecundarios = dinamico ? List.of(PAUSA, SOM, ABORTAR) : List.of(SOM, ABORTAR);
        List<String> rotulosSecundarios = dinamico ? List.of("Pausa", "Som", "Abortar") : List.of("Som", "Abortar");
        float[] diametros = new float[idsSecundarios.size()];
        float larguraDaLinha = 0f;
        float maiorSecundario = 0f;
        for (int i = 0; i < idsSecundarios.size(); i++) {
            diametros[i] = diametroDoBotao(medidas.de(idsSecundarios.get(i)), alvoMinimo);
            larguraDaLinha += diametros[i];
            maiorSecundario = Math.max(maiorSecundario, diametros[i]);
        }
        larguraDaLinha += (idsSecundarios.size() - 1) * ESPACO_ENTRE_CIRCULOS;

        String idPrincipal = dinamico ? ATIRAR : EMBARCAR;
        String rotuloPrincipal = dinamico ? "Atirar" : "Embarcar";
        float diametroPrincipal = Math.max(
                diametroDoBotao(medidas.de(idPrincipal), alvoMinimo), maiorSecundario + 12f);
        float larguraDoGrupo = Math.max(larguraDaLinha, diametroPrincipal);
        float alturaDoGrupo = maiorSecundario + ESPACO_ENTRE_CIRCULOS + diametroPrincipal;
        float xDoGrupo = area.direita() - margem - larguraDoGrupo;
        float yDoGrupo = limitar(area.centroY() - alturaDoGrupo / 2f,
                area.y() + margem, area.topo() - margem - alturaDoGrupo);

        float xPrincipal = xDoGrupo + (larguraDoGrupo - diametroPrincipal) / 2f;
        float yPrincipal = yDoGrupo + maiorSecundario + ESPACO_ENTRE_CIRCULOS;
        lista.add(new BotaoVirtual(idPrincipal, rotuloPrincipal,
                xPrincipal, yPrincipal, diametroPrincipal, diametroPrincipal));

        float x = xDoGrupo + (larguraDoGrupo - larguraDaLinha) / 2f;
        for (int i = 0; i < idsSecundarios.size(); i++) {
            float diametro = diametros[i];
            float y = yDoGrupo + (maiorSecundario - diametro) / 2f;
            lista.add(new BotaoVirtual(idsSecundarios.get(i), rotulosSecundarios.get(i),
                    x, y, diametro, diametro));
            x += diametro + ESPACO_ENTRE_CIRCULOS;
        }
        return joystick;
    }

    private static JoystickVirtual montarJoystickNaArea(RetanguloDeTela area,
            boolean dinamico, float alvoMinimo) {
        float margem = Math.max(MARGEM, alvoMinimo / 5f);
        float raioMaximo = Math.max(1f, Math.min(
                (area.largura() - 2f * margem) / 2f,
                (area.altura() - 2f * margem) / 2f));
        float raio = Math.min(96f, raioMaximo);
        float raioDoBotao = Math.min(raio * 0.48f, Math.max(18f, alvoMinimo * 0.45f));
        return new JoystickVirtual(area.centroX(), area.centroY(), raio, raioDoBotao, dinamico);
    }

    private static void montarAcoesEmPaisagem(List<BotaoVirtual> lista, RetanguloDeTela area,
            boolean dinamico, float alvoMinimo, MedidasDosRotulos medidas) {
        List<String> idsSecundarios = dinamico ? List.of(PAUSA, SOM, ABORTAR) : List.of(SOM, ABORTAR);
        List<String> rotulosSecundarios = dinamico ? List.of("Pausa", "Som", "Abortar") : List.of("Som", "Abortar");
        String idPrincipal = dinamico ? ATIRAR : EMBARCAR;
        String rotuloPrincipal = dinamico ? "Atirar" : "Embarcar";
        float margem = Math.max(MARGEM, alvoMinimo / 5f);
        float espaco = 12f;
        float larguraDisponivel = Math.max(alvoMinimo, area.largura() - 2f * margem);
        float alturaDisponivel = Math.max(0f, area.altura() - 2f * margem);
        float unidades = idsSecundarios.size() + 1.25f;
        float unidade = Math.max(alvoMinimo,
                (alturaDisponivel - idsSecundarios.size() * espaco) / unidades);
        float limiteSecundario = Math.min(larguraDisponivel, unidade);
        float limitePrincipal = Math.min(larguraDisponivel, unidade * 1.25f);

        float maiorSecundarioDesejado = 0f;
        float[] diametrosSecundarios = new float[idsSecundarios.size()];
        for (int i = 0; i < idsSecundarios.size(); i++) {
            float desejado = diametroDoBotao(medidas.de(idsSecundarios.get(i)), alvoMinimo);
            diametrosSecundarios[i] = Math.max(alvoMinimo, Math.min(desejado, limiteSecundario));
            maiorSecundarioDesejado = Math.max(maiorSecundarioDesejado, diametrosSecundarios[i]);
        }
        float desejadoPrincipal = Math.max(
                diametroDoBotao(medidas.de(idPrincipal), alvoMinimo), maiorSecundarioDesejado + 12f);
        float diametroPrincipal = Math.max(alvoMinimo, Math.min(desejadoPrincipal, limitePrincipal));
        float alturaDoGrupo = diametroPrincipal + idsSecundarios.size() * espaco;
        for (float diametro : diametrosSecundarios) {
            alturaDoGrupo += diametro;
        }
        float y = area.centroY() - alturaDoGrupo / 2f;
        for (int i = idsSecundarios.size() - 1; i >= 0; i--) {
            float diametro = diametrosSecundarios[i];
            lista.add(new BotaoVirtual(idsSecundarios.get(i), rotulosSecundarios.get(i),
                    area.centroX() - diametro / 2f, y, diametro, diametro));
            y += diametro + espaco;
        }
        lista.add(new BotaoVirtual(idPrincipal, rotuloPrincipal,
                area.centroX() - diametroPrincipal / 2f, y,
                diametroPrincipal, diametroPrincipal));
    }

    private static float diametroDoBotao(MedidaDoRotulo medida, float alvoMinimo) {
        float diagonal = (float) Math.hypot(medida.largura(), medida.altura());
        return Math.max(alvoMinimo, diagonal + 2f * MARGEM_INTERNA_DO_ROTULO);
    }

    private static float limitar(float valor, float minimo, float maximo) {
        return Math.max(minimo, Math.min(valor, maximo));
    }

    private static Map<String, Float> calcularEscalasDosRotulos(
            List<BotaoVirtual> botoes, MedidasDosRotulos medidas) {
        Map<String, Float> escalas = new HashMap<>();
        for (BotaoVirtual botao : botoes) {
            MedidaDoRotulo medida = medidas.de(botao.getId());
            float diagonal = (float) Math.hypot(medida.largura(), medida.altura());
            float disponivel = Math.max(0f, botao.getLargura() - 2f * MARGEM_INTERNA_DO_ROTULO);
            escalas.put(botao.getId(), diagonal <= 0f ? 1f : Math.min(1f, disponivel / diagonal));
        }
        return Map.copyOf(escalas);
    }

    public List<BotaoVirtual> getBotoes() {
        return botoes;
    }

    public Optional<JoystickVirtual> joystick() {
        return Optional.ofNullable(joystick);
    }

    public float escalaDoRotulo(String id) {
        return escalasDosRotulos.getOrDefault(id, 1f);
    }

    /** Roteia um novo ponteiro para o joystick ou para um botao circular do modo retrato. */
    public ResultadoDaPressao pressionar(int ponteiro, float x, float y) {
        if (joystick == null) {
            return ResultadoDaPressao.ignorado();
        }
        JoystickVirtual.Resultado resultado = joystick.pressionar(ponteiro, x, y);
        if (resultado.tratado()) {
            return new ResultadoDaPressao(true, Optional.empty(), resultado.comandos());
        }
        BotaoVirtual botao = em(x, y);
        return botao == null ? ResultadoDaPressao.ignorado()
                : new ResultadoDaPressao(true, Optional.of(botao.getId()), List.of());
    }

    public JoystickVirtual.Resultado arrastar(int ponteiro, float x, float y) {
        return joystick == null ? new JoystickVirtual.Resultado(false, List.of())
                : joystick.arrastar(ponteiro, x, y);
    }

    public JoystickVirtual.Resultado soltar(int ponteiro) {
        return joystick == null ? new JoystickVirtual.Resultado(false, List.of())
                : joystick.soltar(ponteiro);
    }

    /** Devolve o botao sob o ponto ou null. */
    public BotaoVirtual em(float x, float y) {
        for (BotaoVirtual botao : botoes) {
            if (contemNoCirculo(botao, x, y)) {
                return botao;
            }
        }
        return null;
    }

    private static boolean contemNoCirculo(BotaoVirtual botao, float x, float y) {
        float raio = Math.min(botao.getLargura(), botao.getAltura()) / 2f;
        float dx = x - (botao.getX() + botao.getLargura() / 2f);
        float dy = y - (botao.getY() + botao.getAltura() / 2f);
        return dx * dx + dy * dy <= raio * raio;
    }

    /** Acao do jogo para o botao; {@link Acao#INVALIDA} para botoes que nao sao acao (som e pausa). */
    public static Acao acaoDe(String id) {
        switch (id) {
            case CIMA:
                return Acao.CIMA;
            case BAIXO:
                return Acao.BAIXO;
            case ESQUERDA:
                return Acao.ESQUERDA;
            case DIREITA:
                return Acao.DIREITA;
            case EMBARCAR:
                return Acao.EMBARCAR;
            case ATIRAR:
                return Acao.ATIRAR;
            case ABORTAR:
                return Acao.SAIR;
            default:
                return Acao.INVALIDA;
        }
    }

    /** True para as setas: no modo dinamico a nave anda enquanto o botao esta pressionado. */
    public static boolean ehSeta(String id) {
        return CIMA.equals(id) || BAIXO.equals(id) || ESQUERDA.equals(id) || DIREITA.equals(id);
    }

    /** Setas repetem enquanto o dedo fica apoiado; os demais botoes agem uma vez por toque. */
    public static boolean repete(String id) {
        return ehSeta(id);
    }
}
