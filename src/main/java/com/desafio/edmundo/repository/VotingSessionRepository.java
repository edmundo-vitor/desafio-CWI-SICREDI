package com.desafio.edmundo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.desafio.edmundo.model.VotingSession;

public interface VotingSessionRepository extends JpaRepository<VotingSession, Long>{

}
