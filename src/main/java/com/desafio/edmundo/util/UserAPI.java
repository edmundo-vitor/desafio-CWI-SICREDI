package com.desafio.edmundo.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.desafio.edmundo.dto.UserInfoApiDTO;

@Service
public class UserAPI {
	
	@Value("${user.api.uri}")
	private String userApiUri;
	
	public boolean verifyUser(String cpf) {
		// Remove os caracteres especiais do CPF
		cpf = cpf.replace(".", "");
		cpf = cpf.replace("-", "");
		
		// Faz a requisição para a API externa
		RestTemplate restTemplate = new RestTemplate();
		UserInfoApiDTO dto = restTemplate.getForObject(userApiUri + "/" + cpf, UserInfoApiDTO.class);
		
		// Faz a verificação se o usuário está habilitado para votar
		if(dto.getStatus().equals("ABLE_TO_VOTE")) {
			return true;
		}else {
			return false;
		}
	}
}
