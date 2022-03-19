package com.desafio.edmundo.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.desafio.edmundo.model.Agenda;

public interface AgendaService {
	public Page<Agenda> findAllPaged(Pageable pageable);
	public Agenda findById(Long id);
	public Agenda save(Agenda agenda);
	public Agenda update(Long id, Agenda agenda);
	public void delete(Long id);
}
