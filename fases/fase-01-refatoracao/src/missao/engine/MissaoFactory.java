package missao.engine;
import missao.model.*;
import java.util.Random;
/** GRASP Creator: montagem centralizada com o sorteio e a composição da Fase 0. */
public class MissaoFactory {
    public Missao criar(Dificuldade dificuldade, int tamanhoMapa, Random random) {
        validar(dificuldade, tamanhoMapa);
        long lado = 2L * tamanhoMapa + 1;
        int total = dificuldade.getQtdPassageiros() + dificuldade.getQtdAsteroides() + dificuldade.getQtdInimigos();
        Missao missao = new Missao(new Nave("A-1", 5));
        for (int i=0; i<total; i++) {
            int x, y;
            do {
                x=random.nextInt((int)lado)-tamanhoMapa;
                y=random.nextInt((int)lado)-tamanhoMapa;
            } while (missao.posicaoOcupada(x,y));
            if (i<dificuldade.getQtdPassageiros()) missao.addPassageiro(criarPassageiro(i,x,y));
            else if (i<dificuldade.getQtdPassageiros()+dificuldade.getQtdAsteroides()) missao.addAsteroide(new Asteroide(x,y));
            else missao.addInimigo(new Inimigo(x,y));
        }
        return missao;
    }
    public void validar(Dificuldade dificuldade, int tamanhoMapa) {
        long lado = 2L * tamanhoMapa + 1;
        int total = dificuldade.getQtdPassageiros() + dificuldade.getQtdAsteroides() + dificuldade.getQtdInimigos();
        if (tamanhoMapa < 1 || tamanhoMapa > (Integer.MAX_VALUE - 1) / 2 || lado * lado - 1 < total)
            throw new IllegalArgumentException("Mapa pequeno ou tamanho inválido. Informe outro tamanho.");
    }
    private Passageiro criarPassageiro(int indice,int x,int y) {
        return switch(indice % 5) {
            case 0 -> new Professor("Dr. Silva",x,y);
            case 1 -> new Engenheiro("Eng. Rosa",x,y);
            case 2 -> new Professor("Dr. Lima",x,y);
            case 3 -> new Engenheiro("Eng. Carlos",x,y);
            default -> new Astronauta("Ast. Maria",x,y);
        };
    }
}
