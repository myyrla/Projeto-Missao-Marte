package br.edu.unifor.missaomarte.application.campanha;

public enum Construcao {
    TORRE_DE_COMUNICACAO(Fundador.ASTRONAUTA),
    ZONAS_DE_POUSO(Fundador.ASTRONAUTA),
    BLOCOS_DE_AULA(Fundador.PROFESSOR),
    BIBLIOTECA(Fundador.PROFESSOR),
    PREFEITURA(Fundador.ENGENHEIRO),
    NAMI(Fundador.ENGENHEIRO),
    REDE_DO_ROVER(Fundador.ENGENHEIRO),
    REITORIA(null);

    private final Fundador responsavel;

    Construcao(Fundador responsavel) {
        this.responsavel = responsavel;
    }

    public Fundador responsavel() {
        return responsavel;
    }
}
