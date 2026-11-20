package br.edu.unifor.missaomarte.gdx.visual;

import br.edu.unifor.missaomarte.domain.Asteroide;
import br.edu.unifor.missaomarte.domain.Base;
import br.edu.unifor.missaomarte.domain.EstacaoNami;
import br.edu.unifor.missaomarte.domain.Inimigo;
import br.edu.unifor.missaomarte.domain.Missao;
import br.edu.unifor.missaomarte.domain.Nave;
import br.edu.unifor.missaomarte.domain.Passageiro;
import br.edu.unifor.missaomarte.domain.Posicao;
import java.util.List;
import java.util.function.Predicate;

/** Projeção visual da missão durante a tempestade, sem alterar as coleções do domínio. */
public record CenaVisivelDaTempestade(
        Base base,
        Nave nave,
        List<Passageiro> passageiros,
        List<Asteroide> asteroides,
        List<Inimigo> inimigos,
        List<EstacaoNami> estacoesNami) {

    public static CenaVisivelDaTempestade projetar(
            Missao missao, boolean tempestadeAtiva, Predicate<Posicao> estaVisivel) {
        if (!tempestadeAtiva) {
            return new CenaVisivelDaTempestade(missao.getBase(), missao.getNave(),
                    List.copyOf(missao.getPassageiros()), List.copyOf(missao.getAsteroides()),
                    List.copyOf(missao.getInimigos()), List.copyOf(missao.getEstacoesNami()));
        }
        return new CenaVisivelDaTempestade(missao.getBase(), missao.getNave(),
                VisibilidadeDaTempestade.filtrar(missao.getPassageiros(), estaVisivel),
                VisibilidadeDaTempestade.filtrar(missao.getAsteroides(), estaVisivel),
                VisibilidadeDaTempestade.filtrar(missao.getInimigos(), estaVisivel),
                VisibilidadeDaTempestade.filtrar(missao.getEstacoesNami(), estaVisivel));
    }
}
