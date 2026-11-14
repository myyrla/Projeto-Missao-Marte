package br.edu.unifor.missaomarte.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import br.edu.unifor.missaomarte.application.campanha.CatalogoDaCampanha;
import br.edu.unifor.missaomarte.application.campanha.EtapaDaCampanha;
import br.edu.unifor.missaomarte.application.campanha.Fundador;
import br.edu.unifor.missaomarte.application.campanha.MecanicasDaEtapa;
import br.edu.unifor.missaomarte.application.campanha.PreparacaoDaEtapa;
import br.edu.unifor.missaomarte.domain.Astronauta;
import br.edu.unifor.missaomarte.domain.Dificuldade;
import br.edu.unifor.missaomarte.domain.Engenheiro;
import br.edu.unifor.missaomarte.domain.Missao;
import br.edu.unifor.missaomarte.domain.ObjetoRenderizavel;
import br.edu.unifor.missaomarte.domain.Passageiro;
import br.edu.unifor.missaomarte.domain.Posicao;
import br.edu.unifor.missaomarte.domain.Professor;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import org.junit.jupiter.api.Test;

class MissaoFactoryCampanhaTest {
    private static final int MIN = -5;
    private static final int MAX = 5;

    @Test
    void cadaEtapaGaranteOFundadorEAsMecanicasAprovadas() {
        MissaoFactory fabrica = new MissaoFactory();
        PreparacaoDaEtapa etapa1 = fabrica.criarEtapaDaCampanha(CatalogoDaCampanha.etapa(1), MIN, MAX, new Random(10));
        PreparacaoDaEtapa etapa2 = fabrica.criarEtapaDaCampanha(CatalogoDaCampanha.etapa(2), MIN, MAX, new Random(20));
        PreparacaoDaEtapa etapa3 = fabrica.criarEtapaDaCampanha(CatalogoDaCampanha.etapa(3), MIN, MAX, new Random(30));

        assertFundador(etapa1.missao(), Astronauta.class, "Fundador Astronauta", Dificuldade.FACIL);
        assertEquals(0, perseguidores(etapa1.missao()));
        assertTrue(etapa1.missao().getEstacoesNami().isEmpty());

        assertFundador(etapa2.missao(), Professor.class, "Fundador Professor", Dificuldade.MEDIO);
        assertEquals(1, perseguidores(etapa2.missao()));
        assertEquals(1, etapa2.missao().getEstacoesNami().size());

        assertFundador(etapa3.missao(), Engenheiro.class, "Fundador Engenheiro", Dificuldade.DIFICIL);
        assertEquals(2, perseguidores(etapa3.missao()));
        assertEquals(1, etapa3.missao().getEstacoesNami().size());
        assertEquals(4, etapa3.missao().getParadasDoRover().size());
        assertTrue(etapa3.mecanicas().temTempestade());
    }

    @Test
    void quantidadeDePerseguidoresVemDaConfiguracaoDaEtapa() {
        EtapaDaCampanha etapa = new EtapaDaCampanha(
                1, "Teste", Fundador.ASTRONAUTA, Dificuldade.FACIL, ModoDeJogo.DINAMICO,
                new MecanicasDaEtapa(false, 0, false, 3));

        Missao missao = new MissaoFactory()
                .criarEtapaDaCampanha(etapa, MIN, MAX, new Random(91)).missao();

        assertEquals(3, perseguidores(missao));
    }

    @Test
    void elementosDaCampanhaNaoSeSobrepoemESaoAlcancaveisDaBase() {
        Missao missao = new MissaoFactory().criarEtapaDaCampanha(
                CatalogoDaCampanha.etapa(3), MIN, MAX, new Random(44)).missao();
        List<ObjetoRenderizavel> todos = new ArrayList<>(todosOsObjetos(missao));
        todos.remove(missao.getNave());
        Set<Posicao> posicoes = new HashSet<>();

        for (ObjetoRenderizavel objeto : todos) {
            assertTrue(posicoes.add(objeto.getPosicao()), "posição repetida: " + objeto.getPosicao());
        }
        missao.getEstacoesNami().forEach(nami -> assertTrue(alcancavel(missao, nami.getPosicao())));
        missao.getParadasDoRover().forEach(parada -> assertTrue(alcancavel(missao, parada.getPosicao())));
    }

    @Test
    void mesmaSementeProduzOMesmoMapaDeCampanha() {
        MissaoFactory fabrica = new MissaoFactory();
        Missao primeira = fabrica.criarEtapaDaCampanha(CatalogoDaCampanha.etapa(3), MIN, MAX, new Random(77)).missao();
        Missao segunda = fabrica.criarEtapaDaCampanha(CatalogoDaCampanha.etapa(3), MIN, MAX, new Random(77)).missao();

        assertEquals(assinatura(primeira), assinatura(segunda));
    }

    @Test
    void partidaRapidaMantemAssinaturaDaFaseSete() {
        Missao missao = new MissaoFactory().criarMissao(
                br.edu.unifor.missaomarte.domain.Dificuldade.MEDIO, MIN, MAX, new Random(4), ModoDeJogo.DINAMICO);

        assertEquals("*-5,-5;*-3,-3;*-3,4;*-1,2;*0,-3;*0,4;*2,-3;*2,3;A-3,3;A-2,1;"
                + "B0,0;C5,4;N0,0;P-2,-2;P2,-5;P2,-4;P4,-3;X-5,3;X-3,-5;X-2,5;", assinatura(missao));
        assertTrue(missao.getEstacoesNami().isEmpty());
        assertTrue(missao.getParadasDoRover().isEmpty());
    }

    private static long perseguidores(Missao missao) {
        return missao.getInimigos().stream().filter(inimigo -> inimigo.persegue()).count();
    }

    private static void assertFundador(Missao missao, Class<? extends Passageiro> tipo,
                                       String nome, Dificuldade dificuldade) {
        assertEquals(RegrasDificuldade.obter(dificuldade).getQuantidadePassageiros(),
                missao.getPassageiros().size());
        assertEquals(1, missao.getPassageiros().stream()
                .filter(passageiro -> tipo.isInstance(passageiro) && nome.equals(passageiro.getNome()))
                .count());
    }

    private static List<ObjetoRenderizavel> todosOsObjetos(Missao missao) {
        List<ObjetoRenderizavel> objetos = new ArrayList<>();
        objetos.add(missao.getBase());
        objetos.add(missao.getNave());
        objetos.addAll(missao.getPassageiros());
        objetos.addAll(missao.getAsteroides());
        objetos.addAll(missao.getInimigos());
        objetos.addAll(missao.getEstacoesNami());
        objetos.addAll(missao.getParadasDoRover());
        return objetos;
    }

    private static boolean alcancavel(Missao missao, Posicao destino) {
        Set<Posicao> bloqueadas = new HashSet<>();
        missao.getAsteroides().forEach(a -> bloqueadas.add(a.getPosicao()));
        missao.getInimigos().forEach(i -> bloqueadas.add(i.getPosicao()));
        List<Posicao> fila = new ArrayList<>();
        Set<Posicao> visitadas = new HashSet<>();
        fila.add(new Posicao(0, 0));
        visitadas.add(new Posicao(0, 0));
        for (int indice = 0; indice < fila.size(); indice++) {
            Posicao atual = fila.get(indice);
            if (atual.equals(destino)) {
                return true;
            }
            for (int[] d : List.of(new int[]{1, 0}, new int[]{-1, 0}, new int[]{0, 1}, new int[]{0, -1})) {
                Posicao proxima = atual.mover(d[0], d[1], MIN, MAX);
                if (!bloqueadas.contains(proxima) && visitadas.add(proxima)) {
                    fila.add(proxima);
                }
            }
        }
        return false;
    }

    private static String assinatura(Missao missao) {
        StringBuilder texto = new StringBuilder();
        todosOsObjetos(missao).stream()
                .sorted((a, b) -> {
                    int simbolo = Character.compare(a.getSimbolo(), b.getSimbolo());
                    if (simbolo != 0) return simbolo;
                    int x = Integer.compare(a.getPosicao().getX(), b.getPosicao().getX());
                    return x != 0 ? x : Integer.compare(a.getPosicao().getY(), b.getPosicao().getY());
                })
                .forEach(o -> texto.append(o.getSimbolo()).append(o.getPosicao().getX())
                        .append(',').append(o.getPosicao().getY()).append(';'));
        return texto.toString();
    }
}
