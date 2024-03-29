package br.senai.sp.informatica.cadastro.model;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumns;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.validation.constraints.Email;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import br.senai.sp.informatica.cadastro.model.validacao.Logradouro;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity

public class Cliente {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer idCliente;
	@Size(min=3, max=150, message="O nome deve ter no minimo 3 e no m�ximo 150 caracteres")
	private String nome;
	@Logradouro(max=150, message="O endere�o � inv�lido")
	private String endereco;
	@Pattern(regexp="(9[0-9]{4}|[1-9][0-9]{3})-[0-9]{4}", message="O n� de telefone � inv�lido")
	private String telefone;
	@Email(message="O E-mail � invalido")
	private String email;
	private boolean desativado;
	@ManyToMany(fetch = FetchType.EAGER) 
	@JoinTable(name = "ServicosPrestados",
		joinColumns = { @JoinColumn(name="idCliente")},
		inverseJoinColumns = {@JoinColumn(name="idServicos")})
	private List<Servico> servicos;
	
	public void setEndereco(String endereco) {
		this.endereco = endereco.trim();
	}
}
