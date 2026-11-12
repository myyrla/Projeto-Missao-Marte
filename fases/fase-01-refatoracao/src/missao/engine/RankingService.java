package missao.engine;
import java.util.*;
import missao.engine.port.RankingRepository;
import missao.model.RankingEntry;
/** Política de ranking na aplicação, independente do armazenamento. */
public final class RankingService {
    private final RankingRepository repository;
    public RankingService(RankingRepository repository) { this.repository=repository; }
    public List<RankingEntry> listar() {
        return repository.carregar().stream().sorted(Comparator.comparingInt(RankingEntry::score).reversed()).limit(5).toList();
    }
    public boolean registrar(RankingEntry entrada) {
        List<RankingEntry> itens=new ArrayList<>(listar());
        if (entrada.score()<=0 || (itens.size()==5 && entrada.score()<=itens.get(4).score())) return false;
        itens.add(entrada);
        itens.sort(Comparator.comparingInt(RankingEntry::score).reversed());
        repository.salvar(new ArrayList<>(itens.subList(0,Math.min(5,itens.size()))));
        return true;
    }
    public void resetar() { repository.resetar(); }
}
