package util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

/**
 * Lê as configurações do sistema (dados de conexão com o banco, por exemplo).
 *
 * Ordem de prioridade (a primeira que existir vale):
 *   1) Variáveis de ambiente (MOVECITY_DB_URL, MOVECITY_DB_USUARIO, MOVECITY_DB_SENHA,
 *      MOVECITY_CODIGO_ADMIN, MOVECITY_PORTA, MOVECITY_PASTA_WEB)
 *   2) Arquivo "config.local.properties" na pasta onde o programa é executado
 *   3) Valores padrão definidos aqui embaixo
 *
 * IMPORTANTE: o arquivo config.local.properties NÃO vai para o GitHub (está no .gitignore),
 * assim a senha do seu MySQL nunca fica pública.
 */
public class Configuracao {

    private static final String ARQUIVO = "config.local.properties";
    private static final Properties PROPRIEDADES = carregar();

    // Classe só com métodos estáticos: ninguém precisa criar objetos dela.
    private Configuracao() {
    }

    private static Properties carregar() {
        Properties p = new Properties();

        // 3) valores padrão
        p.setProperty("db.url", "jdbc:mysql://localhost:3306/movecity"
                + "?useSSL=false&allowPublicKeyRetrieval=true&characterEncoding=UTF-8");
        p.setProperty("db.usuario", "root");
        p.setProperty("db.senha", "");
        p.setProperty("codigo.admin", "movecity2026");
        p.setProperty("servidor.porta", "8080");
        p.setProperty("web.pasta", "src/web");

        // 2) arquivo local (se existir)
        File arquivo = new File(ARQUIVO);
        if (arquivo.exists()) {
            try (InputStreamReader leitor =
                         new InputStreamReader(new FileInputStream(arquivo), StandardCharsets.UTF_8)) {
                p.load(leitor);
            } catch (IOException e) {
                System.out.println("Aviso: não foi possível ler " + ARQUIVO + " (" + e.getMessage() + ").");
            }
        }

        // 1) variáveis de ambiente
        substituirPorAmbiente(p, "db.url", "MOVECITY_DB_URL");
        substituirPorAmbiente(p, "db.usuario", "MOVECITY_DB_USUARIO");
        substituirPorAmbiente(p, "db.senha", "MOVECITY_DB_SENHA");
        substituirPorAmbiente(p, "codigo.admin", "MOVECITY_CODIGO_ADMIN");
        substituirPorAmbiente(p, "servidor.porta", "MOVECITY_PORTA");
        substituirPorAmbiente(p, "web.pasta", "MOVECITY_PASTA_WEB");
        return p;
    }

    private static void substituirPorAmbiente(Properties p, String chave, String variavel) {
        String valor = System.getenv(variavel);
        if (valor != null && !valor.isEmpty()) {
            p.setProperty(chave, valor);
        }
    }

    public static String getDbUrl() {
        return PROPRIEDADES.getProperty("db.url");
    }

    public static String getDbUsuario() {
        return PROPRIEDADES.getProperty("db.usuario");
    }

    public static String getDbSenha() {
        return PROPRIEDADES.getProperty("db.senha");
    }

    /** Código que o usuário precisa digitar para se cadastrar como administrador. */
    public static String getCodigoAdmin() {
        return PROPRIEDADES.getProperty("codigo.admin");
    }

    /** Porta em que o servidor web escuta (padrão 8080). */
    public static int getServidorPorta() {
        try {
            return Integer.parseInt(PROPRIEDADES.getProperty("servidor.porta").trim());
        } catch (NumberFormatException e) {
            return 8080;
        }
    }

    /** Pasta com a interface web (HTML, CSS e JavaScript). Caminho relativo: funciona em qualquer sistema. */
    public static String getPastaWeb() {
        return PROPRIEDADES.getProperty("web.pasta");
    }
}
