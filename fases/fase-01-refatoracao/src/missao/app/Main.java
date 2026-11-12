package missao.app;
import missao.engine.*;
import missao.persistence.JsonRankingRepository;
import missao.ui.*;
import java.nio.file.Path;
import java.time.Clock;
import java.util.*;
/** Composition root: único ponto que monta as implementações concretas. */
public class Main {
    public static void main(String[] args) {
        try(Scanner entrada=new Scanner(System.in)) {
            ConsoleUI ui=new ConsoleUI(entrada,System.out,new MapaRenderer());
            GameEngine motor=new GameEngine(new MissaoFactory(),new JsonRankingRepository(Path.of("ranking.json")),ui,new Random(),Clock.systemDefaultZone());
            ui.executar(motor);
        }
    }
}
