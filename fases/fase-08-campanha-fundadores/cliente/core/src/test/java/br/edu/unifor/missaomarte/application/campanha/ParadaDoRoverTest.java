package br.edu.unifor.missaomarte.application.campanha;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import br.edu.unifor.missaomarte.domain.Nave;
import br.edu.unifor.missaomarte.domain.Inimigo;
import br.edu.unifor.missaomarte.domain.Missao;
import br.edu.unifor.missaomarte.domain.ParadaDoRover;
import br.edu.unifor.missaomarte.domain.Posicao;
import java.util.List;
import org.junit.jupiter.api.Test;

class ParadaDoRoverTest {
    @Test
    void entrarEmUmaParadaLevaAParadaParSemVoltarNoMesmoPasso() {
        ParadaDoRover a = new ParadaDoRover("A", new Posicao(1, 0), new Posicao(4, 0));
        ParadaDoRover b = new ParadaDoRover("A", new Posicao(4, 0), new Posicao(1, 0));
        RedeDoRover rede = new RedeDoRover(List.of(a, b));
        Nave nave = new Nave(1, 0, 3, 5);

        assertTrue(rede.transportar(nave));
        assertEquals(new Posicao(4, 0), nave.getPosicao());
        assertFalse(rede.transportar(nave));
        assertEquals(new Posicao(4, 0), nave.getPosicao());
    }

    @Test
    void podeUsarOParNovamenteDepoisDeSairDaParada() {
        ParadaDoRover a = new ParadaDoRover("A", new Posicao(1, 0), new Posicao(4, 0));
        ParadaDoRover b = new ParadaDoRover("A", new Posicao(4, 0), new Posicao(1, 0));
        RedeDoRover rede = new RedeDoRover(List.of(a, b));
        Nave nave = new Nave(1, 0, 3, 5);
        rede.transportar(nave);

        nave.moverComLimites(0, 1, -5, 5);
        assertFalse(rede.transportar(nave));
        nave.moverComLimites(0, -1, -5, 5);

        assertTrue(rede.transportar(nave));
        assertEquals(new Posicao(1, 0), nave.getPosicao());
    }

    @Test
    void inimigoSobreUmaParadaNaoETeleportado() {
        Nave nave = new Nave(0, 0, 3, 5);
        Inimigo inimigo = new Inimigo(4, 0);
        Missao missao = new Missao(nave);
        missao.adicionarInimigo(inimigo);
        missao.adicionarParadaDoRover(new ParadaDoRover("A", new Posicao(1, 0), new Posicao(4, 0)));
        missao.adicionarParadaDoRover(new ParadaDoRover("A", new Posicao(4, 0), new Posicao(1, 0)));
        MecanicasDaPartida mecanicas = MecanicasDaPartida.para(missao, false);

        mecanicas.aposMovimentoDaNave();

        assertEquals(new Posicao(4, 0), inimigo.getPosicao());
    }
}
