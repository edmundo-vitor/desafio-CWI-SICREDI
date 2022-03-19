package com.desafio.edmundo.exception;

public class UserNotAbleToVoteException extends BadRequestException {
	private static final long serialVersionUID = 1L;

	public UserNotAbleToVoteException(String message) {
		super(message);
	}
	
}
