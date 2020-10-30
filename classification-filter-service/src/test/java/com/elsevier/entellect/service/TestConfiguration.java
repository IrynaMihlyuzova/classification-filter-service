package com.elsevier.entellect.service;

import static java.util.Arrays.asList;
import static org.springframework.core.Ordered.HIGHEST_PRECEDENCE;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

import com.amazonaws.services.sqs.AmazonSQS;
import com.elsevier.ces.claimcheck.ClaimCheckNotificationSender;
import com.elsevier.ces.claimcheck.impl.SqsClaimCheckNotificationSender;

@Configuration
public class TestConfiguration {

	@Bean
	@Order(HIGHEST_PRECEDENCE)
	public ClaimCheckNotificationSender localServerSender(AmazonSQS sqs) {
		return new SqsClaimCheckNotificationSender(sqs, asList("http://", "https://"));
	}
}
