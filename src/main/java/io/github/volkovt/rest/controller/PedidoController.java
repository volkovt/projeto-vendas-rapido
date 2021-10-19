package io.github.volkovt.rest.controller;

import io.github.volkovt.domain.entity.ItemPedido;
import io.github.volkovt.domain.entity.Pedido;
import io.github.volkovt.domain.enums.StatusPedido;
import io.github.volkovt.rest.dto.AtualizacaoStatusPedidoDTO;
import io.github.volkovt.rest.dto.InformacoesItemPedidoDTO;
import io.github.volkovt.rest.dto.InformacoesPedidoDTO;
import io.github.volkovt.rest.dto.PedidoDTO;
import io.github.volkovt.service.PedidoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/pedidos")
public class PedidoController {
    @Autowired
    private PedidoService pedidoService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Integer save(@RequestBody @Valid PedidoDTO dto) {
        Pedido pedido = pedidoService.salvar(dto);
        return pedido.getId();
    }

    @GetMapping("/{id}")
    public InformacoesPedidoDTO getById(@PathVariable Integer id) {
        return pedidoService.obterPedidoCompleto(id).map(this::converter).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pedido não encontrado."));
    }

    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateStatus(@PathVariable Integer id,
                             @RequestBody AtualizacaoStatusPedidoDTO novoStatus) {

        pedidoService.atualizaStatus(id, StatusPedido.valueOf(novoStatus.getNovoStatus()));
    }

    private InformacoesPedidoDTO converter(Pedido p) {
        return InformacoesPedidoDTO.builder()
                .codigo(p.getId())
                .dataPedido(p.getDataPedido().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")))
                .cpf(p.getCliente().getCpf())
                .nomeCliente(p.getCliente().getNome())
                .total(p.getTotal())
                .status(p.getStatus().name())
                .listaInfoItemPedido(converter(p.getItens())).build();
    }

    private List<InformacoesItemPedidoDTO> converter(List<ItemPedido> items) {
        if(CollectionUtils.isEmpty(items)) {
            return Collections.emptyList();
        }

        return items.stream().map(
                item -> InformacoesItemPedidoDTO.builder()
                        .descricaoProduto(item.getProduto().getDescricao())
                        .precoUnitario(item.getProduto().getPrecoUnitario())
                        .quantidade(item.getQuantidade())
                        .build()).collect(Collectors.toList());
    }
}
