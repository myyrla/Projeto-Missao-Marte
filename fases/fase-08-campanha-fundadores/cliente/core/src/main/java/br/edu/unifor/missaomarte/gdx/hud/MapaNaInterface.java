package br.edu.unifor.missaomarte.gdx.hud;

import br.edu.unifor.missaomarte.gdx.layout.RetanguloDeTela;

/** Projeta a parte visivel do mapa da camera do mundo para o viewport da interface. */
public final class MapaNaInterface {
    private MapaNaInterface() {
    }

    public static RetanguloDeTela projetar(RetanguloDeTela mapaNoMundo, RetanguloDeTela areaVisivelDoMundo,
            float larguraDaInterface, float alturaDaInterface) {
        float escalaX = larguraDaInterface / areaVisivelDoMundo.largura();
        float escalaY = alturaDaInterface / areaVisivelDoMundo.altura();
        float x = (mapaNoMundo.x() - areaVisivelDoMundo.x()) * escalaX;
        float y = (mapaNoMundo.y() - areaVisivelDoMundo.y()) * escalaY;
        float direita = (mapaNoMundo.direita() - areaVisivelDoMundo.x()) * escalaX;
        float topo = (mapaNoMundo.topo() - areaVisivelDoMundo.y()) * escalaY;
        float xRecortado = limitar(x, 0f, larguraDaInterface);
        float yRecortado = limitar(y, 0f, alturaDaInterface);
        float direitaRecortada = limitar(direita, 0f, larguraDaInterface);
        float topoRecortado = limitar(topo, 0f, alturaDaInterface);
        return new RetanguloDeTela(xRecortado, yRecortado,
                Math.max(0f, direitaRecortada - xRecortado),
                Math.max(0f, topoRecortado - yRecortado));
    }

    private static float limitar(float valor, float minimo, float maximo) {
        return Math.max(minimo, Math.min(maximo, valor));
    }
}
