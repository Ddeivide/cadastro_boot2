package br.senai.sp.informatica.cadastro.service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import br.senai.sp.informatica.cadastro.component.SecurityFacade;
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
	@Autowired
	private SecurityFacade security;
	
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
		} else {
			usuario.setSenha(new BCryptPasswordEncoder().encode(usuario.getSenha()));
		}
		
		repo.save(usuario);
	}
	
	
	public List<Usuario> getUsuario() {
	
		return repo.findAll().stream()
				// Remove os Usuários desbilitados
				.filter(usuario -> usuario.isHabilitado())
				// Remove o usuário que fez o logon
				.filter(usuario -> !usuario.getNome().equals(security.getUserName()))
				// Identificar os usuários que são Administradores
				.map(usuario -> atribuiPerfil(usuario))
				.collect(Collectors.toList());
	}
	
	private Usuario atribuiPerfil(Usuario usuario) {
		// Localiza a autorizacao para este usuário
		Autorizacao autorizacao = getAutorizacao(usuario.getNome());
		// Atribui o perfil encontrado
		if(autorizacao != null) {
			usuario.setAdministrador(autorizacao.getPerfil().endsWith("ADMIN"));
		} else {
			usuario.setAdministrador(false);
		}
		return usuario;
	}

	public Usuario getUsuario(String Nome) {
		Usuario usuario = repo.findById(Nome)
				.orElse(null);
		
		// Se o usuario foi encontrado
		if(usuario != null) {
			usuario = atribuiPerfil(usuario);
		}
		return usuario;		
	}

	public boolean removeUsuario(String nome) {
		
			Usuario usuario = repo.findById(nome)
					.orElse(null);
			if(usuario != null) {	
				
				Autorizacao autorizacao = getAutorizacao(nome);
				if(autorizacao != null)
					auth.delete(autorizacao);
				
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
