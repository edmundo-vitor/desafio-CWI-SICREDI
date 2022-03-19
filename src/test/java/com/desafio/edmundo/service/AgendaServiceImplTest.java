package com.desafio.edmundo.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
import com.desafio.edmundo.model.Agenda;
import com.desafio.edmundo.repository.AgendaRepository;

@DisplayName("AgendaServiceImplTest")
public class AgendaServiceImplTest extends ApplicationConfigTest {
	
	@MockBean
	private AgendaRepository agendaRepository;
	
	@Autowired
	private AgendaService agendaService;
	
	@Test
	public void findAllPaged() {
		Pageable pageable = PageRequest.of(0, 20);
		
		Page<Agenda> page = new PageImpl<>(new ArrayList<Agenda>());
		
		// Mock do repository
		when(agendaRepository.findAll(pageable)).thenReturn(page);
		
		// Método a ser testado
		Page<Agenda> responsePage = agendaService.findAllPaged(pageable); 
		
		// Verificarções
		assertEquals(page, responsePage);
		verify(agendaRepository, times(1)).findAll(pageable);
	}
	
	@Test
	public void findById() {
		Long id = 1L;
		
		// Mock do model
		Agenda agenda = mock(Agenda.class);
		when(agenda.getId()).thenReturn(id);
		
		// Mock do repository
		when(agendaRepository.findById(id)).thenReturn(Optional.of(agenda));
		
		// Método a ser testado
		Agenda responseAgenda = agendaService.findById(id);
		
		// Verificações de sucesso
		assertEquals(agenda, responseAgenda);
		verify(agendaRepository, times(1)).findById(id);
		
		//Verificações de erro
		EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> agendaService.findById(2L));
		assertEquals("Entity not found", exception.getMessage());
	}
	
	@Test
	public void save() {
		// Mock do model
		Agenda agenda = mock(Agenda.class);
		when(agenda.getId()).thenReturn(1L);
		
		// Mock do repository
		when(agendaRepository.save(agenda)).thenReturn(agenda);
		
		// Médoto a ser testado
		Agenda responseAgenda = agendaService.save(agenda);
		
		// Verificações
		assertEquals(agenda, responseAgenda);
		verify(agendaRepository, times(1)).save(agenda);
	}
	
	@Test
	public void update() {
		Long id = 1L;
		
		// Mock do model
		Agenda newAgenda = mock(Agenda.class);
		when(newAgenda.getSubject()).thenReturn("New agenda");
		
		Agenda oldAgenda = mock(Agenda.class);
		when(oldAgenda.getSubject()).thenReturn("Old agenda");
		
		// Mock do repository
		when(agendaRepository.findById(id)).thenReturn(Optional.of(oldAgenda));
		when(agendaRepository.save(oldAgenda)).thenReturn(newAgenda);
		
		// Médoto a ser testado
		Agenda responseAgenda = agendaService.update(id, newAgenda);
		
		// Verificações de sucesso
		assertEquals(newAgenda, responseAgenda);
		verify(agendaRepository, times(1)).findById(id);
		verify(oldAgenda, times(1)).copyToEntity(newAgenda);
		verify(agendaRepository, times(1)).save(oldAgenda);
		
		// Verificações de erro
		EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> agendaService.update(2L, newAgenda));
		assertEquals("Entity not found", exception.getMessage());
	}
	
	@Test
	public void delete() {
		Long id = 1L;
		
		// Mock do model
		Agenda agenda = mock(Agenda.class);
		when(agenda.getId()).thenReturn(id);
		
		// Mock do repository
		when(agendaRepository.findById(id)).thenReturn(Optional.of(agenda));
		
		// Método a ser testado
		agendaService.delete(id);
		
		// Verificações de sucesso
		verify(agendaRepository, times(1)).findById(id);
		verify(agendaRepository, times(1)).delete(agenda);
		
		// Verificação de erro
		EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> agendaService.delete(2L));
		assertEquals("Entity not found", exception.getMessage());
	}
}
