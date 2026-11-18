package br.edu.unifor.missaomarte.gdx.recursos;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.image.BufferedImage;
import java.nio.file.Files;
import java.nio.file.Paths;
import javax.imageio.ImageIO;
import org.junit.jupiter.api.Test;

class ImagemDoMapaTest {
    @Test
    void terrenoTemCaminhoEscalaLogicaERepeticao() throws Exception {
        ImagemDoMapa terreno = ImagemDoMapa.TERRENO;

        assertEquals("imagens/mapa/terreno_mapa.png", terreno.caminho());
        assertEquals(512f, terreno.larguraLogica(), 0f);
        assertEquals(512f, terreno.alturaLogica(), 0f);
        assertTrue(terreno.repetivel());
        assertTrue(Files.isRegularFile(Paths.get("assets").resolve(terreno.caminho())));
        BufferedImage imagem = ImageIO.read(Paths.get("assets").resolve(terreno.caminho()).toFile());
        assertEquals(1024, imagem.getWidth());
        assertEquals(1024, imagem.getHeight());
    }

    @Test
    void efeitosDoMapaTemCaminhoDimensoesLogicasEDobroDeResolucao() throws Exception {
        verificar(ImagemDoMapa.ALCANCE, "alcance.png", 128f, 128f, 256, 256);
        verificar(ImagemDoMapa.PULSO, "pulso.png", 96f, 96f, 192, 192);
        verificar(ImagemDoMapa.SOMBRA, "sombra.png", 64f, 24f, 128, 48);
        verificar(ImagemDoMapa.RASTRO_PONTO, "rastro_ponto.png", 8f, 8f, 16, 16);
    }

    private static void verificar(ImagemDoMapa imagem, String arquivo,
            float larguraLogica, float alturaLogica, int larguraFisica, int alturaFisica) throws Exception {
        assertEquals("imagens/mapa/" + arquivo, imagem.caminho());
        assertEquals(larguraLogica, imagem.larguraLogica(), 0f);
        assertEquals(alturaLogica, imagem.alturaLogica(), 0f);
        assertFalse(imagem.repetivel());
        java.nio.file.Path caminho = Paths.get("assets").resolve(imagem.caminho());
        assertTrue(Files.isRegularFile(caminho));
        BufferedImage arquivoDeImagem = ImageIO.read(caminho.toFile());
        assertEquals(larguraFisica, arquivoDeImagem.getWidth());
        assertEquals(alturaFisica, arquivoDeImagem.getHeight());
    }
}
