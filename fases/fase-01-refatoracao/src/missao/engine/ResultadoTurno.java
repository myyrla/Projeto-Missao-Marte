package missao.engine;
import java.util.List;
public record ResultadoTurno(EstadoPartida estado, List<String> mensagens) {
    public ResultadoTurno { mensagens=List.copyOf(mensagens); }
}
