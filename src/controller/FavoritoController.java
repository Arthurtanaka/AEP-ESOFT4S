package controller;

import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import model.Favorito;
import service.FavoritoService;
import service.RegraNegocioException;
import service.SessaoService;

/** Recurso /api/favoritos: salvar, listar e remover rotas favoritas (RQ08). Exige usuário logado. */
public class FavoritoController extends BaseController {

    private final FavoritoService favoritoService;

    public FavoritoController(SessaoService sessaoService, FavoritoService favoritoService) {
        super(sessaoService);
        this.favoritoService = favoritoService;
    }

    @Override
    protected void tratar(HttpExchange troca, String metodo, String[] partes) throws IOException {
        if (metodo.equals("GET") && partes.length == 0) {
            List<Object> lista = new ArrayList<>();
            for (Favorito f : favoritoService.listar(usuarioLogado(troca))) {
                lista.add(Conversor.favorito(f));
            }
            responder(troca, 200, lista);

        } else if (metodo.equals("POST") && partes.length == 0) {
            Map<String, Object> c = lerCorpo(troca);
            Integer idRota = inteiro(c, "idRota");
            if (idRota == null) {
                throw new RegraNegocioException("Informe a rota a ser favoritada.");
            }
            Favorito f = favoritoService.favoritar(usuarioLogado(troca), idRota);
            responder(troca, 201, Conversor.favorito(f));

        } else if (metodo.equals("DELETE") && partes.length == 1) {
            favoritoService.remover(usuarioLogado(troca), lerId(partes[0]));
            Map<String, Object> resposta = new LinkedHashMap<>();
            resposta.put("mensagem", "Favorito removido.");
            responder(troca, 200, resposta);

        } else {
            metodoNaoPermitido(troca);
        }
    }
}
