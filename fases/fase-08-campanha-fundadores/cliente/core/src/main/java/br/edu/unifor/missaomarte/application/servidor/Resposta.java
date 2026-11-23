package br.edu.unifor.missaomarte.application.servidor;

/**
 * Resultado de uma chamada ao servidor: um valor ou uma mensagem de erro (objeto de dados imutavel).
 * <p>{@code naoAutorizado} indica que o servidor recusou o token (sessao expirada), para a tela mandar o jogador entrar de novo.
 */
public final class Resposta<T> {
    private enum TipoDeErro { NENHUM, GERAL, CONEXAO, NAO_AUTORIZADO }

    private final T valor;
    private final String mensagem;
    private final TipoDeErro tipoDeErro;

    private Resposta(T valor, String mensagem, TipoDeErro tipoDeErro) {
        this.valor = valor;
        this.mensagem = mensagem;
        this.tipoDeErro = tipoDeErro;
    }

    public static <T> Resposta<T> sucesso(T valor) {
        return new Resposta<>(valor, null, TipoDeErro.NENHUM);
    }

    public static <T> Resposta<T> erro(String mensagem) {
        return new Resposta<>(null, mensagem, TipoDeErro.GERAL);
    }

    public static <T> Resposta<T> erroDeConexao(String mensagem) {
        return new Resposta<>(null, mensagem, TipoDeErro.CONEXAO);
    }

    public static <T> Resposta<T> erroNaoAutorizado(String mensagem) {
        return new Resposta<>(null, mensagem, TipoDeErro.NAO_AUTORIZADO);
    }

    public boolean isSucesso() {
        return mensagem == null;
    }

    public T getValor() {
        return valor;
    }

    public String getMensagem() {
        return mensagem;
    }

    public boolean isNaoAutorizado() {
        return tipoDeErro == TipoDeErro.NAO_AUTORIZADO;
    }

    public boolean isErroDeConexao() {
        return tipoDeErro == TipoDeErro.CONEXAO;
    }
}
