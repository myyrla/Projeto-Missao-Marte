package br.edu.unifor.missaomarte.gdx.campanha;

import java.util.ArrayList;
import java.util.List;

/** Cálculos tipográficos puros usados pelo campus em qualquer densidade. */
public final class MetricasTipograficasDoCampus {
    private MetricasTipograficasDoCampus() {
    }

    public static float alturaEmLinhas(float alturaDoLayout, float alturaDaMaiuscula,
            float alturaDaLinha) {
        if (alturaDoLayout <= 0f || alturaDaLinha <= 0f) {
            return 0f;
        }
        int quantidade = Math.max(1,
                Math.round((alturaDoLayout - alturaDaMaiuscula) / alturaDaLinha) + 1);
        return quantidade * alturaDaLinha;
    }

    public static float escalar(float valorBase, float alturaDaLinha,
            float alturaDaLinhaBase) {
        if (alturaDaLinha <= 0f || alturaDaLinhaBase <= 0f) {
            return valorBase;
        }
        return valorBase * alturaDaLinha / alturaDaLinhaBase;
    }

    public static MedidasDoCartao medidasDoCartao(float alturaDoLayoutDoTitulo,
            float alturaDaMaiusculaDoTitulo, float alturaDaLinhaDoTitulo,
            List<Float> alturasDosLayoutsDoConteudo, float alturaDaMaiusculaDoTexto,
            float alturaDaLinhaDoTexto) {
        List<Float> alturas = new ArrayList<>();
        for (float alturaDoLayout : alturasDosLayoutsDoConteudo) {
            alturas.add(alturaEmLinhas(alturaDoLayout, alturaDaMaiusculaDoTexto,
                    alturaDaLinhaDoTexto));
        }
        return new MedidasDoCartao(
                escalar(11f, alturaDaLinhaDoTexto, 21f),
                alturaEmLinhas(alturaDoLayoutDoTitulo, alturaDaMaiusculaDoTitulo,
                        alturaDaLinhaDoTitulo),
                escalar(4f, alturaDaLinhaDoTexto, 21f),
                alturas,
                escalar(6f, alturaDaLinhaDoTexto, 21f),
                escalar(11f, alturaDaLinhaDoTexto, 21f));
    }
}
