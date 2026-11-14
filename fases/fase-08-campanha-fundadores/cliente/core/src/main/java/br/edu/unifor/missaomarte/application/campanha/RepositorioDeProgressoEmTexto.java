package br.edu.unifor.missaomarte.application.campanha;

import br.edu.unifor.missaomarte.application.servidor.ArmazemDeTexto;
import java.util.Optional;

public final class RepositorioDeProgressoEmTexto implements RepositorioDeProgresso {
    private static final String CHAVE = "campanha.progresso";
    private static final String VERSAO = "1";

    private final ArmazemDeTexto armazem;

    public RepositorioDeProgressoEmTexto(ArmazemDeTexto armazem) {
        this.armazem = armazem;
    }

    @Override
    public Optional<ProgressoDaCampanha> carregar() {
        String texto = armazem.ler(CHAVE);
        if (texto == null) {
            return Optional.empty();
        }
        String[] partes = texto.split("\\|", -1);
        if (partes.length != 5 || !VERSAO.equals(partes[0])) {
            return Optional.empty();
        }
        try {
            return Optional.of(ProgressoDaCampanha.restaurar(
                    Integer.parseInt(partes[1]), Integer.parseInt(partes[2]),
                    Integer.parseInt(partes[3]), Integer.parseInt(partes[4])));
        } catch (IllegalArgumentException e) {
            return Optional.empty();
        }
    }

    @Override
    public void salvar(ProgressoDaCampanha progresso) {
        String valor = String.join("|", VERSAO, String.valueOf(progresso.etapaAtual()),
                String.valueOf(progresso.melhorPontuacao(1)),
                String.valueOf(progresso.melhorPontuacao(2)),
                String.valueOf(progresso.melhorPontuacao(3)));
        armazem.gravar(CHAVE, valor);
    }

    @Override
    public void apagar() {
        armazem.apagar(CHAVE);
    }
}
