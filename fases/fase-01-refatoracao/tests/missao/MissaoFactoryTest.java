package missao;
import missao.engine.*;
import missao.model.*;
import java.util.*;
public class MissaoFactoryTest {
    public static void main(String[] args) {
        MissaoFactory f = new MissaoFactory();
        int[][] esperado = {{4,1,1},{5,2,2},{5,3,3}};
        for (Dificuldade d : Dificuldade.values()) {
            Missao m = f.criar(d, 5, new Random(42));
            int[] e = esperado[d.ordinal()];
            check(m.getPassageiros().size()==e[0] && m.getAsteroides().size()==e[1] && m.getInimigos().size()==e[2], "Quantidades");
            Set<String> pos = new HashSet<>(); pos.add("0,0");
            List<EntidadeMapa> itens = new ArrayList<>(); itens.addAll(m.getPassageiros()); itens.addAll(m.getAsteroides()); itens.addAll(m.getInimigos());
            for (EntidadeMapa o : itens) check(pos.add(o.getX()+","+o.getY()), "Sobreposição");
            check(m.getNave().getCapacidade()==5, "Capacidade");
            check(m.getPassageiros().stream().map(Passageiro::getSimbolo).toList().equals(d==Dificuldade.FACIL ? List.of('P','E','P','E') : List.of('P','E','P','E','T')), "Composição original");
            Missao outra=f.criar(d,5,new Random(42));
            check(m.entidades().stream().map(o -> o.getX()+","+o.getY()+":"+o.getSimbolo()).toList().equals(outra.entidades().stream().map(o -> o.getX()+","+o.getY()+":"+o.getSimbolo()).toList()), "Semente");
        }
        try { f.criar(Dificuldade.MEDIO,1,new Random()); throw new AssertionError("Mapa pequeno aceito"); }
        catch (IllegalArgumentException esperadoErro) { }
        f.criar(Dificuldade.FACIL,1,new Random(42));
        Nave n=new Nave("A",5); n.moverComLimites(-1,0,0,2); check(n.getX()==0,"Limite esquerdo");
        n.moverComLimites(1,0,0,2); check(n.getX()==1,"Movimento");
        System.out.println("MissaoFactoryTest: OK");
    }
    static void check(boolean ok,String mensagem) { if(!ok) throw new AssertionError(mensagem); }
}
