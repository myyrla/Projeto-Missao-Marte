package br.edu.unifor.missaomarte.gdx.hud;

/** Métricas tipográficas ativas usadas pelo resultado da partida no toque. */
public record MedidasDoResultadoDaPartida(float larguraDoTitulo, float alturaDoTitulo,
                                          float alturaDoMotivo, float alturaDoRotulo,
                                          float alturaDosPontos, float alturaDaSituacao,
                                          float larguraDoPrimeiroBotao,
                                          float larguraDoSegundoBotao,
                                          float alturaDoTextoDoBotao,
                                          float alvoMinimo) {
}
