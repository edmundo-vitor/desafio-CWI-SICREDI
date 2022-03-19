package com.desafio.edmundo.exception;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BadRequestException extends RuntimeException {
	private static final long serialVersionUID = 1L;
	
	private String message;
	
	public BadRequestException() {
	}

	public BadRequestException(String message) {
		this.message = message;
	}
	
	public String getMessagePersonalized() {
		return message;
	}

}
