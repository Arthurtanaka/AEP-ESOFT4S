package util;

import java.nio.charset.StandardCharsets;
import java.util.Scanner;

/**
 * Funções para LER o que o usuário digita no teclado, já validando.
 * Se o usuário digitar algo inválido (letra onde deveria ser número, por exemplo),
 * a pergunta é repetida até ele acertar. Assim o programa não "quebra".
 */
public class Entrada {

    // Um único Scanner para o programa inteiro (criar vários causa problemas com o teclado).
    private static final Scanner SCANNER = new Scanner(System.in, StandardCharsets.UTF_8);

    private Entrada() {
    }

    /** Lê uma linha de texto (pode ser vazia). */
    public static String lerTexto(String mensagem) {
        System.out.print(mensagem);
        if (!SCANNER.hasNextLine()) {
            // Acabou a entrada (ex.: usuário fechou o terminal com Ctrl+D). Encerra com educação.
            System.out.println();
            System.out.println("Entrada encerrada. Até logo!");
            System.exit(0);
        }
        return SCANNER.nextLine().trim();
    }

    /** Lê um texto e só aceita se não for vazio. */
    public static String lerTextoObrigatorio(String mensagem) {
        while (true) {
            String texto = lerTexto(mensagem);
            if (!texto.isEmpty()) {
                return texto;
            }
            System.out.println("  -> Este campo é obrigatório.");
        }
    }

    /** Lê um texto; se o usuário só apertar Enter, mantém o valor atual (usado em atualizações). */
    public static String lerTextoOpcional(String mensagem, String valorAtual) {
        String texto = lerTexto(mensagem + " [Enter mantém: " + valorAtual + "]: ");
        return texto.isEmpty() ? valorAtual : texto;
    }

    /** Lê um número inteiro dentro de um intervalo (ex.: opção de menu de 0 a 4). */
    public static int lerInteiroEntre(String mensagem, int minimo, int maximo) {
        while (true) {
            String texto = lerTexto(mensagem);
            try {
                int valor = Integer.parseInt(texto);
                if (valor >= minimo && valor <= maximo) {
                    return valor;
                }
            } catch (NumberFormatException e) {
                // cai no aviso abaixo
            }
            System.out.println("  -> Digite um número inteiro entre " + minimo + " e " + maximo + ".");
        }
    }

    /** Lê um número inteiro qualquer maior ou igual a zero (ex.: um ID). */
    public static int lerInteiro(String mensagem) {
        return lerInteiroEntre(mensagem, 0, Integer.MAX_VALUE);
    }

    /** Lê um número decimal. Aceita vírgula ou ponto (12,5 ou 12.5). */
    public static double lerDecimal(String mensagem) {
        while (true) {
            String texto = lerTexto(mensagem).replace(',', '.');
            try {
                return Double.parseDouble(texto);
            } catch (NumberFormatException e) {
                System.out.println("  -> Digite um número válido (exemplo: 12,5).");
            }
        }
    }

    /** Decimal opcional: Enter mantém o valor atual. */
    public static double lerDecimalOpcional(String mensagem, double valorAtual) {
        while (true) {
            String texto = lerTexto(mensagem + " [Enter mantém: " + Formatador.decimal(valorAtual) + "]: ")
                    .replace(',', '.');
            if (texto.isEmpty()) {
                return valorAtual;
            }
            try {
                return Double.parseDouble(texto);
            } catch (NumberFormatException e) {
                System.out.println("  -> Digite um número válido (exemplo: 12,5).");
            }
        }
    }

    /** Pergunta sim/não. Retorna true para "s". */
    public static boolean lerConfirmacao(String mensagem) {
        while (true) {
            String texto = lerTexto(mensagem + " (s/n): ").toLowerCase();
            if (texto.equals("s") || texto.equals("sim")) {
                return true;
            }
            if (texto.equals("n") || texto.equals("nao") || texto.equals("não")) {
                return false;
            }
            System.out.println("  -> Responda com s ou n.");
        }
    }
}
