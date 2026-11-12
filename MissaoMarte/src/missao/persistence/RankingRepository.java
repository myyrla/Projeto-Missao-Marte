package missao.persistence;
import missao.model.RankingEntry;
import java.util.List;

public interface RankingRepository {
    List<RankingEntry> carregar();

    void salvar(List<RankingEntry> rankings);

    void resetar();
}
