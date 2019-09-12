package br.senai.sp.informatica.cadastro.service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import br.senai.sp.informatica.cadastro.model.Autorizacao;
import br.senai.sp.informatica.cadastro.model.Usuario;
import br.senai.sp.informatica.cadastro.repo.AutorizacaoRepo;
import br.senai.sp.informatica.cadastro.repo.UsuarioRepo;

@Service
public class UsuarioService {
	@Autowired
	private UsuarioRepo repo;
	@Autowired
	private AutorizacaoRepo auth;
	
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
		
		// Salva o Perfil do usuário na tabele Autorizacoes
		auth.save(new Autorizacao(usuario.getNome(),
				usuario.isAdministrador() ? "ROLE_ADMIN" : "ROLE_USER"));
		
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
	
	private Autorizacao getAutorizacao(String nome) {
		return auth.findById(nome).orElse(null);
		}
	
	public GrantedAuthority getAutorizacoes(String nome) {
		Autorizacao autorizacao = getAutorizacao(nome);
		
		return autorizacao != null ? () -> autorizacao.getPerfil() : null ;
	}
}
