package controller;

import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import java.util.Map;
import model.Usuario;
import service.SessaoService;
import service.UsuarioService;

/** Recurso /api/usuarios: cadastro de usuários (RQ01). */
public class UsuarioController extends BaseController {

    private final UsuarioService usuarioService;

    public UsuarioController(SessaoService sessaoService, UsuarioService usuarioService) {
        super(sessaoService); // chama o construtor da classe mãe
        this.usuarioService = usuarioService;
    }

    /** POLIMORFISMO: cada controller trata os pedidos do seu jeito. */
    @Override
    protected void tratar(HttpExchange troca, String metodo, String[] partes) throws IOException {
        if (metodo.equals("POST") && partes.length == 0) {
            Map<String, Object> corpo = lerCorpo(troca);
            Usuario usuario = usuarioService.cadastrar(
                    texto(corpo, "nome"), texto(corpo, "email"), texto(corpo, "senha"),
                    texto(corpo, "perfil"), texto(corpo, "preferencia"), texto(corpo, "codigoAdmin"));
            responder(troca, 201, Conversor.usuario(usuario));
        } else {
            metodoNaoPermitido(troca);
        }
    }
}
