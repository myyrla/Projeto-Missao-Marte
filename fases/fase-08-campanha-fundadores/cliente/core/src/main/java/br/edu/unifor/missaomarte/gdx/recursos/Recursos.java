package br.edu.unifor.missaomarte.gdx.recursos;

import br.edu.unifor.missaomarte.application.campanha.EstadoDaConstrucao;
import br.edu.unifor.missaomarte.gdx.ui.IconeDaInterface;
import br.edu.unifor.missaomarte.gdx.ui.PecaDaInterface;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.TextureLoader;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Disposable;
import java.util.EnumMap;
import java.util.Map;

/**
 * Carrega, guarda e libera todas as imagens e sons pelo AssetManager.
 * <p>Padrao: Facade (um ponto unico de acesso aos assets). Texturas e sons sao compartilhados (Flyweight informal) e
 * liberados em {@link #dispose()}. O AssetManager e uma instancia do jogo, nao um campo estatico.
 */
public class Recursos implements Disposable {
    private final AssetManager gerenciador = new AssetManager();
    private final Map<Imagem, TextureRegion[]> quadros = new EnumMap<>(Imagem.class);
    private final Map<IconeDaInterface, TextureRegion> iconesDaInterface = new EnumMap<>(IconeDaInterface.class);

    /** Carrega tudo de uma vez (poucos arquivos pequenos); bloqueia ate terminar. */
    public void carregar() {
        for (Imagem imagem : Imagem.values()) {
            gerenciador.load(imagem.caminho(), Texture.class, criarParametrosDaTextura(imagem));
        }
        TextureLoader.TextureParameter parametrosDaTextura = criarParametrosDaTextura();
        for (ImagemDoMapa imagem : ImagemDoMapa.values()) {
            gerenciador.load(imagem.caminho(), Texture.class, criarParametrosDoMapa(imagem));
        }
        for (PecaDaInterface peca : PecaDaInterface.values()) {
            gerenciador.load(peca.caminho(), Texture.class, parametrosDaTextura);
        }
        for (IconeDaInterface icone : IconeDaInterface.values()) {
            gerenciador.load(icone.caminho(), Texture.class, parametrosDaTextura);
        }
        gerenciador.load(CamadaDoCampus.caminhoDaBase(), Texture.class, parametrosDaTextura);
        for (CamadaDoCampus camada : CamadaDoCampus.values()) {
            gerenciador.load(camada.caminhoPlanta(), Texture.class, parametrosDaTextura);
            gerenciador.load(camada.caminhoConstruido(), Texture.class, parametrosDaTextura);
        }
        for (Som som : Som.values()) {
            if (som.musica()) {
                gerenciador.load(som.caminho(), Music.class);
            } else {
                gerenciador.load(som.caminho(), Sound.class);
            }
        }
        gerenciador.finishLoading();

        for (Imagem imagem : Imagem.values()) {
            Texture textura = gerenciador.get(imagem.caminho(), Texture.class);
            TextureRegion[][] grade = TextureRegion.split(textura, imagem.larguraQuadro(), imagem.alturaQuadro());
            quadros.put(imagem, grade[0]);
        }
        for (IconeDaInterface icone : IconeDaInterface.values()) {
            iconesDaInterface.put(icone, new TextureRegion(textura(icone)));
        }
        int totalDeImagens = Imagem.values().length + ImagemDoMapa.values().length
                + PecaDaInterface.values().length + IconeDaInterface.values().length
                + 1 + CamadaDoCampus.values().length * 2;
        Gdx.app.log("Recursos", totalDeImagens + " imagens e " + Som.values().length + " sons carregados");
    }

    public Texture textura(Imagem imagem) {
        return gerenciador.get(imagem.caminho(), Texture.class);
    }

    public Texture textura(ImagemDoMapa imagem) {
        return gerenciador.get(imagem.caminho(), Texture.class);
    }

    public Texture textura(PecaDaInterface peca) {
        return gerenciador.get(peca.caminho(), Texture.class);
    }

    private Texture textura(IconeDaInterface icone) {
        return gerenciador.get(icone.caminho(), Texture.class);
    }

    public TextureRegion icone(IconeDaInterface icone) {
        return iconesDaInterface.get(icone);
    }

    public Texture texturaDaBaseDoCampus() {
        return gerenciador.get(CamadaDoCampus.caminhoDaBase(), Texture.class);
    }

    public Texture textura(CamadaDoCampus camada, EstadoDaConstrucao estado) {
        return gerenciador.get(camada.caminho(estado), Texture.class);
    }

    /** Primeiro quadro da imagem (a imagem inteira quando ela tem um so quadro). */
    public TextureRegion quadro(Imagem imagem) {
        return quadros.get(imagem)[0];
    }

    public TextureRegion[] quadros(Imagem imagem) {
        return quadros.get(imagem);
    }

    public Animation<TextureRegion> animacao(Imagem imagem, float duracaoDoQuadro, PlayMode modo) {
        Animation<TextureRegion> animacao = new Animation<>(duracaoDoQuadro, quadros.get(imagem));
        animacao.setPlayMode(modo);
        return animacao;
    }

    public Sound som(Som som) {
        return gerenciador.get(som.caminho(), Sound.class);
    }

    public Music musica() {
        return gerenciador.get(Som.MUSICA.caminho(), Music.class);
    }

    static TextureLoader.TextureParameter criarParametrosDaTextura() {
        TextureLoader.TextureParameter parametros = new TextureLoader.TextureParameter();
        parametros.genMipMaps = true;
        parametros.minFilter = Texture.TextureFilter.MipMapLinearLinear;
        parametros.magFilter = Texture.TextureFilter.Linear;
        return parametros;
    }

    static TextureLoader.TextureParameter criarParametrosDaTextura(Imagem imagem) {
        TextureLoader.TextureParameter parametros = criarParametrosDaTextura();
        if (imagem == Imagem.POEIRA) {
            parametros.wrapU = Texture.TextureWrap.Repeat;
            parametros.wrapV = Texture.TextureWrap.Repeat;
        }
        return parametros;
    }

    static TextureLoader.TextureParameter criarParametrosDoMapa(ImagemDoMapa imagem) {
        TextureLoader.TextureParameter parametros = criarParametrosDaTextura();
        if (imagem.repetivel()) {
            parametros.wrapU = Texture.TextureWrap.Repeat;
            parametros.wrapV = Texture.TextureWrap.Repeat;
        }
        return parametros;
    }

    @Override
    public void dispose() {
        gerenciador.dispose();
    }
}
