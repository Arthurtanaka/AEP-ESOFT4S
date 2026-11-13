package controller;

import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import service.AlternativaComparada;
import service.RotaService;
import service.SessaoService;

/**
 * Recurso /api/consulta?origem=...&destino=...: consulta de alternativas, comparação e destaque (RQ05, RQ06, RQ07).
 * A consulta é livre. Se o pedido vier de um usuário logado, a consulta entra no histórico (RN07).
 */
public class ConsultaController extends BaseController {

    private final RotaService rotaService;

    public ConsultaController(SessaoService sessaoService, RotaService rotaService) {
        super(sessaoService);
        this.rotaService = rotaService;
    }

    @Override
    protected void tratar(HttpExchange troca, String metodo, String[] partes) throws IOException {
        if (metodo.equals("GET") && partes.length == 0) {
            Map<String, String> parametros = lerParametros(troca);
            List<AlternativaComparada> alternativas = rotaService.consultarAlternativas(
                    usuarioLogado(troca), parametros.get("origem"), parametros.get("destino"));
            List<Object> resposta = new ArrayList<>();
            for (AlternativaComparada alt : alternativas) {
                resposta.add(Conversor.alternativa(alt));
            }
            responder(troca, 200, resposta);
        } else {
            metodoNaoPermitido(troca);
        }
    }
}
