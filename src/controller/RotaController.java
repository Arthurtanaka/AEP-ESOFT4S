package controller;

import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import model.Rota;
import service.RotaService;
import service.SessaoService;

/**
 * Recurso /api/rotas: CRUD COMPLETO da entidade principal (RQ03).
 *   GET /api/rotas        -> Read (lista)       GET /api/rotas/3    -> Read (uma)
 *   POST /api/rotas       -> Create             PUT /api/rotas/3    -> Update
 *   DELETE /api/rotas/3   -> Delete
 * Leitura é livre; criar, alterar e excluir exigem administrador (verificado no Service).
 */
public class RotaController extends BaseController {

    private final RotaService rotaService;

    public RotaController(SessaoService sessaoService, RotaService rotaService) {
        super(sessaoService);
        this.rotaService = rotaService;
    }

    @Override
    protected void tratar(HttpExchange troca, String metodo, String[] partes) throws IOException {
        if (metodo.equals("GET") && partes.length == 0) {
            List<Object> lista = new ArrayList<>();
            for (Rota r : rotaService.listar()) {
                lista.add(Conversor.rota(r));
            }
            responder(troca, 200, lista);

        } else if (metodo.equals("GET") && partes.length == 1) {
            responder(troca, 200, Conversor.rota(rotaService.buscarPorId(lerId(partes[0]))));

        } else if (metodo.equals("POST") && partes.length == 0) {
            Map<String, Object> c = lerCorpo(troca);
            Rota rota = rotaService.cadastrar(usuarioLogado(troca), texto(c, "origem"), texto(c, "destino"),
                    texto(c, "descricao"), lerTrechos(c));
            responder(troca, 201, Conversor.rota(rota));

        } else if (metodo.equals("PUT") && partes.length == 1) {
            Map<String, Object> c = lerCorpo(troca);
            Rota rota = rotaService.atualizar(usuarioLogado(troca), lerId(partes[0]), texto(c, "origem"),
                    texto(c, "destino"), texto(c, "descricao"), lerTrechos(c));
            responder(troca, 200, Conversor.rota(rota));

        } else if (metodo.equals("DELETE") && partes.length == 1) {
            rotaService.excluir(usuarioLogado(troca), lerId(partes[0]));
            Map<String, Object> resposta = new LinkedHashMap<>();
            resposta.put("mensagem", "Rota excluída.");
            responder(troca, 200, resposta);

        } else {
            metodoNaoPermitido(troca);
        }
    }
}
