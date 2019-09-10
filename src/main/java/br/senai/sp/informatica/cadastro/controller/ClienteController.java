package br.senai.sp.informatica.cadastro.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.senai.sp.informatica.cadastro.component.JsonError;
import br.senai.sp.informatica.cadastro.model.Cliente;
import br.senai.sp.informatica.cadastro.model.Servico;
import br.senai.sp.informatica.cadastro.model.valueobject.ListaDeServicos;
import br.senai.sp.informatica.cadastro.service.ClienteService;
import br.senai.sp.informatica.cadastro.service.ServicoService;

@RestController
@RequestMapping("/api")
public class ClienteController {
	@Autowired
	private ClienteService clienteService;
	@Autowired
	private ServicoService servicoService;

	@PostMapping("/cadastra")
	public ResponseEntity<Object> cadastra(@RequestBody @Valid Cliente cliente, BindingResult result){
		if(result.hasErrors()) {
			return ResponseEntity.unprocessableEntity()
					.contentType(MediaType.APPLICATION_JSON_UTF8)
					.body(JsonError.build(result));
		}else {
		clienteService.salvar(cliente);
		return ResponseEntity.ok().build();
		}
	}
	
	@GetMapping("/listaCliente")
	public ResponseEntity<List<Cliente>> listaCliente(){
		return ResponseEntity.ok(clienteService.getClientes());
	}
	
	@GetMapping("/editaCliente/{id}")
	public ResponseEntity<Object> editarCliente(@PathVariable("id") int idCliente) {
		Cliente cliente = clienteService.getClientes(idCliente);
		
		if(cliente != null) {
			return ResponseEntity.ok(cliente);
		} else {
			return ResponseEntity.notFound().build();
		}
	}
	
	@PostMapping("/removeCliente")
	public ResponseEntity<Object> removeCliente(@RequestBody int [] lista){
		if(clienteService.removeCliente(lista)) {
			return ResponseEntity.ok().build();
		} else {
			return ResponseEntity.unprocessableEntity().build();	
		}
	}
	
	@RequestMapping("/carregaServicos/{idCliente}")
	public ResponseEntity<Object> carregaServicos(@PathVariable("idCliente") int idCliente){
		Cliente cliente= clienteService.getClientes(idCliente);
		
		if(cliente != null) {
			return ResponseEntity.ok(servicoService.getServicos(cliente));
		} else {
			return ResponseEntity.notFound().build();
		}
	}
	
	@PostMapping("/selecionaServico")
	public ResponseEntity<Object> selecionaServico(@RequestBody ListaDeServicos lista){
		//Localiza o cliente pelo ID
		Cliente cliente = clienteService.getClientes(lista.getIdCliente());
		
		//Se o cliente foi encontrado
		if(cliente != null) {
			
			//Garantir que a lista de servicos do cliente seja NULA
			if(cliente.getServicos() == null)
			cliente.setServicos(new ArrayList<>());
			
			//Cria a lista com os servicos a serem excluidos do cliente
			//---------------------------------------------------------
			
			//para cada serviço do cliente faça...
			List<Servico> aDeletar = cliente.getServicos().stream() 
					//Selecione todo serviço do cliente que não esteja marcado como selecioanado
					.filter(servicoDoCliente -> !Arrays.stream(lista.getServicos())
							.filter(servicosEnviados -> servicosEnviados.getIdServico() == servicoDoCliente.getIdServico())
							.findFirst().get().isSelecionado())
					//Separe estes serviços para deletar posteriormente
					.collect(Collectors.toList());
			
			//Exclui do cliente todos os serviços que foram desmarcados no Front-end
			aDeletar.stream().forEach(servico -> 
				cliente.getServicos().removeIf(servicoDoCliente -> 
					servicoDoCliente.getIdServico() == servico.getIdServico()));
			
			//Cria a lista dos serviços para incluir no cliente
			//-------------------------------------------------
			
			//A partir da lista de serviços selecionados no frond-end faça...
			List<Servico> aIncluir = Arrays.stream(lista.getServicos())
					//separe os serviços que foram selecionados
					.filter(Servico::isSelecionado)
					//mas exclua os que o cliente ja tem.
					.filter(servicoSelecionado -> !cliente.getServicos().contains(servicoSelecionado))
					// separe os que sobraram  para incluir depois
					.collect(Collectors.toList());
			
			//Incluir os novos serviços no cliente
			aIncluir.stream().forEach(servico -> cliente.getServicos().add(servico));
			
			//Salvar os dados de cliente e servico no banco de dados
			clienteService.salvar(cliente);
					
			return ResponseEntity.ok().build();
		} else {
			return ResponseEntity.unprocessableEntity().build();
		}
	}
	
}
