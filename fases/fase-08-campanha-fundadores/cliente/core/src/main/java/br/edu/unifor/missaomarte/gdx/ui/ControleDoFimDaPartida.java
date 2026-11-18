package br.edu.unifor.missaomarte.gdx.ui;

/** Foco horizontal do modal de fim da partida. */
public final class ControleDoFimDaPartida {
    public enum Acao { TENTAR_DE_NOVO, SAIR }

    private final ListaDeBotoes botoes = new ListaDeBotoes(Acao.values().length);

    public void esquerda() {
        botoes.subir();
    }

    public void direita() {
        botoes.descer();
    }

    public void focar(Acao acao) {
        botoes.focar(acao.ordinal());
    }

    public int foco() {
        return botoes.foco();
    }

    public boolean emFoco(Acao acao) {
        return botoes.emFoco(acao.ordinal());
    }

    public Acao acaoEmFoco() {
        return Acao.values()[botoes.foco()];
    }
}
