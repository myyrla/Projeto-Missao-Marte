package br.edu.unifor.missaomarte.gdx.campanha;

import java.util.List;

/** Alturas e posições do conteúdo textual de um cartão do campus. */
public record MedidasDoCartao(float margemSuperior, float alturaDoTitulo,
                              float espacoAposTitulo, List<Float> alturasDoConteudo,
                              float espacoEntreConteudos, float margemInferior) {
    public MedidasDoCartao {
        alturasDoConteudo = List.copyOf(alturasDoConteudo);
    }

    public float alturaTotal() {
        float total = margemSuperior + alturaDoTitulo + espacoAposTitulo + margemInferior;
        for (float altura : alturasDoConteudo) {
            total += altura;
        }
        total += Math.max(0, alturasDoConteudo.size() - 1) * espacoEntreConteudos;
        return total;
    }

    public float topoDoTitulo(float topoDoCartao) {
        return topoDoCartao - margemSuperior;
    }

    public float topoDoConteudo(float topoDoCartao, int indice) {
        float topo = topoDoTitulo(topoDoCartao) - alturaDoTitulo - espacoAposTitulo;
        for (int i = 0; i < indice; i++) {
            topo -= alturasDoConteudo.get(i) + espacoEntreConteudos;
        }
        return topo;
    }
}
