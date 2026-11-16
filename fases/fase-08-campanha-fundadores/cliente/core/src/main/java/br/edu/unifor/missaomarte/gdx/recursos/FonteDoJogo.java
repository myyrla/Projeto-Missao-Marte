package br.edu.unifor.missaomarte.gdx.recursos;

/** Catalogo das fontes e dos papeis tipograficos usados pela interface. */
public enum FonteDoJogo {
    DISPLAY("fontes/display.fnt"),
    TITULO("fontes/titulo.fnt"),
    SUBTITULO("fontes/subtitulo.fnt"),
    TEXTO("fontes/texto.fnt"),
    PEQUENO("fontes/pequeno.fnt"),
    ROTULO("fontes/rotulo.fnt");

    private final String caminho;

    FonteDoJogo(String caminho) {
        this.caminho = caminho;
    }

    public String caminho() {
        return caminho;
    }
}
