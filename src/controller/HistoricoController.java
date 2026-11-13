package controller;

import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import model.Historico;
import service.HistoricoService;
import service.SessaoService;

/** Recurso /api/historico: histórico de consultas do usuário logado (RQ09). */
public class HistoricoController extends BaseController {

    private final HistoricoService historicoService;

    public HistoricoController(SessaoService sessaoService, HistoricoService historicoService) {
        super(sessaoService);
        this.historicoService = historicoService;
    }

    @Override
    protected void tratar(HttpExchange troca, String metodo, String[] partes) throws IOException {
        if (metodo.equals("GET") && partes.length == 0) {
            List<Object> lista = new ArrayList<>();
            for (Historico h : historicoService.listar(usuarioLogado(troca))) {
                lista.add(Conversor.historico(h));
            }
            responder(troca, 200, lista);
        } else {
            metodoNaoPermitido(troca);
        }
    }
}
