package com.desafio.edmundo.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum OptionVote {
	Sim("Sim"),
	Não("Não");
	
	private String voto;
}
