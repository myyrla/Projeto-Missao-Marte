package missao.model;

import java.util.ArrayList;
import java.util.List;

public class Nave implements EntidadeMapa {

    private String id;
    private int x;
    private int y;
    private int vidas;
    private int capacidade;
    private List<Passageiro> passageiros = new ArrayList<>();

    public Nave(String id, int capacidade) {
        this.id = id;
        this.x = 0;
        this.y = 0;
        this.vidas = 3;
        this.capacidade = capacidade;
        this.passageiros = new ArrayList<>();
    }

    public String getId() {
        return id;
    }

    public int getVidas() {
        return vidas;
    }

    public int getCapacidade() {
        return capacidade;
    }

    public List<Passageiro> getPassageiros() {
        return List.copyOf(passageiros);
    }

    public void moveUp(){
        this.y--;
    }
    public void moveDown(){
        this.y++;
    }
    public void moveLeft(){
        this.x--;
    }
    public void moveRight(){
        this.x++;
    }

    public void perderVidas(){
        if (vidas > 0){
            this.vidas--;
        }
    }

    public boolean embarcar (Passageiro passageiro){
        if (passageiros.size() >= capacidade){
            return false;
        }
        passageiros.add(passageiro);
        return true;
    }

    @Override
    public int getX() {
        return x;
    }

    @Override
    public int getY() {
        return y;
    }

    @Override
    public char getSimbolo(){
        return '@';
    }
}
