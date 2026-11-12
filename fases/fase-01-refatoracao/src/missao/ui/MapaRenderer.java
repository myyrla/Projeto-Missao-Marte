package missao.ui;
import missao.engine.*;
/** Somente apresentação: recebe uma fotografia, sem alterar o domínio. */
public final class MapaRenderer {
    public String desenhar(EstadoJogo s) {
        StringBuilder texto=new StringBuilder("\nMapa da Missão (Pontos: "+s.pontuacao()+") - Piloto: "+s.piloto()+"\n    ");
        for(int x=-s.tamanhoMapa();x<=s.tamanhoMapa();x++) texto.append(String.format(" %2d",x));
        texto.append("\n    ");
        for(int x=-s.tamanhoMapa();x<=s.tamanhoMapa();x++) texto.append(" __");
        texto.append('\n');
        for(int y=-s.tamanhoMapa();y<=s.tamanhoMapa();y++) {
            texto.append(String.format("%3d|",y));
            for(int x=-s.tamanhoMapa();x<=s.tamanhoMapa();x++) {
                char simbolo='.';
                for(var o:s.objetos()) if(o.x()==x && o.y()==y) { simbolo=o.simbolo(); break; }
                texto.append(String.format(" %2c",simbolo));
            }
            texto.append('\n');
        }
        texto.append("Legenda: @=Nave, P=Professor, E=Engenheiro, T=Astronauta, #=Asteroide, X=Inimigo, L=Plataforma de Pouso, .=Vazio\n");
        texto.append("Comandos: w/s/a/d (mover), c (embarcar), q (sair)\nPassageiros na superfície marciana:\n");
        for(String p:s.passageiros()) texto.append(" - ").append(p).append('\n');
        texto.append(String.format("Nave em (%d,%d) | Pontos: %d | Vidas: %d | A bordo: %d/%d | Restantes: %d%n",
                s.x(),s.y(),s.pontuacao(),s.vidas(),s.aBordo(),s.capacidade(),s.restantes()));
        return texto.toString();
    }
}
