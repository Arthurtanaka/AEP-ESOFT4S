package dao;

import java.util.List;

/**
 * PILAR DE POO: INTERFACE.
 *
 * Uma interface é um "contrato": diz QUAIS operações uma classe deve oferecer, sem dizer COMO.
 * Qualquer DAO que implemente CrudDAO é obrigado a ter os cinco métodos abaixo
 * (o CRUD: Create, Read, Update e Delete) e a escrevê-los com @Override.
 *
 * O "<T>" é um tipo genérico: em CrudDAO<Rota>, o T vira Rota; em CrudDAO<Transporte>, vira Transporte.
 */
public interface CrudDAO<T> {

    /** CREATE: grava um novo registro no banco. */
    void inserir(T objeto);

    /** READ: devolve todos os registros. */
    List<T> listar();

    /** READ: devolve um registro pelo id (ou null se não existir). */
    T buscarPorId(int id);

    /** UPDATE: altera um registro. Retorna false se o id não existir. */
    boolean atualizar(T objeto);

    /** DELETE: apaga um registro. Retorna false se o id não existir. */
    boolean excluir(int id);
}
