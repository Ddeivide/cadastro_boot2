package br.senai.sp.informatica.cadastro.model.validacao;

import java.util.function.BiFunction;
import java.util.function.Predicate;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import javassist.CtField.Initializer;

public class SenhaValidador implements ConstraintValidator<Senha, String> {
	
	BiFunction<String, Predicate<Integer>, Boolean> regra = (texto, condicao) -> 
	// Para cada letra deste texto, faça...
			texto.chars()
			// Separe a letra que atender a condição
			.filter(letra -> condicao.test(letra))
			// Separe a 1ª letra que encontrar
			.findAny()
			//então, achou?;
			.isPresent();
	
			
	@Override
	public boolean isValid(String senha, ConstraintValidatorContext context) {
			
		// A senha deve ter no monimo 8 caracteres
		return senha.length() >= 8 &&
				regra.apply(senha, letra -> letra == '#' || letra =='&' || letra == '%') &&
				// deve ter pelo meno sum simbolo
				regra.apply(senha, Character::isUpperCase) &&
				// e ter pelo menos um digito
				regra.apply(senha, Character::isDigit);
	}

}
