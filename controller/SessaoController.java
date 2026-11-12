package controller;

import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import model.Usuario;
import service.RegraNegocioException;
import service.SessaoService;
import service.UsuarioService;

/**
 * Recurso /api/sessao: login (POST), consulta de quem está logado (GET) e logout (DELETE) (RQ02).
 */
public class SessaoController extends BaseController {

    private final UsuarioService usuarioService;

    public SessaoController(SessaoService sessaoService, UsuarioService usuarioService) {
        super(sessaoService);
        this.usuarioService = usuarioService;
    }

    @Override
    protected void tratar(HttpExchange troca, String metodo, String[] partes) throws IOException {
        if (partes.length != 0) {
            metodoNaoPermitido(troca);
            return;
        }
        switch (metodo) {
            case "POST": { // login
                Map<String, Object> corpo = lerCorpo(troca);
                Usuario usuario = usuarioService.login(texto(corpo, "email"), texto(corpo, "senha"));
                String token = sessaoService.criar(usuario);
                Map<String, Object> resposta = new LinkedHashMap<>();
                resposta.put("token", token);
                resposta.put("usuario", Conversor.usuario(usuario));
                responder(troca, 200, resposta);
                break;
            }
            case "GET": { // quem sou eu?
                Usuario usuario = usuarioLogado(troca);
                if (usuario == null) {
                    throw new RegraNegocioException("Sessão expirada. Entre novamente.", 401);
                }
                responder(troca, 200, Conversor.usuario(usuario));
                break;
            }
            case "DELETE": { // logout
                sessaoService.encerrar(lerToken(troca));
                Map<String, Object> resposta = new LinkedHashMap<>();
                resposta.put("mensagem", "Sessão encerrada.");
                responder(troca, 200, resposta);
                break;
            }
            default:
                metodoNaoPermitido(troca);
        }
    }
}
