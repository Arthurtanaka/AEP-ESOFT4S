package service;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import model.Usuario;

/**
 * Controla quem está logado. Quando o usuário entra, recebemos um "token" (um código aleatório e único);
 * o navegador envia esse token em cada pedido e assim sabemos quem é o usuário.
 * Os tokens ficam na memória do servidor: se o programa for reiniciado, todos precisam entrar de novo.
 */
public class SessaoService {

    // ConcurrentHashMap: seguro quando vários pedidos chegam ao mesmo tempo
    private final Map<String, Usuario> sessoes = new ConcurrentHashMap<>();

    public String criar(Usuario usuario) {
        String token = UUID.randomUUID().toString();
        sessoes.put(token, usuario);
        return token;
    }

    /** Devolve o usuário do token, ou null se o token não existir. */
    public Usuario obter(String token) {
        if (token == null) {
            return null;
        }
        return sessoes.get(token);
    }

    public void encerrar(String token) {
        if (token != null) {
            sessoes.remove(token);
        }
    }
}
