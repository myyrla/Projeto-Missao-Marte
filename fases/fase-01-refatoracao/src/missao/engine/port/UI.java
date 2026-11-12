package missao.engine.port;
import missao.engine.EstadoJogo;
import missao.engine.ResultadoTurno;
/** Porta de saída: nenhuma dependência do console concreto. */
@FunctionalInterface
public interface UI { void apresentar(EstadoJogo estado, ResultadoTurno resultado); }
