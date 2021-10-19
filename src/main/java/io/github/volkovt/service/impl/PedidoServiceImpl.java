package io.github.volkovt.service.impl;

import io.github.volkovt.domain.entity.Cliente;
import io.github.volkovt.domain.entity.ItemPedido;
import io.github.volkovt.domain.entity.Pedido;
import io.github.volkovt.domain.entity.Produto;
import io.github.volkovt.domain.enums.StatusPedido;
import io.github.volkovt.domain.repository.Clientes;
import io.github.volkovt.domain.repository.ItemPedidos;
import io.github.volkovt.domain.repository.Pedidos;
import io.github.volkovt.domain.repository.Produtos;
import io.github.volkovt.exception.PedidoNaoEncontradoException;
import io.github.volkovt.exception.RegraNegocioExcecao;
import io.github.volkovt.rest.dto.ItemPedidoDTO;
import io.github.volkovt.rest.dto.PedidoDTO;
import io.github.volkovt.service.PedidoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PedidoServiceImpl implements PedidoService {

    private final Pedidos repository;
    private final Clientes clientesRepository;
    private final Produtos produtosRepository;
    private final ItemPedidos itemPedidosRepository;

    @Override
    @Transactional
    public Pedido salvar(PedidoDTO dto) {
        Cliente cliente = clientesRepository.findById(dto.getCliente()).orElseThrow(() -> new RegraNegocioExcecao("Código de cliente inválido."));

        Pedido pedido = new Pedido();
        pedido.setTotal(dto.getTotal());
        pedido.setDataPedido(LocalDate.now());
        pedido.setCliente(cliente);
        pedido.setStatus(StatusPedido.REALIZADO);

        List<ItemPedido> itemPedidos = converterItems(pedido, dto.getItems());
        repository.save(pedido);
        itemPedidosRepository.saveAll(itemPedidos);
        pedido.setItens(itemPedidos);
        return pedido;
    }

    @Override
    public Optional<Pedido> obterPedidoCompleto(Integer id) {
        return repository.findByIdFetchItems(id);
    }

    @Override
    @Transactional
    public void atualizaStatus(Integer id, StatusPedido statusPedido) {
        repository.findById(id)
                .map( pedido -> {
                    pedido.setStatus(statusPedido);
                    return repository.save(pedido);
                }).orElseThrow(() -> new PedidoNaoEncontradoException());
    }

    private List<ItemPedido> converterItems(Pedido pedido, List<ItemPedidoDTO> items) {
        if(items.isEmpty()) {
            throw new RegraNegocioExcecao("Não é possiivel realizar um pedido sem items.");
        }

        return items.stream().map( dto -> {
            Produto produto = produtosRepository.findById(dto.getProduto()).orElseThrow(() -> new RegraNegocioExcecao("Código do produto inválido: " + dto.getProduto()));
            ItemPedido itemPedido = new ItemPedido();
            itemPedido.setPedido(pedido);
            itemPedido.setProduto(produto);
            itemPedido.setQuantidade(dto.getQuantidade());
            return itemPedido;
        }).collect(Collectors.toList());
    }
}
