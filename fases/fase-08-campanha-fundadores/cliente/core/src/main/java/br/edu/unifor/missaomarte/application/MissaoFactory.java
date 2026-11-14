package br.edu.unifor.missaomarte.application;

import br.edu.unifor.missaomarte.domain.Asteroide;
import br.edu.unifor.missaomarte.domain.Astronauta;
import br.edu.unifor.missaomarte.domain.Dificuldade;
import br.edu.unifor.missaomarte.domain.Engenheiro;
import br.edu.unifor.missaomarte.domain.Inimigo;
import br.edu.unifor.missaomarte.domain.Missao;
import br.edu.unifor.missaomarte.domain.MovimentoPerseguidor;
import br.edu.unifor.missaomarte.domain.Nave;
import br.edu.unifor.missaomarte.domain.Passageiro;
import br.edu.unifor.missaomarte.domain.Professor;
import br.edu.unifor.missaomarte.domain.EstacaoNami;
import br.edu.unifor.missaomarte.domain.ParadaDoRover;
import br.edu.unifor.missaomarte.domain.Posicao;
import br.edu.unifor.missaomarte.application.campanha.EtapaDaCampanha;
import br.edu.unifor.missaomarte.application.campanha.Fundador;
import br.edu.unifor.missaomarte.application.campanha.MecanicasDaPartida;
import br.edu.unifor.missaomarte.application.campanha.PreparacaoDaEtapa;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

/**
 * Monta a Missao conforme a dificuldade, sem sobrepor elementos no mapa.
 * <p>Padrao: Factory. GRASP: Creator (cria Nave, Passageiros e Perigos), Pure Fabrication, High Cohesion.
 * <p>SOLID: SRP (so cria). A capacidade da nave acompanha a quantidade de passageiros da dificuldade.
 * <p>Limite OCP: criarPassageiroAleatorio escolhe o tipo com if.
 * <p>Fase 7: no modo dinamico a nave recebe municao e entram os perseguidores da dificuldade, longe da posicao inicial da nave.
 * O modo classico faz exatamente os mesmos sorteios das fases anteriores.
 */
public class MissaoFactory {
    private static final int RAIO_LIVRE_AO_REDOR_DA_NAVE = 2;

    public Missao criarMissao(Dificuldade dificuldade, int min, int max, Random random) {
        return criarMissao(dificuldade, min, max, random, ModoDeJogo.CLASSICO);
    }

    public Missao criarMissao(Dificuldade dificuldade, int min, int max, Random random, ModoDeJogo modo) {
        int quantidadeDePerseguidores = modo == ModoDeJogo.DINAMICO
                ? RegrasDeTempoReal.obter(dificuldade).getQuantidadeDePerseguidores() : 0;
        return criarMissao(dificuldade, min, max, random, modo, quantidadeDePerseguidores);
    }

    private Missao criarMissao(Dificuldade dificuldade, int min, int max, Random random,
                               ModoDeJogo modo, int quantidadeDePerseguidores) {
        ConfiguracaoDificuldade cfg = RegrasDificuldade.obter(dificuldade);
        Nave nave = new Nave(0, 0, 3, cfg.getQuantidadePassageiros());
        Missao missao = new Missao(nave);
        Set<String> ocupadas = new HashSet<>();
        ocupadas.add(chave(0, 0));

        for (int i = 0; i < cfg.getQuantidadePassageiros(); i++) {
            int[] pos = novaPosicaoLivre(min, max, random, ocupadas);
            Passageiro passageiro = criarPassageiroAleatorio(i, pos[0], pos[1], random);
            missao.adicionarPassageiro(passageiro);
            ocupadas.add(chave(pos[0], pos[1]));
        }

        for (int i = 0; i < cfg.getQuantidadeAsteroides(); i++) {
            int[] pos = novaPosicaoLivre(min, max, random, ocupadas);
            missao.adicionarAsteroide(new Asteroide(pos[0], pos[1]));
            ocupadas.add(chave(pos[0], pos[1]));
        }

        for (int i = 0; i < cfg.getQuantidadeInimigos(); i++) {
            int[] pos = novaPosicaoLivre(min, max, random, ocupadas);
            missao.adicionarInimigo(new Inimigo(pos[0], pos[1]));
            ocupadas.add(chave(pos[0], pos[1]));
        }

        if (modo == ModoDeJogo.DINAMICO) {
            ConfiguracaoDeTempoReal tempoReal = RegrasDeTempoReal.obter(dificuldade);
            nave.carregar(tempoReal.getMunicaoInicial());
            reservarAreaDaNave(ocupadas);
            for (int i = 0; i < quantidadeDePerseguidores; i++) {
                int[] pos = novaPosicaoLivre(min, max, random, ocupadas);
                missao.adicionarInimigo(new Inimigo(pos[0], pos[1], MovimentoPerseguidor.INSTANCIA));
                ocupadas.add(chave(pos[0], pos[1]));
            }
        }

        return missao;
    }

    // Impede que um perseguidor nasca encostado na nave (ela sai da base, em 0,0).
    private void reservarAreaDaNave(Set<String> ocupadas) {
        for (int x = -RAIO_LIVRE_AO_REDOR_DA_NAVE; x <= RAIO_LIVRE_AO_REDOR_DA_NAVE; x++) {
            for (int y = -RAIO_LIVRE_AO_REDOR_DA_NAVE; y <= RAIO_LIVRE_AO_REDOR_DA_NAVE; y++) {
                ocupadas.add(chave(x, y));
            }
        }
    }

    public int pontuacaoInicial(Dificuldade dificuldade) {
        return RegrasDificuldade.obter(dificuldade).getPontuacaoInicial();
    }

    public PreparacaoDaEtapa criarEtapaDaCampanha(EtapaDaCampanha etapa, int min, int max, Random random) {
        Missao missao = criarMissao(etapa.dificuldade(), min, max, random, etapa.modo(),
                etapa.mecanicas().perseguidores());
        garantirFundador(missao, etapa.fundador());

        Set<String> ocupadas = posicoesOcupadas(missao);
        if (etapa.mecanicas().temEstacaoNami()) {
            Posicao posicao = novaPosicaoEspecial(min, max, random, ocupadas, missao);
            missao.adicionarEstacaoNami(new EstacaoNami(posicao.getX(), posicao.getY()));
            ocupadas.add(chave(posicao.getX(), posicao.getY()));
        }
        for (int par = 0; par < etapa.mecanicas().paresDoRover(); par++) {
            Posicao primeira = novaPosicaoEspecial(min, max, random, ocupadas, missao);
            ocupadas.add(chave(primeira.getX(), primeira.getY()));
            Posicao segunda = novaPosicaoEspecial(min, max, random, ocupadas, missao);
            ocupadas.add(chave(segunda.getX(), segunda.getY()));
            String identificador = String.valueOf((char) ('A' + par));
            missao.adicionarParadaDoRover(new ParadaDoRover(identificador, primeira, segunda));
            missao.adicionarParadaDoRover(new ParadaDoRover(identificador, segunda, primeira));
        }
        MecanicasDaPartida mecanicas = MecanicasDaPartida.para(missao, etapa.mecanicas().temTempestade());
        return new PreparacaoDaEtapa(missao, mecanicas);
    }

    private void garantirFundador(Missao missao, Fundador fundador) {
        Passageiro atual = missao.getPassageiros().get(0);
        Posicao posicao = atual.getPosicao();
        Passageiro substituto;
        switch (fundador) {
            case ASTRONAUTA:
                substituto = new Astronauta("Fundador Astronauta", posicao.getX(), posicao.getY());
                break;
            case PROFESSOR:
                substituto = new Professor("Fundador Professor", posicao.getX(), posicao.getY());
                break;
            default:
                substituto = new Engenheiro("Fundador Engenheiro", posicao.getX(), posicao.getY());
                break;
        }
        missao.substituirPassageiro(atual, substituto);
    }

    private Set<String> posicoesOcupadas(Missao missao) {
        Set<String> ocupadas = new HashSet<>();
        ocupadas.add(chave(0, 0));
        missao.getPassageiros().forEach(p -> ocupadas.add(chave(p.getX(), p.getY())));
        missao.getAsteroides().forEach(a -> ocupadas.add(chave(a.getPosicao().getX(), a.getPosicao().getY())));
        missao.getInimigos().forEach(i -> ocupadas.add(chave(i.getPosicao().getX(), i.getPosicao().getY())));
        return ocupadas;
    }

    private Posicao novaPosicaoEspecial(int min, int max, Random random, Set<String> ocupadas, Missao missao) {
        int tentativasMaximas = (max - min + 1) * (max - min + 1) * 4;
        for (int tentativa = 0; tentativa < tentativasMaximas; tentativa++) {
            Posicao candidata = new Posicao(random.nextInt(max - min + 1) + min, random.nextInt(max - min + 1) + min);
            if (!ocupadas.contains(chave(candidata.getX(), candidata.getY()))
                    && estaAlcancavel(candidata, missao, min, max)) {
                return candidata;
            }
        }
        throw new IllegalStateException("Não foi possível posicionar uma mecânica alcançável.");
    }

    private boolean estaAlcancavel(Posicao destino, Missao missao, int min, int max) {
        Set<Posicao> bloqueadas = new HashSet<>();
        missao.getAsteroides().forEach(a -> bloqueadas.add(a.getPosicao()));
        missao.getInimigos().forEach(i -> bloqueadas.add(i.getPosicao()));
        Deque<Posicao> fila = new ArrayDeque<>();
        Set<Posicao> visitadas = new HashSet<>();
        Posicao base = new Posicao(0, 0);
        fila.add(base);
        visitadas.add(base);
        int[][] direcoes = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};
        while (!fila.isEmpty()) {
            Posicao atual = fila.removeFirst();
            if (atual.equals(destino)) {
                return true;
            }
            for (int[] direcao : direcoes) {
                Posicao proxima = atual.mover(direcao[0], direcao[1], min, max);
                if (!bloqueadas.contains(proxima) && visitadas.add(proxima)) {
                    fila.addLast(proxima);
                }
            }
        }
        return false;
    }

    private Passageiro criarPassageiroAleatorio(int indice, int x, int y, Random random) {
        int tipo = random.nextInt(3);
        String sufixo = String.valueOf(indice + 1);

        if (tipo == 0) {
            return new Professor("Prof. " + sufixo, x, y);
        }
        if (tipo == 1) {
            return new Engenheiro("Eng. " + sufixo, x, y);
        }
        return new Astronauta("Astro " + sufixo, x, y);
    }

    // Sorteia ate achar uma celula fora do Set de ocupadas (base, passageiros e perigos ja criados).
    private int[] novaPosicaoLivre(int min, int max, Random random, Set<String> ocupadas) {
        int tentativasMaximas = (max - min + 1) * (max - min + 1) * 2;
        for (int tentativa = 0; tentativa < tentativasMaximas; tentativa++) {
            int x = random.nextInt(max - min + 1) + min;
            int y = random.nextInt(max - min + 1) + min;
            String key = chave(x, y);
            if (!ocupadas.contains(key)) {
                return new int[]{x, y};
            }
        }
        throw new IllegalStateException("Nao foi possivel encontrar posicao livre no mapa.");
    }

    private String chave(int x, int y) {
        return x + "," + y;
    }
}
