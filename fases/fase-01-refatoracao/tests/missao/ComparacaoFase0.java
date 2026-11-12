package missao;
import java.util.*;
import java.lang.reflect.*;
import missao.engine.MissaoFactory;
public class ComparacaoFase0 {
    static Object chamar(Object o,String nome) throws Exception { return o.getClass().getMethod(nome).invoke(o); }
    static List<String> descrever(Object m) throws Exception {
        List<String> s=new ArrayList<>();
        for(String lista:List.of("getPassageiros","getAsteroides","getInimigos")) {
            for(Object o:(List<?>)chamar(m,lista)) {
                String item=lista+":"+chamar(o,"getX")+","+chamar(o,"getY");
                if(lista.equals("getPassageiros")) item+=":"+chamar(o,"getNome")+":"+chamar(o,"getPontuacao");
                s.add(item);
            }
        }
        return s;
    }
    public static void main(String[] args) throws Exception {
        Class<?> dif=Class.forName("Dificuldade");
        Method criar=Class.forName("Main").getDeclaredMethod("criarNovaMissao",Random.class,int.class,int.class,int.class,int.class,dif);
        criar.setAccessible(true);
        for(var dificuldade:missao.model.Dificuldade.values()) {
            Object originalDif=dif.getMethod("valueOf",String.class).invoke(null,dificuldade.name());
            for(int seed=0;seed<50;seed++) {
                Object original=criar.invoke(null,new Random(seed),-5,5,-5,5,originalDif);
                var equipe=new MissaoFactory().criar(dificuldade,5,new Random(seed));
                if(!descrever(original).equals(descrever(equipe))) throw new AssertionError("Cenário diverge: "+dificuldade+" semente "+seed);
                Random r0=new Random(seed+10),r1=new Random(seed+10);
                for(int turno=0;turno<20;turno++) {
                    original.getClass().getMethod("moverInimigos",Random.class,int.class,int.class,int.class,int.class).invoke(original,r0,-5,5,-5,5);
                    equipe.moverInimigos(r1,-5,5);
                    if(!descrever(original).equals(descrever(equipe))) throw new AssertionError("Inimigos divergem");
                }
            }
        }
        System.out.println("Comparação com Fase 0: 150 cenários e 3000 turnos de inimigos idênticos.");
    }
}
