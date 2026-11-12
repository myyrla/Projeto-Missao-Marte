package missao.model;

public final class Colisao {
    private Colisao() {

    }
    public static boolean entre(EntidadeMapa a, EntidadeMapa b) {
        return a.getX() == b.getX() && a.getY() == b.getY();
    }
}
