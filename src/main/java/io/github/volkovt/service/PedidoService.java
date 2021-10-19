package io.github.volkovt.service;

import io.github.volkovt.domain.entity.Pedido;
import io.github.volkovt.domain.enums.StatusPedido;
import io.github.volkovt.rest.dto.PedidoDTO;

import java.util.Optional;

public interface PedidoService {
    Pedido salvar(PedidoDTO dto);

    Optional<Pedido> obterPedidoCompleto(Integer id);
    void atualizaStatus(Integer id, StatusPedido statusPedido);
}
