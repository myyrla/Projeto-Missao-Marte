package missao;
public class TodosOsTestes {
    public static void main(String[] args) throws Exception {
        DominioTest.main(args); MissaoFactoryTest.main(args); GameEngineTest.main(args);
        RankingServiceTest.main(args); JsonRankingRepositoryTest.main(args); ConsoleUITest.main(args);
        System.out.println("6 grupos de testes aprovados.");
    }
}
