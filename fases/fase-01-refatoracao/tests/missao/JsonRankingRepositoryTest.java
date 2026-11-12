package missao;
import missao.persistence.*;
import missao.model.*;
import java.nio.file.*;
import java.util.*;
import java.io.*;
public class JsonRankingRepositoryTest {
    public static void main(String[] args) throws Exception {
        Path dir=Files.createTempDirectory("missao-ranking-"); Path path=dir.resolve("ranking.json");
        try {
            var r=new JsonRankingRepository(path);
            if(!r.carregar().isEmpty()) throw new AssertionError("Arquivo ausente");
            var entrada=new RankingEntry("Ana, \"B\" \\ {C}\n",37,"FACIL",4,"2026-11-12 12:00:00",12);
            r.salvar(List.of(entrada));
            if(!r.carregar().equals(List.of(entrada))) throw new AssertionError("Roundtrip de strings e duração");
            Files.writeString(path,"[{\"name\":\"Original\",\"score\":40,\"dificuldade\":\"MEDIO\",\"passageirosColetados\":5,\"dataHora\":\"hoje\",\"tempoJogo\":8}]");
            if(!r.carregar().get(0).nome().equals("Original") || r.carregar().get(0).tempoJogo()!=8) throw new AssertionError("Formato da Fase 0");
            Files.writeString(path,"[{\"nome\":\"Legado\",\"score\":30}]");
            if(r.carregar().get(0).tempoJogo()!=0) throw new AssertionError("Duração legada");
            Files.writeString(path,"JSON inválido");
            try { r.carregar(); throw new AssertionError("Corrupção silenciosa"); } catch(UncheckedIOException esperado) { }
            r.resetar(); if(Files.exists(path)) throw new AssertionError("Reset");
            var impossivel=new JsonRankingRepository(dir.resolve("ausente/ranking.json"));
            try { impossivel.salvar(List.of(entrada)); throw new AssertionError("Falha de IO escondida"); } catch(UncheckedIOException esperado) { }
        } finally { Files.deleteIfExists(path); Files.deleteIfExists(dir); }
        System.out.println("JsonRankingRepositoryTest: OK");
    }
}
