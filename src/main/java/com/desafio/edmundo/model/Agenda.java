package com.desafio.edmundo.model;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "tb_agenda")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Agenda extends BaseEntity {
	private static final long serialVersionUID = 1L;

	@NotNull(message = "Add a subject to the agenda")
	private String subject;
	
	@OneToOne(mappedBy = "agenda", cascade = CascadeType.ALL)
	@JsonIdentityReference(alwaysAsId = true)
	@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
	private VotingSession votingSession;
	
	@JsonProperty(access = Access.READ_ONLY)
	private String votingResult;
	
	public void copyToEntity(Agenda obj) {
		this.subject = obj.getSubject();
		this.votingSession = obj.getVotingSession();
	}
}
