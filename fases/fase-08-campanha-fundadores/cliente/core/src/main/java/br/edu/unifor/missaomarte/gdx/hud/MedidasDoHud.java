package br.edu.unifor.missaomarte.gdx.hud;

/** Medidas tipograficas usadas pelo layout puro do HUD. */
public record MedidasDoHud(float alturaDoRotulo, float alturaDoTextoDoObjetivo,
                           float alturaDoStatus, int quantidadeDeAtalhos, float alturaDoAviso,
                           boolean avisoVisivel, boolean tempestadeVisivel,
                           float alturaDaLinhaDoTexto, float alturaDaLinhaPequena,
                           float alvoMinimo) {
    public MedidasDoHud(float alturaDoRotulo, float alturaDoTextoDoObjetivo,
            float alturaDoStatus, int quantidadeDeAtalhos, float alturaDoAviso) {
        this(alturaDoRotulo, alturaDoTextoDoObjetivo, alturaDoStatus,
                quantidadeDeAtalhos, alturaDoAviso, alturaDoAviso > 0f, false,
                alturaDoTextoDoObjetivo, alturaDoRotulo, 0f);
    }

    public MedidasDoHud(float alturaDoRotulo, float alturaDoTextoDoObjetivo,
            float alturaDoStatus, int quantidadeDeAtalhos, float alturaDoAviso,
            boolean avisoVisivel, boolean tempestadeVisivel) {
        this(alturaDoRotulo, alturaDoTextoDoObjetivo, alturaDoStatus,
                quantidadeDeAtalhos, alturaDoAviso, avisoVisivel, tempestadeVisivel,
                alturaDoTextoDoObjetivo, alturaDoRotulo, 0f);
    }

    public MedidasDoHud comAlvoMinimo(float novoAlvoMinimo) {
        return new MedidasDoHud(alturaDoRotulo, alturaDoTextoDoObjetivo, alturaDoStatus,
                quantidadeDeAtalhos, alturaDoAviso, avisoVisivel, tempestadeVisivel,
                alturaDaLinhaDoTexto, alturaDaLinhaPequena, novoAlvoMinimo);
    }

    public static MedidasDoHud padrao(float escala) {
        return new MedidasDoHud(14f * escala, 14f * escala, 28f * escala,
                5, 18f * escala, false, false, 14f * escala, 12f * escala, 0f);
    }
}
