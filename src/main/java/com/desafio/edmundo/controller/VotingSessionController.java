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

import com.desafio.edmundo.model.VotingSession;
import com.desafio.edmundo.service.VotingSessionService;

@RestController
@RequestMapping("/voting-sessions")
public class VotingSessionController {
	
	@Autowired
	private VotingSessionService votingSessionService;

	@GetMapping
	public ResponseEntity<Page<VotingSession>> findAllPaged(Pageable pageable) {
		Page<VotingSession> votingSessionPage = votingSessionService.findAllPaged(pageable);
		return ResponseEntity.ok().body(votingSessionPage);
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<VotingSession> findById(@PathVariable Long id) {
		VotingSession entity = votingSessionService.findById(id);
		return ResponseEntity.ok().body(entity);
	}
	
	@PostMapping
	public ResponseEntity<VotingSession> save(@RequestBody @Valid VotingSession votingSession) {
		VotingSession entity = votingSessionService.save(votingSession);
		URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(entity.getId()).toUri();
		return ResponseEntity.created(uri).body(entity);
	}
	
	@PutMapping("/{id}")
	public ResponseEntity<VotingSession> update(@PathVariable Long id, @RequestBody @Valid VotingSession votingSession) {
		VotingSession entity = votingSessionService.update(id, votingSession);
		return ResponseEntity.ok().body(entity);
	}
	
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> delete(@PathVariable Long id) {
		votingSessionService.delete(id);
		return ResponseEntity.noContent().build();
	}
}
