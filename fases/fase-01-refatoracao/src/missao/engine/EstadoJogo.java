package missao.engine;
import java.util.List;
import missao.model.Dificuldade;
/** Fotografia imutável: a interface não recebe entidades mutáveis do domínio. */
public record EstadoJogo(String piloto, Dificuldade dificuldade, int tamanhoMapa,
        int x, int y, int vidas, int capacidade, int pontuacao, int aBordo, int restantes,
        EstadoPartida estado, EstatisticasMissao estatisticas, List<Objeto> objetos, List<String> passageiros) {
    public EstadoJogo { objetos=List.copyOf(objetos); passageiros=List.copyOf(passageiros); }
    public record Objeto(int x,int y,char simbolo) { }
}
