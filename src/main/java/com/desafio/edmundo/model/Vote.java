package com.desafio.edmundo.model;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import org.hibernate.validator.constraints.br.CPF;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

@Entity
@Table(name = "tb_vote")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Vote extends BaseEntity {
	private static final long serialVersionUID = 1L;

	@CPF(message = "Invalid CPF")
	private String CPF;
	
	@Enumerated(EnumType.STRING)
	private OptionVote vote;
	
	@ManyToOne(cascade = CascadeType.ALL)
	@JsonIdentityReference(alwaysAsId = true)
	@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
	private VotingSession votingSession;
	
	public void copyToEntity(Vote entity) {
		this.CPF = entity.getCPF();
		this.vote = entity.getVote();
		this.votingSession = entity.getVotingSession();
	}
}
