package br.edu.unifor.missaomarte.application.campanha;

import br.edu.unifor.missaomarte.domain.Dificuldade;
import br.edu.unifor.missaomarte.application.ModoDeJogo;

public record EtapaDaCampanha(int numero, String local, Fundador fundador,
                              Dificuldade dificuldade, ModoDeJogo modo, MecanicasDaEtapa mecanicas) {
}
