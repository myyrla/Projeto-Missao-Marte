package missao.model;
/** Plataforma de pouso, sem regra de desembarque. */
public final class Base implements EntidadeMapa {
    public int getX() { return 0; }
    public int getY() { return 0; }
    public char getSimbolo() { return 'L'; }
}
