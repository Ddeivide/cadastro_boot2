package br.senai.sp.informatica.cadastro.service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.senai.sp.informatica.cadastro.model.Usuario;
import br.senai.sp.informatica.cadastro.repo.UsuarioRepo;

@Service
public class UsuarioService {
	@Autowired
	private UsuarioRepo repo;
	
	public void salvar(@Valid Usuario usuario) {
		Usuario old_usuario;
		
		// Verifica se o nome do Usuario foi alterado
		if(!usuario.getOld_nome().equalsIgnoreCase(usuario.getNome())) {
			// Se sim, tire uma cópia do registro deste usuário do banco de dados
			old_usuario = getUsuario(usuario.getOld_nome());
			// e após, apague este registro no banco de dados
			removeUsuario(usuario.getOld_nome());
		} else {
			old_usuario = getUsuario(usuario.getNome());
		}
		
		// Se o nome do usuário foi alterado
		if(old_usuario != null) {
			// Preserve a senha
			usuario.setSenha(old_usuario.getSenha());
		}
		
		repo.save(usuario);
	}
	
	
	public List<Usuario> getUsuario() {
	
		return repo.findAll();
	}
	

	public Usuario getUsuario(String Nome) {
		return repo.findById(Nome)
				.orElse(null);
				
	}

	public boolean removeUsuario(String lista) {
		
			Usuario usuario = repo.findById(lista)
					.orElse(null);
			if(usuario != null) {		
				repo.delete(usuario);
				return true;
			} else {
				return false;
			}
	
		
	}
}
