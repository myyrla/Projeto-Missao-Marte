package br.edu.unifor.missaomarte.application.campanha;

public record MecanicasDaEtapa(boolean temEstacaoNami, boolean temTempestade, int perseguidores) {
    public MecanicasDaEtapa {
        if (perseguidores < 0) {
            throw new IllegalArgumentException("Quantidades de mecânicas não podem ser negativas.");
        }
    }
}
