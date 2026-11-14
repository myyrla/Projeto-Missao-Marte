package br.edu.unifor.missaomarte.gdx.campanha;

import br.edu.unifor.missaomarte.application.campanha.Campanha;
import br.edu.unifor.missaomarte.application.campanha.CatalogoDaCampanha;
import br.edu.unifor.missaomarte.application.campanha.EtapaDaCampanha;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class ResumoVisualDaCampanha {
    public enum SituacaoDaEtapa { RESGATADA, ATUAL, PENDENTE }

    public record Etapa(int numero, String local, String fundador, SituacaoDaEtapa situacao) {
    }

    private final List<Etapa> etapas;
    private final int fundadoresResgatados;
    private final int pontuacaoTotal;
    private final String rotuloDaAcao;

    private ResumoVisualDaCampanha(List<Etapa> etapas, int fundadoresResgatados,
                                  int pontuacaoTotal, String rotuloDaAcao) {
        this.etapas = Collections.unmodifiableList(etapas);
        this.fundadoresResgatados = fundadoresResgatados;
        this.pontuacaoTotal = pontuacaoTotal;
        this.rotuloDaAcao = rotuloDaAcao;
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
        return new ResumoVisualDaCampanha(etapas, resgatados, campanha.pontuacaoTotal(),
                campanha.concluida() ? "Nova campanha" : "Iniciar missão");
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
}
