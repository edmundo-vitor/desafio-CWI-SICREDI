package com.desafio.edmundo.util;

import java.util.Set;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.desafio.edmundo.connection.RabbitMQConstants;
import com.desafio.edmundo.model.OptionVote;
import com.desafio.edmundo.model.Vote;
import com.desafio.edmundo.model.VotingSession;
import com.desafio.edmundo.repository.VotingSessionRepository;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Component
@AllArgsConstructor
@NoArgsConstructor
public class VotingSessionThread implements Runnable {
	
	private VotingSession votingSession;
	
	@Autowired
	private VotingSessionRepository votingSessionRepository;
	
	@Autowired
	private RabbitTemplate rabbitTemplate;

	@Override
	public void run() {
		try {
			// Seta o tempo de votação da sessão
			Long votingSessionTimeInMilis = votingSession.getTimeOpen().toSecondOfDay() * 1000L;
			Thread.sleep(votingSessionTimeInMilis);
			
			// Busca a sessão de votação para verificar o resultado
			VotingSession votingSessionEntity = votingSessionRepository.findById(votingSession.getId()).get();
			
			// Encerra a cessão de votação alterando o status
			votingSessionEntity.setOpen(false);
			
			// Apura o resultado da votação
			int votesYes = 0;
			int votesNo = 0;
			
			Set<Vote> votes = votingSessionEntity.getVotes();
			
			for(Vote vote: votes) {
				if(vote.getVote() == OptionVote.Sim) {
					votesYes++;
				}else {
					votesNo++;
				}
			}
			
			// Adicona o resultado da votação a pauta
			String votingResultMessage = "Votes yes: " + votesYes + "\nVotes no: " + votesNo;
			votingSessionEntity.getAgenda().setVotingResult(votingResultMessage);
			
			// Salva os dados da sessão de votação
			votingSessionEntity = votingSessionRepository.save(votingSessionEntity);
			
			// Mensagem para enviar para a fila
			String responseMessage = "Agenda ID: " + votingSessionEntity.getAgenda().getId() + "\n"
					+ "Votes yes: " + votesYes + "\n"
					+ "Votes no: " + votesNo;
			
			// Manda a mensagem para a fila do RabbitMQ
			rabbitTemplate.convertAndSend(RabbitMQConstants.QUEUE_VOTING_RESULT, responseMessage);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}
