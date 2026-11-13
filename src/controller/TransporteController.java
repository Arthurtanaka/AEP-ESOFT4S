package controller;

import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import model.Transporte;
import service.SessaoService;
import service.TransporteService;

/**
 * Recurso /api/transportes: CRUD de meios de transporte (RQ04).
 * GET é livre (a tela precisa da lista); POST, PUT e DELETE exigem administrador (verificado no Service).
 */
public class TransporteController extends BaseController {

    private final TransporteService transporteService;

    public TransporteController(SessaoService sessaoService, TransporteService transporteService) {
        super(sessaoService);
        this.transporteService = transporteService;
    }

    @Override
    protected void tratar(HttpExchange troca, String metodo, String[] partes) throws IOException {
        if (metodo.equals("GET") && partes.length == 0) {
            List<Object> lista = new ArrayList<>();
            for (Transporte t : transporteService.listar()) {
                lista.add(Conversor.transporte(t));
            }
            responder(troca, 200, lista);

        } else if (metodo.equals("POST") && partes.length == 0) {
            Map<String, Object> c = lerCorpo(troca);
            Transporte t = transporteService.cadastrar(usuarioLogado(troca), texto(c, "tipo"), texto(c, "nome"),
                    numero(decimal(c, "velocidadeMediaKmH")), numero(decimal(c, "emissaoCo2GKm")),
                    decimal(c, "fatorCongestionamento"), inteiro(c, "capacidadePassageiros"));
            responder(troca, 201, Conversor.transporte(t));

        } else if (metodo.equals("PUT") && partes.length == 1) {
            Map<String, Object> c = lerCorpo(troca);
            Transporte t = transporteService.atualizar(usuarioLogado(troca), lerId(partes[0]), texto(c, "nome"),
                    numero(decimal(c, "velocidadeMediaKmH")), numero(decimal(c, "emissaoCo2GKm")),
                    decimal(c, "fatorCongestionamento"), inteiro(c, "capacidadePassageiros"));
            responder(troca, 200, Conversor.transporte(t));

        } else if (metodo.equals("DELETE") && partes.length == 1) {
            transporteService.excluir(usuarioLogado(troca), lerId(partes[0]));
            Map<String, Object> resposta = new LinkedHashMap<>();
            resposta.put("mensagem", "Transporte excluído.");
            responder(troca, 200, resposta);

        } else {
            metodoNaoPermitido(troca);
        }
    }

    /** Número não informado vira 0 (o Service rejeita valores inválidos com mensagem clara). */
    private double numero(Double valor) {
        return valor == null ? 0.0 : valor;
    }
}
