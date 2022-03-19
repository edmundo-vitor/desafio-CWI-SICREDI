package com.desafio.edmundo.connection;

import javax.annotation.PostConstruct;

import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.stereotype.Component;



@Component
public class RabbitMQConnection {
	
	private static final String EXCHANGE_NAME = "amq.direct"; 
	
	private AmqpAdmin amqpAdmin;
	
	public RabbitMQConnection(AmqpAdmin amqpAdmin) {
		this.amqpAdmin = amqpAdmin;
	}

	private Queue queue(String queueName) {
		return new Queue(queueName, true, false, false);
	}
	
	private DirectExchange directExchange() {
		return new DirectExchange(EXCHANGE_NAME);
	}
	
	private Binding bindQueueExchange(Queue queue, DirectExchange directExchange) {
		return new Binding(queue.getName(), Binding.DestinationType.QUEUE, directExchange.getName(), queue.getName(), null);
	}
	
	@PostConstruct
	private void add() {
		Queue queueVotingResult = queue(RabbitMQConstants.QUEUE_VOTING_RESULT);
		DirectExchange directExchange = directExchange();
		Binding bindingVotingResultDirectExchange = bindQueueExchange(queueVotingResult, directExchange);
		
		// Criando a fila no RabbitMQ
		amqpAdmin.declareQueue(queueVotingResult);
		amqpAdmin.declareExchange(directExchange);
		amqpAdmin.declareBinding(bindingVotingResultDirectExchange);
	}
}
