package io.github.volkovt.domain.repository;

import io.github.volkovt.domain.entity.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface Clientes extends JpaRepository<Cliente, Integer> {

    @Query(value = "FROM Cliente c WHERE c.nome LIKE :nome ")
    List<Cliente> encontrarPorNome(String nome);

    Boolean existsByNome(String diego);

    @Modifying
    void deleteByNome(String nome);

    @Query(" SELECT c FROM Cliente c LEFT JOIN FETCH c.pedidos WHERE c.id = :id")
    Cliente findClienteFetchPedidos(Integer id);
}
