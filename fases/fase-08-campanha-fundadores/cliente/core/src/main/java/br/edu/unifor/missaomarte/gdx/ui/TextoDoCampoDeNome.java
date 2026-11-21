package br.edu.unifor.missaomarte.gdx.ui;

/** Conteúdo visual do campo de nome, separado da regra que inicia a partida. */
public record TextoDoCampoDeNome(String conteudo, boolean exemplo) {
    public static TextoDoCampoDeNome de(boolean logado, String nomeDaConta,
            String nomeDigitado, String cursor) {
        if (logado) {
            return new TextoDoCampoDeNome(nomeDaConta, false);
        }
        if (nomeDigitado.isEmpty()) {
            return new TextoDoCampoDeNome("Toque para digitar", true);
        }
        return new TextoDoCampoDeNome(nomeDigitado + cursor, false);
    }
}
