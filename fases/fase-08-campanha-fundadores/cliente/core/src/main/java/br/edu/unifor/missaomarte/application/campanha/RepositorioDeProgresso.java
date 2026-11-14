package br.edu.unifor.missaomarte.application.campanha;

import java.util.Optional;

public interface RepositorioDeProgresso {
    Optional<ProgressoDaCampanha> carregar();

    void salvar(ProgressoDaCampanha progresso);

    void apagar();

    static RepositorioDeProgresso nenhum() {
        return new RepositorioDeProgresso() {
            @Override
            public Optional<ProgressoDaCampanha> carregar() {
                return Optional.empty();
            }

            @Override
            public void salvar(ProgressoDaCampanha progresso) {
            }

            @Override
            public void apagar() {
            }
        };
    }
}
