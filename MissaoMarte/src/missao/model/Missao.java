package missao.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Missao {

    private Nave nave;
    private List<Passageiro> passageiros;
    private List<Asteroide> asteroides;
    private List<Inimigo> inimigos;

    public Missao(Nave nave) {
        this.nave = nave;
        this.passageiros = new ArrayList<>();
        this.asteroides = new ArrayList<>();
        this.inimigos = new ArrayList<>();
    }

    public Nave getNave() {
        return nave;
    }

    public List<Passageiro> getPassageiros() {
        return List.copyOf(passageiros);
    }

    public List<Asteroide> getAsteroides() {
        return List.copyOf(asteroides);
    }

    public List<Inimigo> getInimigos() {
        return List.copyOf(inimigos);
    }

    public void addPassageiro(Passageiro passageiro) {
        passageiros.add(passageiro);
    }

    public void addAsteroide(Asteroide asteroide) {
        asteroides.add(asteroide);
    }

    public void addInimigo(Inimigo inimigo) {
        inimigos.add(inimigo);
    }

    public boolean verificaColisao() {
        for (Asteroide a : asteroides) {
            if (Colisao.entre(nave, a)) {
                return true;
            }
        }
        for (Inimigo i : inimigos) {
            if (Colisao.entre(nave, i)) {
                return true;
            }
        }
        return false;
    }

    public boolean embarcarPassageiroNaPosicao(){
        Iterator<Passageiro> passageiroIterator = passageiros.iterator();
        while (passageiroIterator.hasNext()) {
            Passageiro passageiro = passageiroIterator.next();

            if (passageiro.getX() == nave.getX() && passageiro.getY() == nave.getY()) {
                boolean embarcou = nave.embarcar(passageiro);
                if (embarcou){
                    passageiroIterator.remove();
                }
                return embarcou;
            }
        }
        return false;
    }

    public boolean todosEmbarcados(){
        return passageiros.isEmpty();
    }
}