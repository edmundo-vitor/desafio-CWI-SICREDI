package com.desafio.edmundo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.desafio.edmundo.model.Vote;

public interface VoteRepository extends JpaRepository<Vote, Long>{

}
