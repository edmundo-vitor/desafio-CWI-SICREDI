package com.desafio.edmundo.exception;

public class UserAlreadyVotedException extends BadRequestException{
	private static final long serialVersionUID = 1L;

	public UserAlreadyVotedException(String message) {
		super(message);
	}
}
