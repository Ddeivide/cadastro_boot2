package br.senai.sp.informatica.cadastro.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.Size;

import br.senai.sp.informatica.cadastro.model.validacao.Senha;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table (name = "users")
public class Usuario {
	
	@Id
	
	@Column (name = "username")
	@Size(min=5, max=15)
	private String nome;
	@Transient
	private String old_nome;
	@Column (name = "password")
	@Senha(message="A senha deve conter pelo menos um símbolo '#&$%")
	private String senha;
	
	@Column (name = "enabled")
	private boolean habilitado;

	@Transient
	private boolean administrador;
	@Transient
	private boolean editar;
	public Usuario orElse(Object object) {
		
		return null;
	}

	
	
}
