package util;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Leitor e gerador de JSON simples (sem bibliotecas externas).
 *
 * JSON é o formato de texto que o navegador e o servidor usam para trocar dados, por exemplo:
 *   {"origem": "Centro", "trechos": [{"idTransporte": 1, "distanciaKm": 5.5}]}
 *
 * Ao LER (parse), os tipos Java resultantes são:
 *   objeto -> Map<String, Object>   lista -> List<Object>   texto -> String
 *   número -> Double                verdadeiro/falso -> Boolean   nulo -> null
 * Ao ESCREVER (escrever), aceita os mesmos tipos (mais Integer, Long etc.).
 */
public class Json {

    private final String texto;
    private int posicao = 0;

    private Json(String texto) {
        this.texto = texto;
    }

    // ===================== LER =====================

    public static Object parse(String texto) {
        Json leitor = new Json(texto);
        leitor.pularEspacos();
        Object valor = leitor.lerValor();
        leitor.pularEspacos();
        if (leitor.posicao != texto.length()) {
            throw new IllegalArgumentException("JSON inválido: conteúdo extra no final.");
        }
        return valor;
    }

    private Object lerValor() {
        pularEspacos();
        if (posicao >= texto.length()) {
            throw new IllegalArgumentException("JSON inválido: texto incompleto.");
        }
        char c = texto.charAt(posicao);
        if (c == '{') {
            return lerObjeto();
        }
        if (c == '[') {
            return lerLista();
        }
        if (c == '"') {
            return lerTexto();
        }
        if (texto.startsWith("true", posicao)) {
            posicao += 4;
            return Boolean.TRUE;
        }
        if (texto.startsWith("false", posicao)) {
            posicao += 5;
            return Boolean.FALSE;
        }
        if (texto.startsWith("null", posicao)) {
            posicao += 4;
            return null;
        }
        return lerNumero();
    }

    private Map<String, Object> lerObjeto() {
        Map<String, Object> mapa = new LinkedHashMap<>();
        posicao++; // pula o '{'
        pularEspacos();
        if (espiar() == '}') {
            posicao++;
            return mapa;
        }
        while (true) {
            pularEspacos();
            String chave = lerTexto();
            pularEspacos();
            esperar(':');
            mapa.put(chave, lerValor());
            pularEspacos();
            if (espiar() == ',') {
                posicao++;
            } else {
                esperar('}');
                return mapa;
            }
        }
    }

    private List<Object> lerLista() {
        List<Object> lista = new ArrayList<>();
        posicao++; // pula o '['
        pularEspacos();
        if (espiar() == ']') {
            posicao++;
            return lista;
        }
        while (true) {
            lista.add(lerValor());
            pularEspacos();
            if (espiar() == ',') {
                posicao++;
            } else {
                esperar(']');
                return lista;
            }
        }
    }

    private String lerTexto() {
        esperar('"');
        StringBuilder sb = new StringBuilder();
        while (posicao < texto.length()) {
            char c = texto.charAt(posicao++);
            if (c == '"') {
                return sb.toString();
            }
            if (c == '\\') {
                if (posicao >= texto.length()) {
                    break;
                }
                char e = texto.charAt(posicao++);
                switch (e) {
                    case 'n': sb.append('\n'); break;
                    case 't': sb.append('\t'); break;
                    case 'r': sb.append('\r'); break;
                    case 'b': sb.append('\b'); break;
                    case 'f': sb.append('\f'); break;
                    case 'u':
                        sb.append((char) Integer.parseInt(texto.substring(posicao, posicao + 4), 16));
                        posicao += 4;
                        break;
                    default: sb.append(e); // \" \\ \/
                }
            } else {
                sb.append(c);
            }
        }
        throw new IllegalArgumentException("JSON inválido: texto sem aspas finais.");
    }

    private Double lerNumero() {
        int inicio = posicao;
        while (posicao < texto.length() && "+-0123456789.eE".indexOf(texto.charAt(posicao)) >= 0) {
            posicao++;
        }
        if (inicio == posicao) {
            throw new IllegalArgumentException("JSON inválido na posição " + posicao + ".");
        }
        return Double.valueOf(texto.substring(inicio, posicao));
    }

    private void pularEspacos() {
        while (posicao < texto.length() && Character.isWhitespace(texto.charAt(posicao))) {
            posicao++;
        }
    }

    private char espiar() {
        if (posicao >= texto.length()) {
            throw new IllegalArgumentException("JSON inválido: texto incompleto.");
        }
        return texto.charAt(posicao);
    }

    private void esperar(char esperado) {
        if (espiar() != esperado) {
            throw new IllegalArgumentException("JSON inválido: esperava '" + esperado + "'.");
        }
        posicao++;
    }

    // ===================== ESCREVER =====================

    public static String escrever(Object valor) {
        StringBuilder sb = new StringBuilder();
        escreverValor(sb, valor);
        return sb.toString();
    }

    private static void escreverValor(StringBuilder sb, Object valor) {
        if (valor == null) {
            sb.append("null");
        } else if (valor instanceof String) {
            escreverTexto(sb, (String) valor);
        } else if (valor instanceof Boolean) {
            sb.append(valor);
        } else if (valor instanceof Number) {
            double d = ((Number) valor).doubleValue();
            if (valor instanceof Double || valor instanceof Float) {
                // arredonda para 4 casas, evitando números gigantes como 0.30000000000000004
                sb.append(Math.round(d * 10000.0) / 10000.0);
            } else {
                sb.append(valor);
            }
        } else if (valor instanceof Map) {
            sb.append('{');
            boolean primeiro = true;
            for (Map.Entry<?, ?> e : ((Map<?, ?>) valor).entrySet()) {
                if (!primeiro) {
                    sb.append(',');
                }
                primeiro = false;
                escreverTexto(sb, String.valueOf(e.getKey()));
                sb.append(':');
                escreverValor(sb, e.getValue());
            }
            sb.append('}');
        } else if (valor instanceof Iterable) {
            sb.append('[');
            boolean primeiro = true;
            for (Object item : (Iterable<?>) valor) {
                if (!primeiro) {
                    sb.append(',');
                }
                primeiro = false;
                escreverValor(sb, item);
            }
            sb.append(']');
        } else {
            escreverTexto(sb, valor.toString());
        }
    }

    private static void escreverTexto(StringBuilder sb, String s) {
        sb.append('"');
        for (char c : s.toCharArray()) {
            switch (c) {
                case '"': sb.append("\\\""); break;
                case '\\': sb.append("\\\\"); break;
                case '\n': sb.append("\\n"); break;
                case '\r': sb.append("\\r"); break;
                case '\t': sb.append("\\t"); break;
                default:
                    if (c < 0x20) {
                        sb.append(String.format("\\u%04x", (int) c));
                    } else {
                        sb.append(c);
                    }
            }
        }
        sb.append('"');
    }
}
