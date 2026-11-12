package missao.suporte;
import missao.engine.port.RankingRepository;
import missao.model.RankingEntry;
import java.util.*;
public class RankingRepositoryEmMemoria implements RankingRepository {
    private List<RankingEntry> dados = new ArrayList<>();
    public List<RankingEntry> carregar() { return new ArrayList<>(dados); }
    public void salvar(List<RankingEntry> itens) { dados=new ArrayList<>(itens); }
    public void resetar() { dados.clear(); }
}
