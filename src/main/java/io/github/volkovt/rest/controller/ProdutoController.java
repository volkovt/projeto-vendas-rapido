package io.github.volkovt.rest.controller;

import io.github.volkovt.domain.entity.Produto;
import io.github.volkovt.domain.repository.Produtos;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/produtos")
public class ProdutoController {
    @Autowired
    private Produtos repositorio;

    @GetMapping
    public List<Produto> buscarLista(Produto produto) {
        ExampleMatcher matcher = ExampleMatcher.matchingAny().withIgnoreCase().withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING);
        return repositorio.findAll(Example.of(produto, matcher));
    }

    @GetMapping("/{id}")
    public Produto buscarPorId(Integer id) {
        return repositorio.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Produto salvar(@RequestBody @Valid Produto produto) {
        return repositorio.save(produto);
    }

    @PutMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void atualizar(@RequestParam Integer id, @RequestBody @Valid Produto produto) {
        repositorio.findById(id).map(x -> {
            produto.setId(id);
            return repositorio.save(produto);
        }).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public void deletar(@RequestParam Integer id) {
        repositorio.findById(id).map(x -> {
            repositorio.deleteById(x.getId());
            return x;
        }).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }
}
