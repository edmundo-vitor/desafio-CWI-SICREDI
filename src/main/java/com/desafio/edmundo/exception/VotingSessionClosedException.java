package com.desafio.edmundo.exception;

public class VotingSessionClosedException extends BadRequestException {
	private static final long serialVersionUID = 1L;

	public VotingSessionClosedException(String message) {
		super(message);
	}
}
