package com.desafio.edmundo.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

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
import com.desafio.edmundo.exception.UserAlreadyVotedException;
import com.desafio.edmundo.exception.UserNotAbleToVoteException;
import com.desafio.edmundo.exception.VotingSessionClosedException;
import com.desafio.edmundo.model.Vote;
import com.desafio.edmundo.model.VotingSession;
import com.desafio.edmundo.repository.VoteRepository;
import com.desafio.edmundo.repository.VotingSessionRepository;
import com.desafio.edmundo.util.UserAPI;

@DisplayName("VoteServiceImplTest")
public class VoteServiceImplTest extends ApplicationConfigTest {

	@MockBean
	private VoteRepository voteRepository;
	
	@MockBean
	private VotingSessionRepository votingSessionRepository;
	
	@MockBean
	private UserAPI userAPI;
	
	@Autowired
	private VoteService voteService;
	
	@Test
	public void findAllPaged() {
		Pageable pageable = PageRequest.of(0, 20);
		
		Page<Vote> page = new PageImpl<>(new ArrayList<Vote>());
		
		// Mock do repository
		when(voteRepository.findAll(pageable)).thenReturn(page);
		
		// Método a ser testado
		Page<Vote> responsePage = voteService.findAllPaged(pageable); 
		
		// Verificarções
		assertEquals(page, responsePage);
		verify(voteRepository, times(1)).findAll(pageable);
	}
	
	@Test
	public void findById() {
		Long id = 1L;
		
		// Mock do model
		Vote vote = mock(Vote.class);
		when(vote.getId()).thenReturn(id);
		
		// Mock do repository
		when(voteRepository.findById(id)).thenReturn(Optional.of(vote));
		
		// Método a ser testado
		Vote responseVote= voteService.findById(id);
		
		// Verificações de sucesso
		assertEquals(vote, responseVote);
		verify(voteRepository, times(1)).findById(id);
		
		//Verificações de erro
		EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> voteService.findById(2L));
		assertEquals("Entity not found", exception.getMessage());
	}
	
	@Test
	public void save_Ok() {
		// Mock do model
		VotingSession votingSession = mock(VotingSession.class);
		Vote vote = mock(Vote.class);
		when(votingSession.getId()).thenReturn(1L);
		when(votingSession.getVotes()).thenReturn(new HashSet<Vote>());
		when(votingSession.isOpen()).thenReturn(true);
		
		when(vote.getCPF()).thenReturn("202.089.110-73");
		when(vote.getVotingSession()).thenReturn(votingSession);
		
		// Mock do repository
		when(votingSessionRepository.findById(vote.getVotingSession().getId())).thenReturn(Optional.of(votingSession));
		when(voteRepository.save(vote)).thenReturn(vote);
		
		// Mock da API de usuário
		when(userAPI.verifyUser(vote.getCPF())).thenReturn(true);
		
		// Método a ser testado
		Vote responseVote = voteService.save(vote);
		
		// Verificação de sucesso
		assertEquals(vote, responseVote);
	}
	
	@Test
	public void save_UserNotAbleToVoteException() {
		// Mock model
		Vote vote = mock(Vote.class);
		when(vote.getCPF()).thenReturn("202.089.110-73");
		
		// Mock API de usuário
		when(userAPI.verifyUser(vote.getCPF())).thenReturn(false);
		
		UserNotAbleToVoteException exception = assertThrows(UserNotAbleToVoteException.class, () -> voteService.save(vote));
		assertEquals("CPF " + vote.getCPF() + " unable to vote", exception.getMessage());
	}
	
	@Test
	public void save_EntityNotFoundException() {
		// Mock model
		VotingSession votingSession = mock(VotingSession.class);
		when(votingSession.getId()).thenReturn(1L);
		
		Vote vote = mock(Vote.class);
		when(vote.getCPF()).thenReturn("202.089.110-73");
		when(vote.getVotingSession()).thenReturn(votingSession);
		
		// Mock API de usuário
		when(userAPI.verifyUser(vote.getCPF())).thenReturn(true);
		
		EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> voteService.save(vote));
		assertEquals("ID " + vote.getVotingSession().getId() + " voting session not found", exception.getMessage());
	}
	
	@Test
	public void save_VotingSessionClosedException() {
		// Mock model
		VotingSession votingSession = mock(VotingSession.class);
		when(votingSession.getId()).thenReturn(1L);
		when(votingSession.isOpen()).thenReturn(false);
		
		Vote vote = mock(Vote.class);
		when(vote.getCPF()).thenReturn("202.089.110-73");
		when(vote.getVotingSession()).thenReturn(votingSession);
		
		// Mock do repository
		when(votingSessionRepository.findById(vote.getVotingSession().getId())).thenReturn(Optional.of(votingSession));
		
		// Mock API de usuário
		when(userAPI.verifyUser(vote.getCPF())).thenReturn(true);
		
		VotingSessionClosedException exception = assertThrows(VotingSessionClosedException.class, () -> voteService.save(vote));
		assertEquals("ID " + vote.getVotingSession().getId() + " voting session is closed", exception.getMessage());
	}
	
	@Test
	public void save_UserAlreadyVotedException() {
		// Mock model
		VotingSession votingSession = mock(VotingSession.class);
		Vote vote = mock(Vote.class);
		
		Set<Vote> setVote = new HashSet<Vote>();
		setVote.add(vote);
		
		when(votingSession.getId()).thenReturn(1L);
		when(votingSession.isOpen()).thenReturn(true);
		when(votingSession.getVotes()).thenReturn(setVote);
		
		when(vote.getCPF()).thenReturn("202.089.110-73");
		when(vote.getVotingSession()).thenReturn(votingSession);
		
		// Mock do repository
		when(votingSessionRepository.findById(vote.getVotingSession().getId())).thenReturn(Optional.of(votingSession));
		
		// Mock API de usuário
		when(userAPI.verifyUser(vote.getCPF())).thenReturn(true);
		
		UserAlreadyVotedException exception = assertThrows(UserAlreadyVotedException.class, () -> voteService.save(vote));
		assertEquals("CPF " + vote.getCPF() + " already have a vote registered on this agenda", exception.getMessage());
	}
	
	@Test
	public void update_Ok() {
		Long id = 2L;
		
		// Mock model
		Vote voteEntity = mock(Vote.class);
		Vote voteDTO = mock(Vote.class);
		VotingSession votingSessionEntity = mock(VotingSession.class);
		
		when(voteDTO.getVotingSession()).thenReturn(votingSessionEntity);
		
		when(voteEntity.getId()).thenReturn(id);
		when(voteEntity.getVotingSession()).thenReturn(votingSessionEntity);
		
		when(votingSessionEntity.isOpen()).thenReturn(true);
		when(votingSessionEntity.getId()).thenReturn(1L);
		
		// Mock repository
		when(voteRepository.findById(id)).thenReturn(Optional.of(voteEntity));
		when(voteRepository.save(voteEntity)).thenReturn(voteEntity);
		when(votingSessionRepository.findById(voteDTO.getVotingSession().getId())).thenReturn(Optional.of(votingSessionEntity));
	
		// Método a ser testado
		Vote responseVote = voteService.update(id, voteDTO);
		
		// Verificação de sucesso
		assertEquals(voteEntity, responseVote);
		verify(voteRepository, times(1)).findById(id);
		verify(votingSessionRepository, times(1)).findById(voteDTO.getVotingSession().getId());
		verify(voteRepository, times(1)).save(voteEntity);
	}
	
	@Test
	public void update_VoteNotFoundException() {
		Long id = 1L;
		
		// Mock model
		Vote vote = mock(Vote.class);
		
		// Verificação de erro
		EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> voteService.update(id, vote));
		assertEquals("Entity not found", exception.getMessage());
	}
	
	@Test
	public void update_VotingSessionNotFoundException() {
		Long id = 2L;
		
		// Mock model
		VotingSession votingSession = mock(VotingSession.class);
		Vote vote = mock(Vote.class);
		
		when(votingSession.getId()).thenReturn(1L);
		when(vote.getVotingSession()).thenReturn(votingSession);
		
		// Mock repository
		when(voteRepository.findById(id)).thenReturn(Optional.of(vote));
		
		// Verificação de erro
		EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> voteService.update(id, vote));
		assertEquals("ID " + vote.getVotingSession().getId() + " voting session not found", exception.getMessage());
	}
	
	@Test
	public void update_VotingSessionClosedException() {
		Long id = 2L;
		
		// Mock model
		VotingSession votingSession = mock(VotingSession.class);
		Vote vote = mock(Vote.class);
		
		when(votingSession.isOpen()).thenReturn(false);
		when(votingSession.getId()).thenReturn(1L);
		when(vote.getVotingSession()).thenReturn(votingSession);
		
		// Mock repository
		when(voteRepository.findById(id)).thenReturn(Optional.of(vote));
		when(votingSessionRepository.findById(vote.getVotingSession().getId())).thenReturn(Optional.of(votingSession));
		
		// Verificação de erro
		VotingSessionClosedException exception = assertThrows(VotingSessionClosedException.class, () -> voteService.update(id, vote));
		assertEquals("ID " + vote.getVotingSession().getId() + " voting session is closed", exception.getMessage());
	}
	
	@Test
	public void update_UserAlreadyVotedException() {
		Long id = 2L;
		
		// Mock model
		VotingSession votingSession = mock(VotingSession.class);
		VotingSession newVotingSession = mock(VotingSession.class);
		Vote vote = mock(Vote.class);
		
		Set<Vote> setVote = new HashSet<Vote>();
		setVote.add(vote);
		
		when(votingSession.getId()).thenReturn(1L);
		
		when(newVotingSession.isOpen()).thenReturn(true);
		when(newVotingSession.getId()).thenReturn(3L);
		when(newVotingSession.getVotes()).thenReturn(setVote);
		
		when(vote.getVotingSession()).thenReturn(votingSession);
		when(vote.getCPF()).thenReturn("202.089.110-73");
		
		// Mock repository
		when(voteRepository.findById(id)).thenReturn(Optional.of(vote));
		when(votingSessionRepository.findById(vote.getVotingSession().getId())).thenReturn(Optional.of(newVotingSession));
		
		// Verificação de erro
		UserAlreadyVotedException exception = assertThrows(UserAlreadyVotedException.class, () -> voteService.update(id, vote));
		assertEquals("CPF " + vote.getCPF() + " already have a vote registered on this agenda", exception.getMessage());
	}
	
	@Test
	public void delete_Ok() {
		Long id = 1L;
		
		// Mock do model
		Vote voteEntity = mock(Vote.class);
		
		// Mock do repository
		when(voteRepository.findById(id)).thenReturn(Optional.of(voteEntity));
		
		// Método a ser testado
		voteService.delete(id);
		
		// Verificação de sucesso
		verify(voteRepository, times(1)).findById(id);
		verify(voteRepository, times(1)).delete(voteEntity);
		
		// Verificação de erro
		EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> voteService.delete(2L));
		assertEquals("Entity not found", exception.getMessage());
	}
}
