package com.desafio.edmundo.service.implementation;

import java.time.LocalTime;
import java.util.Optional;

import javax.persistence.EntityNotFoundException;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.desafio.edmundo.exception.BadRequestException;
import com.desafio.edmundo.model.Agenda;
import com.desafio.edmundo.model.VotingSession;
import com.desafio.edmundo.repository.AgendaRepository;
import com.desafio.edmundo.repository.VotingSessionRepository;
import com.desafio.edmundo.service.VotingSessionService;
import com.desafio.edmundo.util.VotingSessionThread;

@Service
public class VotingSessionServiceImpl implements VotingSessionService {
	
	@Autowired
	private VotingSessionRepository votingSessionRepository;
	
	@Autowired
	private AgendaRepository agendaRepository;
	
	@Autowired
	private RabbitTemplate rabbitTemplate;
	
	@Value("#{ T(java.time.LocalTime).parse('${default.voting.session.time}')}")
	private LocalTime defaultVotingSessionTime;

	@Transactional(readOnly = true)
	public Page<VotingSession> findAllPaged(Pageable pageable) {
		Page<VotingSession> pageEntities = votingSessionRepository.findAll(pageable);
		return pageEntities;
	}

	@Transactional(readOnly = true)
	public VotingSession findById(Long id) {
		Optional<VotingSession> opt = votingSessionRepository.findById(id);
		VotingSession entity = opt.orElseThrow(() -> new EntityNotFoundException("Entity not found"));
		return entity;
	}

	@Transactional
	public VotingSession save(VotingSession votingSession) {
		// Setando o tempo padrão da sessão de votação
		if(votingSession.getTimeOpen() == null) {
			votingSession.setTimeOpen(defaultVotingSessionTime);
		}
		
		// Verifica se a agenda existe
		Agenda agendaEntity = agendaRepository.findById(votingSession.getAgenda().getId())
				.orElseThrow(() -> new EntityNotFoundException("ID " + votingSession.getAgenda().getId() + " agenda not found"));
		
		// Verifica se já foi cadastrada uma sessão de votação para essa agenda
		if(agendaEntity.getVotingSession() != null) {
			throw new BadRequestException("ID " + votingSession.getAgenda().getId() + " agenda already has a registered voting session");
		}else {
			// Adciona o relacionamento com agenda
			votingSession.setAgenda(agendaEntity);
		}
		
		VotingSession votingSessionEntity = votingSessionRepository.save(votingSession);
		
		// Inicia a sessão de votação
		Thread thread = new Thread(new VotingSessionThread(votingSessionEntity, votingSessionRepository, rabbitTemplate));
		thread.start();
		
		return votingSessionEntity;
	}

	@Transactional
	public VotingSession update(Long id, VotingSession votingSession) {
		Optional<VotingSession> opt = votingSessionRepository.findById(id);
		VotingSession votingSessionEntity = opt.orElseThrow(() -> new EntityNotFoundException("Entity not found"));
		
		// Verifica se a agenda existe
		Agenda agendaEntity = agendaRepository.findById(votingSession.getAgenda().getId())
				.orElseThrow(() -> new EntityNotFoundException("ID " + votingSession.getAgenda().getId() + " agenda not found"));
			
		// Verifica se o id da agenda é diferente da agenda "original"
		if(votingSessionEntity.getAgenda().getId() != votingSession.getAgenda().getId()) {
			// Verifica se já foi cadastrada uma sessão de votação para essa agenda
			if(agendaEntity.getVotingSession() != null) {
				throw new BadRequestException("ID " + votingSession.getAgenda().getId() + " agenda already has a registered voting session");
			}
		}
		
		// Adciona o relacionamento com agenda
		votingSession.setAgenda(agendaEntity);
		
		votingSessionEntity.copyToEntity(votingSession);
		votingSessionEntity = votingSessionRepository.save(votingSessionEntity);
		return votingSessionEntity;
	}

	public void delete(Long id) {
		Optional<VotingSession> opt = votingSessionRepository.findById(id);
		VotingSession votingSessionEntity = opt.orElseThrow(() -> new EntityNotFoundException("Entity not found"));
		votingSessionRepository.delete(votingSessionEntity);
	}

}
