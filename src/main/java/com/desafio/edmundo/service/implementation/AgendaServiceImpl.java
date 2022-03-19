package com.desafio.edmundo.service.implementation;

import java.util.Optional;

import javax.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.desafio.edmundo.model.Agenda;
import com.desafio.edmundo.repository.AgendaRepository;
import com.desafio.edmundo.service.AgendaService;

@Service
public class AgendaServiceImpl implements AgendaService {
	
	@Autowired
	private AgendaRepository agendaRepository;

	@Transactional(readOnly = true)
	public Page<Agenda> findAllPaged(Pageable pageable) {
		Page<Agenda> pageEntities = agendaRepository.findAll(pageable);
		return pageEntities;
	}

	@Transactional(readOnly = true)
	public Agenda findById(Long id) {
		Optional<Agenda> opt = agendaRepository.findById(id);
		Agenda entity = opt.orElseThrow(() -> new EntityNotFoundException("Entity not found"));
		return entity;
	}

	@Transactional
	public Agenda save(Agenda agenda) {
		Agenda agendaEntity = agendaRepository.save(agenda);
		return agendaEntity;
	}

	@Transactional
	public Agenda update(Long id, Agenda agenda) {
		Optional<Agenda> opt = agendaRepository.findById(id);
		Agenda agendaEntity = opt.orElseThrow(() -> new EntityNotFoundException("Entity not found"));
		
		agendaEntity.copyToEntity(agenda);
		agendaEntity = agendaRepository.save(agendaEntity);
		return agendaEntity;
	}

	public void delete(Long id) {
		Optional<Agenda> opt = agendaRepository.findById(id);
		Agenda agendaEntity = opt.orElseThrow(() -> new EntityNotFoundException("Entity not found"));
		agendaRepository.delete(agendaEntity);
	}
	
}
