package controller;

import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import model.Alerta;
import service.AlertaService;
import service.RegraNegocioException;
import service.SessaoService;

/** Recurso /api/alertas: administrador registra, lista e ativa/inativa alertas de rotas (RQ10). */
public class AlertaController extends BaseController {

    private final AlertaService alertaService;

    public AlertaController(SessaoService sessaoService, AlertaService alertaService) {
        super(sessaoService);
        this.alertaService = alertaService;
    }

    @Override
    protected void tratar(HttpExchange troca, String metodo, String[] partes) throws IOException {
        if (metodo.equals("GET") && partes.length == 0) {
            List<Object> lista = new ArrayList<>();
            for (Alerta a : alertaService.listarTodos(usuarioLogado(troca))) {
                lista.add(Conversor.alerta(a));
            }
            responder(troca, 200, lista);

        } else if (metodo.equals("POST") && partes.length == 0) {
            Map<String, Object> c = lerCorpo(troca);
            Integer idRota = inteiro(c, "idRota");
            if (idRota == null) {
                throw new RegraNegocioException("Informe a rota do alerta.");
            }
            Alerta a = alertaService.registrar(usuarioLogado(troca), idRota, texto(c, "mensagem"));
            responder(troca, 201, Conversor.alerta(a));

        } else if (metodo.equals("PUT") && partes.length == 1) {
            Map<String, Object> c = lerCorpo(troca);
            alertaService.alterarStatus(usuarioLogado(troca), lerId(partes[0]), texto(c, "status"));
            Map<String, Object> resposta = new LinkedHashMap<>();
            resposta.put("mensagem", "Status do alerta atualizado.");
            responder(troca, 200, resposta);

        } else {
            metodoNaoPermitido(troca);
        }
    }
}
