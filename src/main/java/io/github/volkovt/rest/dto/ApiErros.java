package io.github.volkovt.rest.dto;

import lombok.Getter;

import java.util.Arrays;
import java.util.List;

public class ApiErros {

    @Getter
    public List<String> erros;

    public ApiErros(String mensagem) {
        this.erros = Arrays.asList(mensagem);
    }

    public ApiErros(List<String> mensagens) {
        this.erros = mensagens;
    }
}
