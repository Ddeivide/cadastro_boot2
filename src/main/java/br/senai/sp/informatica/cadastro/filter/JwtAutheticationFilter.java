package br.senai.sp.informatica.cadastro.filter;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import br.senai.sp.informatica.cadastro.component.JwtTokenProvider;
import br.senai.sp.informatica.cadastro.model.Usuario;
import br.senai.sp.informatica.cadastro.service.UsuarioService;

public class JwtAutheticationFilter extends OncePerRequestFilter{
	@Autowired
	private JwtTokenProvider tokenProvider;
	@Autowired 
	UsuarioService usuarioService;
	
	private static final Logger logger = LoggerFactory.getLogger(JwtAutheticationFilter.class);
	
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		try {
			// Retira do Header HTTP o Token de autenticação
			String jwt = getJwtFromRequest(request);
			// Valida o Token
			if(StringUtils.hasLength(jwt) && tokenProvider.validateToken(jwt)) {
				// Obtém o UserID a partir do Token
				String userId = tokenProvider.getUserIdFromJWT(jwt);
				// Localiza os dados do Usuário do Banco de dados
				Usuario usuario = usuarioService.getUsuario(userId);
				
				// Cria o Token de Autenticação para o ambiente de Segurança Web
				UsernamePasswordAuthenticationToken autenticacao = 
						new UsernamePasswordAuthenticationToken(
								usuario, Collections.singletonList(
										usuarioService.getAutorizacoes(userId) ));
				
				// Registra a URL da requisição Web no Token de Autorização
				autenticacao.setDetails(
						new WebAuthenticationDetailsSource().buildDetails(request));
				// Autentica o Usuário no Ambiente de Segurça Web
				SecurityContextHolder.getContext().setAuthentication(autenticacao);
			}
		} catch (Exception erro) {
			logger.error("Não foi possivel registrar a Autenticação", erro);
		}
		
	}
	
	private String getJwtFromRequest(HttpServletRequest request) {
		// Obtém o Token do Cabeçálho HTTP
		String bearerToken = request.getHeader("Authorization");
		
		// Verifica se existe a informação de autenticação
		if(StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
			// Retorna somente o Token Excluindo o prefixo "Bearer"
			return bearerToken.substring(7, bearerToken.length());
		}
		
		return null;
	}

}
