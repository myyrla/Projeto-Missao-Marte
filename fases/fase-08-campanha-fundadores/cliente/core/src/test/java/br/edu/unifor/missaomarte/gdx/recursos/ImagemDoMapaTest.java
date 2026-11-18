package br.edu.unifor.missaomarte.gdx.recursos;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
        assertTrue(terreno.repetivel());
        assertTrue(Files.isRegularFile(Paths.get("assets").resolve(terreno.caminho())));
        BufferedImage imagem = ImageIO.read(Paths.get("assets").resolve(terreno.caminho()).toFile());
        assertEquals(1024, imagem.getWidth());
        assertEquals(1024, imagem.getHeight());
    }
}
