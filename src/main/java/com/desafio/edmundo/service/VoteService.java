package com.desafio.edmundo.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.desafio.edmundo.model.Vote;

public interface VoteService {
	public Page<Vote> findAllPaged(Pageable pageable);
	public Vote findById(Long id);
	public Vote save(Vote vote);
	public Vote update(Long id, Vote vote);
	public void delete(Long id);
}
