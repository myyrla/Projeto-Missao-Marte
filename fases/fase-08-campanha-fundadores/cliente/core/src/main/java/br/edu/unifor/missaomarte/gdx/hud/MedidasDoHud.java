package br.edu.unifor.missaomarte.gdx.hud;

/** Medidas tipograficas usadas pelo layout puro do HUD. */
public record MedidasDoHud(float alturaDoRotulo, float alturaDoTextoDoObjetivo,
                           float alturaDoStatus, int quantidadeDeAtalhos, float alturaDoAviso,
                           boolean avisoVisivel, boolean tempestadeVisivel) {
    public MedidasDoHud(float alturaDoRotulo, float alturaDoTextoDoObjetivo,
            float alturaDoStatus, int quantidadeDeAtalhos, float alturaDoAviso) {
        this(alturaDoRotulo, alturaDoTextoDoObjetivo, alturaDoStatus,
                quantidadeDeAtalhos, alturaDoAviso, alturaDoAviso > 0f, false);
    }

    public static MedidasDoHud padrao(float escala) {
        return new MedidasDoHud(14f * escala, 14f * escala, 28f * escala,
                5, 18f * escala, false, false);
    }
}
