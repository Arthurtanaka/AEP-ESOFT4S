package controller;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import dao.DaoException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import model.Usuario;
import service.RegraNegocioException;
import service.RotaService;
import service.SessaoService;
import util.Json;

/**
 * PILAR DE POO: CLASSE ABSTRATA + HERANÇA + POLIMORFISMO.
 *
 * Todo controller da API (RotaController, TransporteController...) HERDA desta classe.
 * Ela já resolve o que é comum a todos: ler o JSON do pedido, devolver JSON na resposta,
 * descobrir quem está logado e transformar erros em respostas HTTP com mensagem clara.
 *
 * Cada subclasse só precisa escrever o método abstrato tratar() com @Override, dizendo o que fazer
 * para cada tipo de pedido (GET, POST, PUT, DELETE) do seu recurso.
 */
public abstract class BaseController implements HttpHandler {

    protected final SessaoService sessaoService;

    protected BaseController(SessaoService sessaoService) {
        this.sessaoService = sessaoService;
    }

    /**
     * Implementado por cada controller.
     *
     * @param metodo "GET", "POST", "PUT" ou "DELETE"
     * @param partes pedaços do caminho depois do recurso. Ex.: no pedido DELETE /api/rotas/3,
     *               o recurso é "rotas" e partes = ["3"]
     */
    protected abstract void tratar(HttpExchange troca, String metodo, String[] partes) throws IOException;

    /** Chamado pelo servidor a cada pedido. Aqui ficam o roteamento comum e o tratamento de erros. */
    @Override
    public void handle(HttpExchange troca) throws IOException {
        try {
            String contexto = troca.getHttpContext().getPath();              // ex.: /api/rotas
            String caminho = troca.getRequestURI().getPath();                // ex.: /api/rotas/3
            String resto = caminho.length() > contexto.length() ? caminho.substring(contexto.length()) : "";
            String[] partes = resto.isEmpty() || resto.equals("/") ? new String[0]
                    : resto.substring(1).split("/");
            tratar(troca, troca.getRequestMethod().toUpperCase(), partes);

        } catch (RegraNegocioException e) {
            // erro "esperado": mensagem pensada para o usuário
            responderErro(troca, e.getStatus(), e.getMessage());
        } catch (DaoException e) {
            System.err.println("[ERRO DE BANCO] " + e.getMessage() + " -> " + e.getCause());
            responderErro(troca, 500, "Erro ao acessar o banco de dados. Tente novamente.");
        } catch (IllegalArgumentException e) {
            responderErro(troca, 400, "Dados inválidos: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            responderErro(troca, 500, "Erro inesperado no servidor.");
        } finally {
            troca.close();
        }
    }

    // ===================== Respostas =====================

    protected void responder(HttpExchange troca, int status, Object corpo) throws IOException {
        byte[] bytes = Json.escrever(corpo).getBytes(StandardCharsets.UTF_8);
        troca.getResponseHeaders().set("Content-Type", "application/json; charset=utf-8");
        troca.sendResponseHeaders(status, bytes.length);
        try (OutputStream saida = troca.getResponseBody()) {
            saida.write(bytes);
        }
    }

    protected void responderErro(HttpExchange troca, int status, String mensagem) throws IOException {
        Map<String, Object> erro = new LinkedHashMap<>();
        erro.put("erro", mensagem);
        responder(troca, status, erro);
    }

    protected void metodoNaoPermitido(HttpExchange troca) throws IOException {
        responderErro(troca, 405, "Operação não disponível para este recurso.");
    }

    // ===================== Leitura do pedido =====================

    /** Lê o corpo do pedido (JSON) e devolve como Map. Corpo vazio vira Map vazio. */
    @SuppressWarnings("unchecked")
    protected Map<String, Object> lerCorpo(HttpExchange troca) throws IOException {
        try (InputStream entrada = troca.getRequestBody()) {
            String texto = new String(entrada.readAllBytes(), StandardCharsets.UTF_8).trim();
            if (texto.isEmpty()) {
                return new HashMap<>();
            }
            Object valor = Json.parse(texto);
            if (!(valor instanceof Map)) {
                throw new IllegalArgumentException("o corpo do pedido deve ser um objeto JSON.");
            }
            return (Map<String, Object>) valor;
        }
    }

    /** Lê os parâmetros da URL (?origem=A&destino=B). */
    protected Map<String, String> lerParametros(HttpExchange troca) {
        Map<String, String> parametros = new HashMap<>();
        String consulta = troca.getRequestURI().getRawQuery();
        if (consulta == null || consulta.isEmpty()) {
            return parametros;
        }
        for (String par : consulta.split("&")) {
            String[] kv = par.split("=", 2);
            String chave = URLDecoder.decode(kv[0], StandardCharsets.UTF_8);
            String valor = kv.length > 1 ? URLDecoder.decode(kv[1], StandardCharsets.UTF_8) : "";
            parametros.put(chave, valor);
        }
        return parametros;
    }

    /** Pega o token do cabeçalho "Authorization: Bearer <token>". */
    protected String lerToken(HttpExchange troca) {
        String cabecalho = troca.getRequestHeaders().getFirst("Authorization");
        if (cabecalho != null && cabecalho.startsWith("Bearer ")) {
            return cabecalho.substring(7).trim();
        }
        return null;
    }

    /** Devolve o usuário logado, ou null se ninguém estiver logado. */
    protected Usuario usuarioLogado(HttpExchange troca) {
        return sessaoService.obter(lerToken(troca));
    }

    // ===================== Conversão de valores do JSON =====================

    protected String texto(Map<String, Object> corpo, String chave) {
        Object valor = corpo.get(chave);
        return valor == null ? "" : valor.toString().trim();
    }

    /** Lê um número do JSON (aceita número ou texto como "12,5"). Retorna null se não informado. */
    protected Double decimal(Map<String, Object> corpo, String chave) {
        Object valor = corpo.get(chave);
        if (valor == null || valor.toString().trim().isEmpty()) {
            return null;
        }
        if (valor instanceof Number) {
            return ((Number) valor).doubleValue();
        }
        try {
            return Double.valueOf(valor.toString().trim().replace(',', '.'));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("o campo '" + chave + "' deve ser um número.");
        }
    }

    protected Integer inteiro(Map<String, Object> corpo, String chave) {
        Double d = decimal(corpo, chave);
        return d == null ? null : Integer.valueOf((int) Math.round(d));
    }

    /** Converte um texto da URL em número inteiro (ex.: o "3" de /api/rotas/3). */
    protected int lerId(String texto) {
        try {
            return Integer.parseInt(texto);
        } catch (NumberFormatException e) {
            throw new RegraNegocioException("Identificador inválido.", 400);
        }
    }

    /** Lê a lista "trechos" do JSON: [{"idTransporte": 1, "distanciaKm": 5.5}, ...] */
    protected List<RotaService.TrechoEntrada> lerTrechos(Map<String, Object> corpo) {
        Object bruto = corpo.get("trechos");
        List<RotaService.TrechoEntrada> trechos = new java.util.ArrayList<>();
        if (bruto == null) {
            return trechos;
        }
        if (!(bruto instanceof List)) {
            throw new IllegalArgumentException("o campo 'trechos' deve ser uma lista.");
        }
        for (Object item : (List<?>) bruto) {
            if (!(item instanceof Map)) {
                throw new IllegalArgumentException("cada trecho deve ser um objeto.");
            }
            @SuppressWarnings("unchecked")
            Map<String, Object> mapa = (Map<String, Object>) item;
            Integer idTransporte = inteiro(mapa, "idTransporte");
            Double distancia = decimal(mapa, "distanciaKm");
            if (idTransporte == null || distancia == null) {
                throw new RegraNegocioException("Cada trecho precisa de um transporte e de uma distância.");
            }
            trechos.add(new RotaService.TrechoEntrada(idTransporte, distancia));
        }
        return trechos;
    }
}
