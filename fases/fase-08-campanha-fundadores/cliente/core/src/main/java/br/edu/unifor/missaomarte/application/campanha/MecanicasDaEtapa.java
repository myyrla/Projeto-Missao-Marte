package br.edu.unifor.missaomarte.application.campanha;

public record MecanicasDaEtapa(boolean temEstacaoNami, int paresDoRover,
                               boolean temTempestade, int perseguidores) {
    public MecanicasDaEtapa {
        if (paresDoRover < 0 || perseguidores < 0) {
            throw new IllegalArgumentException("Quantidades de mecânicas não podem ser negativas.");
        }
    }
}
