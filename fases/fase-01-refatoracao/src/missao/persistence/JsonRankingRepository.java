package missao.persistence;
import missao.engine.port.RankingRepository;
import missao.model.RankingEntry;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;
/** Adaptador JSON específico do ranking, sem apresentação e sem dependências externas. */
public final class JsonRankingRepository implements RankingRepository {
    private final Path path;
    public JsonRankingRepository(Path path) { this.path=path; }
    public List<RankingEntry> carregar() {
        try {
            String texto=Files.readString(path,StandardCharsets.UTF_8).trim();
            if(texto.isEmpty()) return new ArrayList<>();
            return new Leitor(texto).ler();
        } catch(NoSuchFileException e) { return new ArrayList<>(); }
        catch(IOException e) { throw new UncheckedIOException(e); }
    }
    public void salvar(List<RankingEntry> itens) {
        StringJoiner json=new StringJoiner(",","[","]");
        for(RankingEntry e:itens) json.add("{\"name\":"+texto(e.nome())+",\"score\":"+e.score()
                +",\"dificuldade\":"+texto(e.dificuldade())+",\"passageirosColetados\":"+e.passageirosColetados()
                +",\"dataHora\":"+texto(e.dataHora())+",\"tempoJogo\":"+e.tempoJogo()+"}");
        try { Files.writeString(path,json.toString(),StandardCharsets.UTF_8); }
        catch(IOException e) { throw new UncheckedIOException(e); }
    }
    public void resetar() {
        try { Files.deleteIfExists(path); } catch(IOException e) { throw new UncheckedIOException(e); }
    }
    private static String texto(String valor) {
        StringBuilder s=new StringBuilder("\"");
        for(char c:valor.toCharArray()) {
            switch(c) {
                case '"' -> s.append("\\\"");
                case '\\' -> s.append("\\\\");
                case '\n' -> s.append("\\n");
                case '\r' -> s.append("\\r");
                case '\t' -> s.append("\\t");
                default -> { if(c<32) s.append(String.format("\\u%04x",(int)c)); else s.append(c); }
            }
        }
        return s.append('"').toString();
    }
    /** Parser do formato plano: delimitadores dentro de strings nunca separam campos. */
    private static final class Leitor {
        private final String json;
        private int pos;
        Leitor(String json) { this.json=json; }
        List<RankingEntry> ler() throws IOException {
            List<RankingEntry> lista=new ArrayList<>(); exigir('[');
            if(!aceitar(']')) {
                do {
                    Map<String,String> campos=new HashMap<>(); exigir('{');
                    if(!aceitar('}')) {
                        do { String chave=string(); exigir(':'); campos.put(chave,valor()); } while(aceitar(','));
                        exigir('}');
                    }
                    String nome=campos.getOrDefault("name",campos.get("nome"));
                    try {
                        if(nome!=null && campos.containsKey("score")) lista.add(new RankingEntry(nome,Integer.parseInt(campos.get("score")),
                                campos.getOrDefault("dificuldade","MEDIO"),Integer.parseInt(campos.getOrDefault("passageirosColetados","0")),
                                campos.getOrDefault("dataHora",""),Long.parseLong(campos.getOrDefault("tempoJogo","0"))));
                    } catch(NumberFormatException e) { throw erro(); }
                } while(aceitar(','));
                exigir(']');
            }
            espacos(); if(pos!=json.length()) throw erro();
            return lista;
        }
        private String valor() throws IOException {
            espacos(); if(pos<json.length() && json.charAt(pos)=='"') return string();
            int inicio=pos;
            if(pos<json.length() && json.charAt(pos)=='-') pos++;
            int digitos=pos;
            while(pos<json.length() && Character.isDigit(json.charAt(pos))) pos++;
            if(pos==digitos) throw erro();
            return json.substring(inicio,pos);
        }
        private String string() throws IOException {
            exigir('"'); StringBuilder s=new StringBuilder();
            while(pos<json.length()) {
                char c=json.charAt(pos++);
                if(c=='"') return s.toString();
                if(c<32) throw erro();
                if(c!='\\') { s.append(c); continue; }
                if(pos>=json.length()) throw erro();
                char escape=json.charAt(pos++);
                switch(escape) {
                    case '"', '\\', '/' -> s.append(escape);
                    case 'n' -> s.append('\n'); case 'r' -> s.append('\r'); case 't' -> s.append('\t');
                    case 'b' -> s.append('\b'); case 'f' -> s.append('\f');
                    case 'u' -> {
                        if(pos+4>json.length()) throw erro();
                        try { s.append((char)Integer.parseInt(json.substring(pos,pos+4),16)); }
                        catch(NumberFormatException e) { throw erro(); }
                        pos+=4;
                    }
                    default -> throw erro();
                }
            }
            throw erro();
        }
        private void espacos() { while(pos<json.length() && Character.isWhitespace(json.charAt(pos))) pos++; }
        private boolean aceitar(char c) { espacos(); if(pos<json.length() && json.charAt(pos)==c) { pos++; return true; } return false; }
        private void exigir(char c) throws IOException { if(!aceitar(c)) throw erro(); }
        private IOException erro() { return new IOException("Ranking JSON inválido na posição "+pos); }
    }
}
