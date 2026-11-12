package controller;

import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;
import service.AlertaService;
import service.FavoritoService;
import service.HistoricoService;
import service.RotaService;
import service.SessaoService;
import service.TransporteService;
import service.UsuarioService;

/**
 * Monta e liga o servidor web. Usa o HttpServer que já vem no Java (pacote com.sun.net.httpserver),
 * então não é preciso instalar nenhum framework.
 *
 * Cada "contexto" liga um endereço da API ao controller responsável por ele.
 */
public class ServidorWeb {

    private final HttpServer servidor;
    private final int porta;

    public ServidorWeb(int porta, String pastaWeb, SessaoService sessaoService, UsuarioService usuarioService,
                       TransporteService transporteService, RotaService rotaService,
                       FavoritoService favoritoService, HistoricoService historicoService,
                       AlertaService alertaService) throws IOException {
        this.porta = porta;
        // Escuta em todas as interfaces de rede da máquina, na porta informada
        this.servidor = HttpServer.create(new InetSocketAddress(porta), 0);

        servidor.createContext("/api/usuarios", new UsuarioController(sessaoService, usuarioService));
        servidor.createContext("/api/sessao", new SessaoController(sessaoService, usuarioService));
        servidor.createContext("/api/transportes", new TransporteController(sessaoService, transporteService));
        servidor.createContext("/api/rotas", new RotaController(sessaoService, rotaService));
        servidor.createContext("/api/consulta", new ConsultaController(sessaoService, rotaService));
        servidor.createContext("/api/favoritos", new FavoritoController(sessaoService, favoritoService));
        servidor.createContext("/api/historico", new HistoricoController(sessaoService, historicoService));
        servidor.createContext("/api/alertas", new AlertaController(sessaoService, alertaService));
        servidor.createContext("/", new ArquivosEstaticosController(pastaWeb));

        // Vários pedidos ao mesmo tempo: um "pool" de 8 threads atende em paralelo
        servidor.setExecutor(Executors.newFixedThreadPool(8));
    }

    public void iniciar() {
        servidor.start();
    }

    public int getPorta() {
        return porta;
    }
}
