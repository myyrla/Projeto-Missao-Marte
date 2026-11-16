package br.edu.unifor.missaomarte.gdx.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.image.BufferedImage;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.imageio.ImageIO;
import org.junit.jupiter.api.Test;

class ContratoDaInterfaceTest {
    private static final Path ASSETS = Paths.get("assets");

    @Test
    void pecasPossuemArquivosDimensoesECortesCompativeis() throws Exception {
        Map<PecaDaInterface, DadosDaPeca> esperadas = new LinkedHashMap<>();
        esperadas.put(PecaDaInterface.PAINEL, new DadosDaPeca("painel.png", 64, 64, 26));
        esperadas.put(PecaDaInterface.BOTAO, new DadosDaPeca("botao.png", 64, 64, 22));
        esperadas.put(PecaDaInterface.BOTAO_FOCO, new DadosDaPeca("botao_foco.png", 64, 64, 22));
        esperadas.put(PecaDaInterface.CHIP, new DadosDaPeca("chip.png", 64, 64, 22));
        esperadas.put(PecaDaInterface.CHIP_BORDA, new DadosDaPeca("chip_borda.png", 64, 64, 22));
        esperadas.put(PecaDaInterface.TECLA, new DadosDaPeca("tecla.png", 48, 48, 16));
        esperadas.put(PecaDaInterface.SEGMENTO, new DadosDaPeca("segmento.png", 24, 24, 8));
        esperadas.put(PecaDaInterface.BARRA_FUNDO, new DadosDaPeca("barra_fundo.png", 24, 24, 10));

        for (Map.Entry<PecaDaInterface, DadosDaPeca> entrada : esperadas.entrySet()) {
            PecaDaInterface peca = entrada.getKey();
            DadosDaPeca esperada = entrada.getValue();
            String caminho = peca.caminho();
            assertEquals("imagens/ui/" + esperada.arquivo(), caminho);
            assertEquals(esperada.corte(), peca.corte());

            BufferedImage imagem = ImageIO.read(ASSETS.resolve(caminho).toFile());
            assertEquals(esperada.largura(), imagem.getWidth(), "largura de " + caminho);
            assertEquals(esperada.altura(), imagem.getHeight(), "altura de " + caminho);
            assertTrue(esperada.corte() * 2 < esperada.largura(), "corte horizontal invalido em " + caminho);
            assertTrue(esperada.corte() * 2 < esperada.altura(), "corte vertical invalido em " + caminho);
        }
    }

    @Test
    void iconesPossuemArquivosDe48PixelsETamanhoLogicoDe24() throws Exception {
        Map<IconeDaInterface, String> esperados = Map.of(
                IconeDaInterface.PROFESSOR, "icone_professor.png",
                IconeDaInterface.ENGENHEIRO, "icone_engenheiro.png",
                IconeDaInterface.ASTRONAUTA, "icone_astronauta.png",
                IconeDaInterface.CHECK, "icone_check.png",
                IconeDaInterface.CADEADO, "icone_cadeado.png",
                IconeDaInterface.SETA, "icone_seta.png");

        for (Map.Entry<IconeDaInterface, String> entrada : esperados.entrySet()) {
            IconeDaInterface icone = entrada.getKey();
            String caminho = icone.caminho();
            assertEquals("imagens/ui/" + entrada.getValue(), caminho);
            assertTrue(Files.isRegularFile(ASSETS.resolve(caminho)), "arquivo ausente: " + caminho);
            BufferedImage imagem = ImageIO.read(ASSETS.resolve(caminho).toFile());
            assertEquals(48, imagem.getWidth(), "largura de " + caminho);
            assertEquals(48, imagem.getHeight(), "altura de " + caminho);
            assertEquals(24f, icone.tamanhoLogico(), 0f);
        }
        assertEquals(esperados.size(), IconeDaInterface.values().length);
    }

    @Test
    void teclaReservaMargensHorizontaisEMantemLarguraMinima() {
        assertEquals(32f, EstiloDaInterface.larguraDaTecla(0f), 0f);
        assertEquals(36f, EstiloDaInterface.larguraDaTecla(20f), 0f);
        assertEquals(80f, EstiloDaInterface.larguraDaTecla(64f), 0f);
    }

    private record DadosDaPeca(String arquivo, int largura, int altura, int corte) {
    }
}
