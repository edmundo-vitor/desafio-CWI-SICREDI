package com.desafio.edmundo.service.implementation;

import java.util.Optional;

import javax.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.desafio.edmundo.exception.UserAlreadyVotedException;
import com.desafio.edmundo.exception.UserNotAbleToVoteException;
import com.desafio.edmundo.exception.VotingSessionClosedException;
import com.desafio.edmundo.model.Vote;
import com.desafio.edmundo.model.VotingSession;
import com.desafio.edmundo.repository.VoteRepository;
import com.desafio.edmundo.repository.VotingSessionRepository;
import com.desafio.edmundo.service.VoteService;
import com.desafio.edmundo.util.UserAPI;

@Service
public class VoteServiceImpl implements VoteService {
	
	@Autowired
	private VoteRepository voteRepository;
	
	@Autowired
	private VotingSessionRepository votingSessionRepository;
	
	@Autowired
	private UserAPI userAPI;

	@Transactional(readOnly = true)
	public Page<Vote> findAllPaged(Pageable pageable) {
		Page<Vote> pageEntities = voteRepository.findAll(pageable);
		return pageEntities;
	}

	@Transactional(readOnly = true)
	public Vote findById(Long id) {
		Optional<Vote> opt = voteRepository.findById(id);
		Vote entity = opt.orElseThrow(() -> new EntityNotFoundException("Entity not found"));
		return entity;
	}

	@Transactional
	public Vote save(Vote vote) {
		// Verifica se o usuário não está habilitado para votar
		if(!userAPI.verifyUser(vote.getCPF())) {
			throw new UserNotAbleToVoteException("CPF " + vote.getCPF() + " unable to vote");
		}
		
		// Verifica se a sessão de votação existe
		VotingSession votingSessionEntity = votingSessionRepository.findById(vote.getVotingSession().getId())
				.orElseThrow(() -> new EntityNotFoundException("ID " + vote.getVotingSession().getId() + " voting session not found"));
		
		// Verifica se a sessão de votação ta encerrada
		if(!votingSessionEntity.isOpen()) {
			throw new VotingSessionClosedException("ID " + vote.getVotingSession().getId() + " voting session is closed");
		}
		
		// Verifica se o associado já votou nessa sessão de votação/pauta 
		votingSessionEntity.getVotes().stream().forEach(voteDB -> {
			if(voteDB.getCPF().equals(vote.getCPF())) {
				throw new UserAlreadyVotedException("CPF " + vote.getCPF() + " already have a vote registered on this agenda");
			}
		});
		
		// Adiciona o relacionamento de sessão da votação com o voto
		vote.setVotingSession(votingSessionEntity);
		Vote voteEntity = voteRepository.save(vote);
		
		return voteEntity;
	}

	@Transactional
	public Vote update(Long id, Vote vote) {
		Optional<Vote> opt = voteRepository.findById(id);
		Vote voteEntity = opt.orElseThrow(() -> new EntityNotFoundException("Entity not found"));
		
		// Verifica se a sessão de votação existe
		VotingSession votingSessionEntity = votingSessionRepository.findById(vote.getVotingSession().getId())
				.orElseThrow(() -> new EntityNotFoundException("ID " + vote.getVotingSession().getId() + " voting session not found"));
		
		// Verifica se a sessão de votação ta encerrada
		if(!votingSessionEntity.isOpen()) {
			throw new VotingSessionClosedException("ID " + vote.getVotingSession().getId() + " voting session is closed");
		}
		
		// Verifica se está alterando a sessão de votação
		if(voteEntity.getVotingSession().getId() != votingSessionEntity.getId()) {
			// Verifica se o associado já votou nessa sessão de votação/pauta 
			votingSessionEntity.getVotes().stream().forEach(voteDB -> {
				if(voteDB.getCPF().equals(vote.getCPF())) {
					throw new UserAlreadyVotedException("CPF " + vote.getCPF() + " already have a vote registered on this agenda");
				}
			});
		}
		
		// Adiciona o relacionamento de sessão da votação com o voto
		vote.setVotingSession(votingSessionEntity);
		
		voteEntity.copyToEntity(vote);
		voteEntity = voteRepository.save(voteEntity);
		return voteEntity;
	}

	public void delete(Long id) {
		Optional<Vote> opt = voteRepository.findById(id);
		Vote voteEntity = opt.orElseThrow(() -> new EntityNotFoundException("Entity not found"));
		System.out.println("Entrou " + voteEntity.getId());
		voteRepository.delete(voteEntity);
	}
	
}
