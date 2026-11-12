package missao;
import missao.model.*;
public class DominioTest {
    public static void main(String[] args) {
        if (Dificuldade.DIFICIL.getQtdPassageiros() != 5 || Dificuldade.DIFICIL.getPontuacaoInicial() != 45)
            throw new AssertionError("Difícil deve ter 5 passageiros e 45 pontos");
        Nave nave = new Nave("A-1", 5);
        for (int i = 0; i < 5; i++) if (!nave.embarcar(new Professor("P", 0, 0))) throw new AssertionError("Capacidade 5");
        if (nave.embarcar(new Astronauta("A", 0, 0))) throw new AssertionError("Sexto passageiro");
        for (int i = 0; i < 5; i++) nave.perderVidas();
        if (nave.getVidas() != 0) throw new AssertionError("Vidas negativas");
        System.out.println("DominioTest: OK");
    }
}
