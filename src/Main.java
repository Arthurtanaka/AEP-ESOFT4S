import controller.ServidorWeb;
import dao.AlertaDAO;
import dao.ConexaoBanco;
import dao.DaoException;
import dao.FavoritoDAO;
import dao.HistoricoDAO;
import dao.RotaDAO;
import dao.TransporteDAO;
import dao.UsuarioDAO;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import service.AlertaService;
import service.FavoritoService;
import service.HistoricoService;
import service.RotaService;
import service.SessaoService;
import service.TransporteService;
import service.UsuarioService;
import util.Configuracao;

/**
 * Ponto de partida do MoveCity. Aqui o sistema é "montado":
 *   DAOs (banco)  ->  Services (regras)  ->  Controllers (API web)  ->  Servidor.
 *
 * Para executar, veja o README.md (scripts executar.sh / executar.bat).
 */
public class Main {

    public static void main(String[] args) {
        // Garante acentos corretos no terminal, em qualquer sistema operacional
        System.setOut(new PrintStream(System.out, true, StandardCharsets.UTF_8));
        System.setErr(new PrintStream(System.err, true, StandardCharsets.UTF_8));

        System.out.println("=== MoveCity - Mobilidade Urbana Sustentável ===");

        // 1) Confere se o banco está acessível antes de subir o servidor
        try {
            ConexaoBanco.testarConexao();
            System.out.println("[OK] Conexão com o banco de dados estabelecida.");
        } catch (DaoException e) {
            System.out.println("[ERRO] " + e.getMessage());
            System.out.println("Verifique se o MySQL está em execução, se o script database/schema.sql foi");
            System.out.println("executado e se usuário e senha estão corretos (veja o README.md, seção \"Configuração\").");
            System.exit(1);
        }

        // 2) Cria os DAOs (acesso ao banco)
        UsuarioDAO usuarioDAO = new UsuarioDAO();
        TransporteDAO transporteDAO = new TransporteDAO();
        RotaDAO rotaDAO = new RotaDAO();
        FavoritoDAO favoritoDAO = new FavoritoDAO();
        HistoricoDAO historicoDAO = new HistoricoDAO();
        AlertaDAO alertaDAO = new AlertaDAO();

        // 3) Cria os Services (regras de negócio), entregando a cada um os DAOs de que precisa
        SessaoService sessaoService = new SessaoService();
        UsuarioService usuarioService = new UsuarioService(usuarioDAO);
        TransporteService transporteService = new TransporteService(transporteDAO);
        RotaService rotaService = new RotaService(rotaDAO, transporteDAO, alertaDAO, historicoDAO);
        FavoritoService favoritoService = new FavoritoService(favoritoDAO, rotaDAO);
        HistoricoService historicoService = new HistoricoService(historicoDAO);
        AlertaService alertaService = new AlertaService(alertaDAO, rotaDAO);

        // 4) Liga o servidor web (controllers)
        try {
            ServidorWeb servidor = new ServidorWeb(Configuracao.getServidorPorta(), Configuracao.getPastaWeb(),
                    sessaoService, usuarioService, transporteService, rotaService,
                    favoritoService, historicoService, alertaService);
            servidor.iniciar();
            System.out.println("[OK] Servidor iniciado. Abra no navegador: http://localhost:" + servidor.getPorta());
            System.out.println("Para encerrar, pressione Ctrl+C.");
        } catch (IOException e) {
            System.out.println("[ERRO] Não foi possível iniciar o servidor na porta "
                    + Configuracao.getServidorPorta() + ": " + e.getMessage());
            System.out.println("A porta pode estar em uso. Troque-a em config.local.properties (servidor.porta).");
            System.exit(1);
        }
    }
}
