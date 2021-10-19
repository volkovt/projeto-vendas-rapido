package io.github.volkovt.rest.controller;

import io.github.volkovt.domain.entity.Cliente;
import io.github.volkovt.domain.repository.Clientes;
import io.swagger.annotations.*;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/clientes")
@Api("Api Clientes")
public class ClienteController {
    private Clientes repositorioClientes;

    public ClienteController (Clientes clientes) {
        this.repositorioClientes = clientes;
    }

    @GetMapping("/{id}")
    @ApiOperation("Obter detalhes de um cliente.")
    @ApiResponses({
            @ApiResponse(code = 200, message="Cliente encontrado"),
            @ApiResponse(code = 400, message="Cliente não encontrado para o ID informado")
    })
    public Cliente getClienteById(@PathVariable @ApiParam("Id do cliente") Integer id ) {
        return repositorioClientes.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente não encontrado"));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @ApiOperation("Salva um novo cliente.")
    @ApiResponses({
            @ApiResponse(code = 201, message="Cliente salvo com sucesso"),
            @ApiResponse(code = 400, message="Erro de validação")
    })
    public Cliente salvar ( @RequestBody @Valid Cliente cliente ) {
        return repositorioClientes.save(cliente);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletar ( @PathVariable Integer id ) {
        repositorioClientes.findById(id).map( cliente -> {
            repositorioClientes.delete(cliente);
            return cliente;
        }).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente não encontrado"));
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void update (@PathVariable Integer id, @RequestBody @Valid Cliente cliente) {
        repositorioClientes.findById(id).map( x -> {
            cliente.setId(x.getId());
            repositorioClientes.save(cliente);
            return x;
        }).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente não encontrado"));
    }

    @GetMapping
    public List<Cliente> find( Cliente cliente ) {
        ExampleMatcher matcher = ExampleMatcher
                .matching()
                .withIgnoreCase()
                .withStringMatcher( ExampleMatcher.StringMatcher.CONTAINING );

        Example example = Example.of(cliente, matcher);
        return repositorioClientes.findAll(example);
    }
}
