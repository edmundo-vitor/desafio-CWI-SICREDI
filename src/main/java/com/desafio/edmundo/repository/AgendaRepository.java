package com.desafio.edmundo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.desafio.edmundo.model.Agenda;

public interface AgendaRepository extends JpaRepository<Agenda, Long>{

}
