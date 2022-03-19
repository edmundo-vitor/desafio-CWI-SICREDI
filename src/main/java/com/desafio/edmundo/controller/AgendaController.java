package com.desafio.edmundo.controller;

import java.net.URI;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.desafio.edmundo.model.Agenda;
import com.desafio.edmundo.service.AgendaService;

@RestController
@RequestMapping("/agendas")
public class AgendaController {

	@Autowired
	private AgendaService agendaService;
	
	@GetMapping
	public ResponseEntity<Page<Agenda>> findAllPaged(Pageable pageable) {
		Page<Agenda> agendaPage = agendaService.findAllPaged(pageable);
		return ResponseEntity.ok().body(agendaPage);
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<Agenda> findById(@PathVariable Long id) {
		Agenda entity = agendaService.findById(id);
		return ResponseEntity.ok().body(entity);
	}
	
	@PostMapping
	public ResponseEntity<Agenda> save(@RequestBody @Valid Agenda agenda) {
		Agenda entity = agendaService.save(agenda);
		URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(entity.getId()).toUri();
		return ResponseEntity.created(uri).body(entity);
	}
	
	@PutMapping("/{id}")
	public ResponseEntity<Agenda> update(@PathVariable Long id, @RequestBody @Valid Agenda agenda) {
		Agenda entity = agendaService.update(id, agenda);
		return ResponseEntity.ok().body(entity);
	}
	
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> delete(@PathVariable Long id) {
		agendaService.delete(id);
		return ResponseEntity.noContent().build();
	}
}
