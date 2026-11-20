package br.edu.unifor.missaomarte.gdx.campanha;

import br.edu.unifor.missaomarte.application.campanha.Campanha;
import br.edu.unifor.missaomarte.application.campanha.CatalogoDaCampanha;
import br.edu.unifor.missaomarte.application.campanha.Construcao;
import br.edu.unifor.missaomarte.application.campanha.EtapaDaCampanha;
import br.edu.unifor.missaomarte.application.campanha.EstadoDaConstrucao;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public final class ResumoVisualDaCampanha {
    public enum SituacaoDaEtapa { RESGATADA, ATUAL, PENDENTE }

    public record Etapa(int numero, String local, String fundador, SituacaoDaEtapa situacao) {
    }

    public record ProximaMissao(String cenario, String fundador, String mecanica, boolean concluida) {
    }

    public record Conclusao(int pontuacaoTotal, int construcoesErguidas,
                            int totalDeConstrucoes, String frase) {
    }

    private final List<Etapa> etapas;
    private final int fundadoresResgatados;
    private final int pontuacaoTotal;
    private final String rotuloDaAcao;
    private final ProximaMissao proximaMissao;
    private final Conclusao conclusao;

    private ResumoVisualDaCampanha(List<Etapa> etapas, int fundadoresResgatados,
                                  int pontuacaoTotal, String rotuloDaAcao, ProximaMissao proximaMissao,
                                  Conclusao conclusao) {
        this.etapas = Collections.unmodifiableList(etapas);
        this.fundadoresResgatados = fundadoresResgatados;
        this.pontuacaoTotal = pontuacaoTotal;
        this.rotuloDaAcao = rotuloDaAcao;
        this.proximaMissao = proximaMissao;
        this.conclusao = conclusao;
    }

    public static ResumoVisualDaCampanha de(Campanha campanha) {
        int atual = campanha.progresso().etapaAtual();
        List<Etapa> etapas = new ArrayList<>();
        for (int numero = 1; numero <= 3; numero++) {
            EtapaDaCampanha etapa = CatalogoDaCampanha.etapa(numero);
            SituacaoDaEtapa situacao = numero < atual ? SituacaoDaEtapa.RESGATADA
                    : numero == atual ? SituacaoDaEtapa.ATUAL : SituacaoDaEtapa.PENDENTE;
            etapas.add(new Etapa(numero, etapa.local(), nomeDoFundador(etapa), situacao));
        }
        int resgatados = Math.min(3, atual - 1);
        ProximaMissao proxima = campanha.concluida()
                ? new ProximaMissao("Campanha concluída", "Os três fundadores foram resgatados", "", true)
                : proximaMissao(CatalogoDaCampanha.etapa(atual));
        Conclusao conclusao = campanha.concluida() ? conclusao(campanha) : null;
        return new ResumoVisualDaCampanha(etapas, resgatados, campanha.pontuacaoTotal(),
                campanha.concluida() ? "Nova campanha" : "Iniciar missão", proxima, conclusao);
    }

    private static Conclusao conclusao(Campanha campanha) {
        int erguidas = 0;
        for (Construcao construcao : Construcao.values()) {
            if (campanha.campus().estado(construcao) != EstadoDaConstrucao.PLANTA) {
                erguidas++;
            }
        }
        return new Conclusao(campanha.pontuacaoTotal(), erguidas, Construcao.values().length,
                "O campus UNIFOR Marte está pronto para a inauguração.");
    }

    private static ProximaMissao proximaMissao(EtapaDaCampanha etapa) {
        String mecanica = etapa.mecanicas().temTempestade() ? "Tempestade de areia"
                : etapa.mecanicas().temEstacaoNami() ? "NAMI" : "Aprendizado";
        return new ProximaMissao(etapa.local(), nomeDoFundador(etapa), mecanica, false);
    }

    private static String nomeDoFundador(EtapaDaCampanha etapa) {
        String texto = etapa.fundador().name().toLowerCase();
        return Character.toUpperCase(texto.charAt(0)) + texto.substring(1);
    }

    public List<Etapa> etapas() {
        return etapas;
    }

    public int fundadoresResgatados() {
        return fundadoresResgatados;
    }

    public int pontuacaoTotal() {
        return pontuacaoTotal;
    }

    public String rotuloDaAcao() {
        return rotuloDaAcao;
    }

    public ProximaMissao proximaMissao() {
        return proximaMissao;
    }

    public Optional<Conclusao> conclusao() {
        return Optional.ofNullable(conclusao);
    }

    public boolean exibePontuacaoNoCabecalho() {
        return conclusao == null;
    }
}
