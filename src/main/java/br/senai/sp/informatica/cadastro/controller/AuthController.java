package br.senai.sp.informatica.cadastro.controller;

import javax.validation.Valid;

import org.apache.logging.log4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import br.senai.sp.informatica.cadastro.component.JwtTokenProvider;
import br.senai.sp.informatica.cadastro.filter.JwtAutheticationFilter;
import br.senai.sp.informatica.cadastro.model.valueobject.JwtAuthenticationResponse;
import br.senai.sp.informatica.cadastro.model.valueobject.LoginRequest;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
	@Autowired
	private AuthenticationManager authenticationManager;
	
	@Autowired
	private JwtTokenProvider tokenProvider;
	
	private static final org.slf4j.Logger logger = LoggerFactory.getLogger(AuthController.class);
	
	@GetMapping("/teste")
	
	public ResponseEntity<Object> teste(@RequestBody LoginRequest login) {
		logger.error("Teste " +login.getUsername() + " : " + login.getPassword());
		return ResponseEntity.ok(login);
	}
	
	@PostMapping("/signin")
	public ResponseEntity<?> authenticaUsuario(@RequestBody @Valid LoginRequest login){
		Authentication auth = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(login.getUsername(), login.getPassword() ) );
		
		logger.error(login.getUsername()+ " : " + login.getPassword());
		
		SecurityContextHolder.getContext().setAuthentication(auth);
		
		// Criar o Token de Autenticação
		String jwt = tokenProvider.generateToken(auth);
		
		logger.error("Token " + jwt);
		
		return ResponseEntity.ok(new JwtAuthenticationResponse(jwt));
	}
}
