package br.edu.unifor.missaomarte.application;

import br.edu.unifor.missaomarte.application.servidor.Resposta;

/** Registra uma vitória rápida localmente quando o servidor fica inacessível, sem duplicar tentativas. */
public final class RegistroAlternativoNoRankingLocal {
    private boolean tentativaConcluida;

    public boolean registrarSeNecessario(boolean campanha, GameSession sessao, Resposta<?> resposta,
            RankingService ranking) {
        if (tentativaConcluida || campanha || sessao.getEstado() != EstadoPartida.VITORIA
                || !resposta.isErroDeConexao()) {
            return false;
        }
        tentativaConcluida = true;
        return ranking.registrarVitoria(sessao);
    }
}
