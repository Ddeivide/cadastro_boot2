package br.senai.sp.informatica.cadastro.component;

import java.util.stream.Collectors;

import org.springframework.validation.BindingResult;

public class JsonError {
	public static String build(BindingResult result) {
		return new StringBuilder("{\n" +
	// Obt�m a lista dos atributos com erro
				result.getFieldErrors().stream()
					// Separa o nome do atributo e da a mensagem de erro
					.map(erro -> "\"" + erro.getField() + "\" : \"" +
										erro.getDefaultMessage() + "\"")
					// Agrupa todos dos erros separados por virgula
					.collect(Collectors.joining(",\n"))
				+ "}\n").toString();
	}

}
