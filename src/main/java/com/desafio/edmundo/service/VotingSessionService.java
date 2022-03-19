package com.desafio.edmundo.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.desafio.edmundo.model.VotingSession;

public interface VotingSessionService {
	public Page<VotingSession> findAllPaged(Pageable pageable);
	public VotingSession findById(Long id);
	public VotingSession save(VotingSession votingSession);
	public VotingSession update(Long id, VotingSession votingSession);
	public void delete(Long id);
}
