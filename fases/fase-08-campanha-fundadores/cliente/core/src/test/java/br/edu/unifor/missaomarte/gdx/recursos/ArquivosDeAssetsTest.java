package br.edu.unifor.missaomarte.gdx.recursos;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import javax.imageio.ImageIO;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import org.junit.jupiter.api.Test;

/** Garante que o catalogo (enums) e os arquivos em core/assets continuam de acordo, sem precisar de OpenGL ou audio. */
class ArquivosDeAssetsTest {
    private static final Path ASSETS = Paths.get("assets");

    @Test
    void cadaImagemExisteComAsDimensoesDoCatalogo() throws IOException {
        for (Imagem imagem : Imagem.values()) {
            File arquivo = ASSETS.resolve(imagem.caminho()).toFile();
            assertTrue(arquivo.isFile(), "arquivo ausente: " + imagem.caminho());

            BufferedImage pixels = ImageIO.read(arquivo);
            assertNotNull(pixels, "imagem ilegivel: " + imagem.caminho());
            assertEquals(imagem.larguraQuadro() * imagem.quadros(), pixels.getWidth(), "largura de " + imagem.caminho());
            assertEquals(imagem.alturaQuadro(), pixels.getHeight(), "altura de " + imagem.caminho());
        }
    }

    @Test
    void cadaConstrucaoDoCampusPossuiPlantaEImagemConstruida() throws IOException {
        validarImagemDoCampus(CamadaDoCampus.caminhoDaBase());
        for (CamadaDoCampus camada : CamadaDoCampus.values()) {
            validarImagemDoCampus(camada.caminhoPlanta());
            validarImagemDoCampus(camada.caminhoConstruido());
        }
    }

    @Test
    void cadaFontePossuiDescricaoImagemELicenca() throws IOException {
        Map<FonteDoJogo, String> nomes = Map.of(
                FonteDoJogo.TITULO, "titulo",
                FonteDoJogo.SUBTITULO, "subtitulo",
                FonteDoJogo.TEXTO, "texto",
                FonteDoJogo.PEQUENO, "pequeno");
        for (FonteDoJogo tipo : FonteDoJogo.values()) {
            String nome = nomes.get(tipo);
            Path descricao = ASSETS.resolve(tipo.caminho());
            Path imagem = ASSETS.resolve("fontes/" + nome + ".png");
            assertTrue(Files.isRegularFile(descricao), "descricao ausente: " + descricao);
            assertTrue(Files.isRegularFile(imagem), "imagem ausente: " + imagem);
            assertTrue(Files.readString(descricao).contains("file=\"" + nome + ".png\""),
                    "pagina incorreta em " + descricao);
        }
        assertTrue(Files.isRegularFile(ASSETS.resolve("fontes/licencas/OFL-ChakraPetch.txt")));
        assertTrue(Files.isRegularFile(ASSETS.resolve("fontes/licencas/OFL-Inter.txt")));
    }

    @Test
    void cadaSomExisteEEUmWavValido() throws IOException, UnsupportedAudioFileException {
        for (Som som : Som.values()) {
            Path arquivo = ASSETS.resolve(som.caminho());
            assertTrue(Files.size(arquivo) > 44, "arquivo vazio ou ausente: " + som.caminho());

            try (AudioInputStream fluxo = AudioSystem.getAudioInputStream(arquivo.toFile())) {
                assertTrue(fluxo.getFrameLength() > 0, "sem amostras: " + som.caminho());
            }
        }
    }

    @Test
    void existeExatamenteUmaMusica() {
        long musicas = java.util.Arrays.stream(Som.values()).filter(Som::musica).count();

        assertEquals(1, musicas);
        assertTrue(Som.MUSICA.musica());
    }

    private static void validarImagemDoCampus(String caminho) throws IOException {
        File arquivo = ASSETS.resolve(caminho).toFile();
        assertTrue(arquivo.isFile(), "arquivo ausente: " + caminho);

        BufferedImage pixels = ImageIO.read(arquivo);
        assertNotNull(pixels, "imagem ilegivel: " + caminho);
        assertEquals(1056, pixels.getWidth(), "largura de " + caminho);
        assertEquals(864, pixels.getHeight(), "altura de " + caminho);
    }
}
