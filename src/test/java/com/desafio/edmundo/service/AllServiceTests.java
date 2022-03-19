package com.desafio.edmundo.service;

import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectClasses({AgendaServiceImplTest.class, VoteServiceImplTest.class, VotingSessionServiceImplTest.class})
public class AllServiceTests {
	
}
