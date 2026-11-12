package util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Funções de segurança. Aqui fica o cálculo do HASH da senha (regra de negócio RN02:
 * a senha nunca é guardada em texto puro no banco).
 *
 * Um hash é como uma "impressão digital" do texto: sempre gera o mesmo resultado para a
 * mesma senha, mas não dá para voltar do hash para a senha original.
 *
 * Observação para o professor/leitor: SHA-256 foi usado por ser simples e já vir com o Java.
 * Em um sistema real, o ideal seria um algoritmo próprio para senhas (BCrypt, por exemplo),
 * que exige uma biblioteca externa.
 */
public class SegurancaUtil {

    private SegurancaUtil() {
    }

    /** Transforma a senha digitada em um hash SHA-256 de 64 caracteres (hexadecimal). */
    public static String gerarHash(String texto) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] bytes = digest.digest(texto.getBytes(StandardCharsets.UTF_8));

            StringBuilder hex = new StringBuilder();
            for (byte b : bytes) {
                hex.append(String.format("%02x", b)); // cada byte vira 2 letras/números
            }
            return hex.toString();
        } catch (NoSuchAlgorithmException e) {
            // Todo Java possui SHA-256, então isto na prática nunca acontece.
            throw new RuntimeException("Algoritmo SHA-256 indisponível.", e);
        }
    }
}
