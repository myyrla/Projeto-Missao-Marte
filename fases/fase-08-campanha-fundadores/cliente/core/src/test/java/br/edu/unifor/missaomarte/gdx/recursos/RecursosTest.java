package br.edu.unifor.missaomarte.gdx.recursos;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.badlogic.gdx.assets.loaders.TextureLoader;
import com.badlogic.gdx.graphics.Texture;
import org.junit.jupiter.api.Test;

class RecursosTest {
    @Test
    void configuraMipmapsParaReduzirAsTexturasComSuavidade() {
        TextureLoader.TextureParameter parametros = Recursos.criarParametrosDaTextura();

        assertTrue(parametros.genMipMaps);
        assertEquals(Texture.TextureFilter.MipMapLinearLinear, parametros.minFilter);
        assertEquals(Texture.TextureFilter.Linear, parametros.magFilter);
    }

    @Test
    void configuraTerrenoDoMapaParaRepetirSemEmenda() {
        TextureLoader.TextureParameter parametros = Recursos.criarParametrosDoMapa(ImagemDoMapa.TERRENO);

        assertEquals(Texture.TextureWrap.Repeat, parametros.wrapU);
        assertEquals(Texture.TextureWrap.Repeat, parametros.wrapV);
    }
}
