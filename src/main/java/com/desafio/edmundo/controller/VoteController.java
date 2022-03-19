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

import com.desafio.edmundo.model.Vote;
import com.desafio.edmundo.service.VoteService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/votes")
@Api(tags = "Votes")
public class VoteController {
	
	@Autowired
	private VoteService voteService;

	@GetMapping
	@ApiOperation(value = "Search all votes in a paged search", produces = "application/json")
	public ResponseEntity<Page<Vote>> findAllPaged(Pageable pageable) {
		Page<Vote> votePage = voteService.findAllPaged(pageable);
		return ResponseEntity.ok().body(votePage);
	}
	
	@GetMapping("/{id}")
	@ApiOperation(value = "Search for a vote by ID", produces = "application/json")
	public ResponseEntity<Vote> findById(@PathVariable Long id) {
		Vote entity = voteService.findById(id);
		return ResponseEntity.ok().body(entity);
	}
	
	@PostMapping
	@ApiOperation(value = "Register a vote", consumes = "application/json", produces = "application/json")
	public ResponseEntity<Vote> save(@RequestBody @Valid Vote vote) {
		Vote entity = voteService.save(vote);
		URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(entity.getId()).toUri();
		return ResponseEntity.created(uri).body(entity);
	}
	
	@PutMapping("/{id}")
	@ApiOperation(value = "Edit a vote", consumes = "application/json", produces = "application/json")
	public ResponseEntity<Vote> update(@PathVariable Long id, @RequestBody @Valid Vote vote) {
		Vote entity = voteService.update(id, vote);
		return ResponseEntity.ok().body(entity);
	}
	
	@DeleteMapping("/{id}")
	@ApiOperation(value = "Delete a vote by ID")
	public ResponseEntity<Void> delete(@PathVariable Long id) {
		voteService.delete(id);
		return ResponseEntity.noContent().build();
	}
}
