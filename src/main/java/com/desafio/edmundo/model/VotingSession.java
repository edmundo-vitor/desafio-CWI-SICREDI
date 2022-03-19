package com.desafio.edmundo.model;

import java.time.LocalTime;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.PrePersist;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import lombok.AllArgsConstructor;

import lombok.Setter;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "tb_voting_session")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class VotingSession extends BaseEntity {
	private static final long serialVersionUID = 1L;

	private LocalTime timeOpen;
	
	@JsonProperty(access = Access.READ_ONLY)
	private boolean open;
	
	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "agenda_id", referencedColumnName = "id")
	@JsonIdentityReference(alwaysAsId = true)
	@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
	private Agenda agenda;
	
	@OneToMany(cascade = CascadeType.ALL, mappedBy = "votingSession", fetch = FetchType.EAGER)
	@JsonIdentityReference(alwaysAsId = true)
	@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
	private Set<Vote> votes = new HashSet<>();
	
	@PrePersist
	public void preSave() {
		this.open = true;
	}
	
	public void copyToEntity(VotingSession obj) {
		this.timeOpen = obj.getTimeOpen();
		this.open = obj.isOpen();
		this.agenda = obj.getAgenda();
		this.votes = obj.getVotes();
	}
}
