package br.edu.unifor.missaomarte.gdx.visual;

import br.edu.unifor.missaomarte.gdx.layout.RetanguloDeTela;

/** Retangulo de scissor em pixels do backbuffer, calculado sem dependencias do libGDX. */
public record RecorteDoMundo(int x, int y, int largura, int altura) {
    public static RecorteDoMundo calcular(RetanguloDeTela area, float larguraDoMundo, float alturaDoMundo,
            int viewportX, int viewportY, int larguraDoViewport, int alturaDoViewport,
            int larguraDaJanela, int alturaDaJanela, int larguraDoBackbuffer, int alturaDoBackbuffer) {
        float esquerdaNaJanela = viewportX + area.x() / larguraDoMundo * larguraDoViewport;
        float baseNaJanela = viewportY + area.y() / alturaDoMundo * alturaDoViewport;
        float direitaNaJanela = viewportX + area.direita() / larguraDoMundo * larguraDoViewport;
        float topoNaJanela = viewportY + area.topo() / alturaDoMundo * alturaDoViewport;
        float escalaX = larguraDoBackbuffer / (float) Math.max(1, larguraDaJanela);
        float escalaY = alturaDoBackbuffer / (float) Math.max(1, alturaDaJanela);
        int x = limitar((int) Math.floor(esquerdaNaJanela * escalaX), 0, larguraDoBackbuffer);
        int y = limitar((int) Math.floor(baseNaJanela * escalaY), 0, alturaDoBackbuffer);
        int direita = limitar((int) Math.ceil(direitaNaJanela * escalaX), x, larguraDoBackbuffer);
        int topo = limitar((int) Math.ceil(topoNaJanela * escalaY), y, alturaDoBackbuffer);
        return new RecorteDoMundo(x, y, direita - x, topo - y);
    }

    public boolean contem(int px, int py) {
        return px >= x && px <= x + largura && py >= y && py <= y + altura;
    }

    private static int limitar(int valor, int minimo, int maximo) {
        return Math.max(minimo, Math.min(maximo, valor));
    }
}
