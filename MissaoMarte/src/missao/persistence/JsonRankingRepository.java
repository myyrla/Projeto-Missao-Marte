package missao.persistence;

import missao.model.RankingEntry;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class JsonRankingRepository implements RankingRepository {

    private final Path path;

    public JsonRankingRepository(Path path) {
        this.path = path;
    }

    @Override
    public List<RankingEntry> carregar() {

        if (!Files.exists(path)) {
            return new ArrayList<>();
        }

        try {
            String json = Files.readString(path, StandardCharsets.UTF_8).trim();
            return parseJson(json);

        } catch (IOException e) {
            System.out.println("Não foi possível carregar o ranking: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    @Override
    public void salvar(List<RankingEntry> rankings) {

        StringBuilder builder = new StringBuilder("[");

        for (int i = 0; i < rankings.size(); i++) {
            RankingEntry entry = rankings.get(i);

            builder.append("{\"nome\":\"").append(escapeJson(entry.nome()))
                    .append("\",\"score\":").append(entry.score())
                    .append(",\"dificuldade\":\"").append(escapeJson(entry.dificuldade()))
                    .append("\",\"passageirosColetados\":").append(entry.passageirosColetados())
                    .append(",\"dataHora\":\"").append(escapeJson(entry.dataHora()))
                    .append("\"}");

            if (i < rankings.size() - 1) {
                builder.append(",");
            }
        }

        builder.append("]");

        try {
            Files.writeString(path, builder.toString(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            System.out.println("Não foi possível salvar o ranking: " + e.getMessage());
        }
    }

    @Override
    public void resetar() {
        try {
            Files.deleteIfExists(path);
        } catch (IOException e) {
            System.out.println("Não foi possível resetar o ranking: " + e.getMessage());
        }
    }

    // =========================================================
    // Parsing (privado — detalhe interno desta classe)
    // =========================================================

    private List<RankingEntry> parseJson(String json) {

        List<RankingEntry> resultado = new ArrayList<>();

        if (json.isBlank() || json.equals("[]")) {
            return resultado;
        }

        if (json.startsWith("[")) json = json.substring(1);
        if (json.endsWith("]")) json = json.substring(0, json.length() - 1);

        int index = 0;

        while (index < json.length()) {
            int inicio = json.indexOf('{', index);
            if (inicio < 0) break;

            int fim = json.indexOf('}', inicio);
            if (fim < 0) break;

            String objeto = json.substring(inicio + 1, fim);
            RankingEntry entry = parseObjeto(objeto);

            if (entry != null) {
                resultado.add(entry);
            }

            index = fim + 1;
        }

        return resultado;
    }

    private RankingEntry parseObjeto(String objeto) {

        String nome = null;
        Integer score = null;
        String dificuldade = "N/A";
        int passageirosColetados = 0;
        String dataHora = "N/A";

        for (String parte : objeto.split(",")) {
            String[] par = parte.split(":", 2);
            if (par.length != 2) continue;

            String chave = par[0].trim().replace("\"", "");
            String valor = par[1].trim();

            switch (chave) {
                case "nome" -> nome = limpar(valor);
                case "score" -> score = parseIntSeguro(valor);
                case "dificuldade" -> dificuldade = limpar(valor);
                case "passageirosColetados" -> passageirosColetados = parseIntSeguro(valor) != null ? parseIntSeguro(valor) : 0;
                case "dataHora" -> dataHora = limpar(valor);
                default -> { }
            }
        }

        if (nome == null || score == null) {
            return null;
        }

        return new RankingEntry(nome, score, dificuldade, passageirosColetados, dataHora);
    }

    private Integer parseIntSeguro(String valor) {
        try {
            return Integer.parseInt(valor);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private String limpar(String valor) {
        if (valor.startsWith("\"") && valor.endsWith("\"")) {
            valor = valor.substring(1, valor.length() - 1);
        }
        return valor.replace("\\\"", "\"").replace("\\\\", "\\");
    }

    private String escapeJson(String valor) {
        return valor.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}