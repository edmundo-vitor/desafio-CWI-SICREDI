package com.desafio.edmundo.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Optional;

import javax.persistence.EntityNotFoundException;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.desafio.edmundo.ApplicationConfigTest;
import com.desafio.edmundo.exception.BadRequestException;
import com.desafio.edmundo.model.Agenda;
import com.desafio.edmundo.model.VotingSession;
import com.desafio.edmundo.repository.AgendaRepository;
import com.desafio.edmundo.repository.VotingSessionRepository;

@DisplayName("VotingSessionServiceImplTest")
public class VotingSessionServiceImplTest extends ApplicationConfigTest {
	
	@MockBean
	private VotingSessionRepository votingSessionRepository;
	
	@MockBean
	private AgendaRepository agendaRepository;
	
	@Autowired
	private VotingSessionService votingSessionService;
	
	@Test
	public void findAllPaged() {
		Pageable pageable = PageRequest.of(0, 20);
		
		Page<VotingSession> page = new PageImpl<>(new ArrayList<VotingSession>());
		
		// Mock do repository
		when(votingSessionRepository.findAll(pageable)).thenReturn(page);
		
		// Método a ser testado
		Page<VotingSession> responsePage = votingSessionService.findAllPaged(pageable); 
		
		// Verificarções
		assertEquals(page, responsePage);
		verify(votingSessionRepository, times(1)).findAll(pageable);
	}
	
	@Test
	public void findById() {
		Long id = 1L;
		
		// Mock do model
		VotingSession votingSession = mock(VotingSession.class);
		when(votingSession.getId()).thenReturn(id);
		
		// Mock do repository
		when(votingSessionRepository.findById(id)).thenReturn(Optional.of(votingSession));
		
		// Método a ser testado
		VotingSession responseVotingSession = votingSessionService.findById(id);
		
		// Verificações de sucesso
		assertEquals(votingSession, responseVotingSession);
		verify(votingSessionRepository, times(1)).findById(id);
		
		//Verificações de erro
		EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> votingSessionService.findById(2L));
		assertEquals("Entity not found", exception.getMessage());
	}
	
	@Test
	public void save_Ok() {
		// Mock dos models
		Agenda agenda = mock(Agenda.class);
		when(agenda.getId()).thenReturn(1L);
		
		VotingSession votingSession = mock(VotingSession.class);
		when(votingSession.getId()).thenReturn(2L);
		when(votingSession.getAgenda()).thenReturn(agenda);
		when(votingSession.getTimeOpen()).thenReturn(LocalTime.of(0, 1, 0));
		
		// Mock do repository
		when(votingSessionRepository.save(votingSession)).thenReturn(votingSession);
		when(agendaRepository.findById(votingSession.getAgenda().getId())).thenReturn(Optional.of(agenda));
		
		// Médoto a ser testado
		VotingSession responseVotingSession = votingSessionService.save(votingSession);
		
		// Verificações de sucesso
		assertEquals(votingSession, responseVotingSession);
		verify(votingSessionRepository, times(1)).save(votingSession);
	}
	
	@Test
	public void save_AgendaNotFoundException() {
		// Mock dos models
		Agenda agenda = mock(Agenda.class);
		when(agenda.getId()).thenReturn(1L);
		
		VotingSession votingSession = mock(VotingSession.class);
		when(votingSession.getId()).thenReturn(2L);
		when(votingSession.getAgenda()).thenReturn(agenda);
		
		//Verificações de erro
		EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> votingSessionService.save(votingSession));
		assertEquals("ID 1 agenda not found", exception.getMessage());
	}
	
	@Test
	public void save_BadRequestException() {
		// Mock dos models
		Agenda agenda = mock(Agenda.class);
		VotingSession votingSession = mock(VotingSession.class);
		
		when(agenda.getId()).thenReturn(1L);
		when(agenda.getVotingSession()).thenReturn(votingSession);
		
		when(votingSession.getId()).thenReturn(2L);
		when(votingSession.getAgenda()).thenReturn(agenda);
		
		// Mock repository
		when(agendaRepository.findById(votingSession.getAgenda().getId())).thenReturn(Optional.of(agenda));
		
		//Verificações de erro
		BadRequestException exception = assertThrows(BadRequestException.class, () -> votingSessionService.save(votingSession));
		assertEquals("ID 1 agenda already has a registered voting session", exception.getMessagePersonalized());
	}
	
	@Test
	public void update_Ok() {
		// Mock dos models
		Agenda agenda = mock(Agenda.class);
		when(agenda.getId()).thenReturn(1L);
		
		VotingSession votingSession = mock(VotingSession.class);
		when(votingSession.getId()).thenReturn(2L);
		when(votingSession.getAgenda()).thenReturn(agenda);
		when(votingSession.getTimeOpen()).thenReturn(LocalTime.of(0, 1, 0));
		
		// Mock do repository
		when(votingSessionRepository.findById(2L)).thenReturn(Optional.of(votingSession));
		when(votingSessionRepository.save(votingSession)).thenReturn(votingSession);
		when(agendaRepository.findById(votingSession.getAgenda().getId())).thenReturn(Optional.of(agenda));
		
		// Médoto a ser testado
		VotingSession responseVotingSession = votingSessionService.update(2L, votingSession);
		
		// Verificações de sucesso
		assertEquals(votingSession, responseVotingSession);
		verify(votingSessionRepository, times(1)).save(votingSession);
	}
	
	@Test
	public void update_VotingSessionNotFound() {
		// Mock do model
		VotingSession votingSession = mock(VotingSession.class);
		
		// Verificação de erro
		EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> votingSessionService.update(2L, votingSession));
		assertEquals("Entity not found", exception.getMessage());
	}
	
	@Test 
	public void update_AgendaNotFoundException() {
		// Mock do model
		Agenda agenda = mock(Agenda.class);
		when(agenda.getId()).thenReturn(1L);
		
		VotingSession votingSession = mock(VotingSession.class);
		when(votingSession.getAgenda()).thenReturn(agenda);
		
		// Mock do repository
		when(votingSessionRepository.findById(2L)).thenReturn(Optional.of(votingSession));
		
		// Verificação de erro
		EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> votingSessionService.update(2L, votingSession));
		assertEquals("ID 1 agenda not found", exception.getMessage());
	}
	
	@Test 
	public void update_BadRequestException() {
		// Mock do model
		// Dados da entidade
		Agenda agendaEntity = mock(Agenda.class);
		VotingSession votingSessionEntity = mock(VotingSession.class);
		when(agendaEntity.getId()).thenReturn(1L);
		when(agendaEntity.getVotingSession()).thenReturn(votingSessionEntity);
		when(votingSessionEntity.getAgenda()).thenReturn(agendaEntity);
		
		// Dados do DTO
		Agenda agendaDTO = mock(Agenda.class);
		VotingSession votingSessionDTO = mock(VotingSession.class);
		when(agendaDTO.getId()).thenReturn(2L);
		when(votingSessionDTO.getAgenda()).thenReturn(agendaDTO);
		
		// Mock do repository
		when(votingSessionRepository.findById(3L)).thenReturn(Optional.of(votingSessionEntity));
		when(agendaRepository.findById(votingSessionDTO.getAgenda().getId())).thenReturn(Optional.of(agendaEntity));
		
		// Verificação de erro
		BadRequestException exception = assertThrows(BadRequestException.class, () -> votingSessionService.update(3L, votingSessionDTO));
		assertEquals("ID " + votingSessionDTO.getAgenda().getId() + " agenda already has a registered voting session", exception.getMessagePersonalized());
	}
	
	@Test
	public void delete_Ok() {
		Long id = 1L;
		
		// Mock do model
		VotingSession votingSessionEntity = mock(VotingSession.class);
		
		// Mock do repository
		when(votingSessionRepository.findById(id)).thenReturn(Optional.of(votingSessionEntity));
		
		// Método a ser testado
		votingSessionService.delete(id);
		
		// Verificação de sucesso
		verify(votingSessionRepository, times(1)).findById(id);
		verify(votingSessionRepository, times(1)).delete(votingSessionEntity);
		
		// Verificação de erro
		EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> votingSessionService.delete(2L));
		assertEquals("Entity not found", exception.getMessage());
	}
}
