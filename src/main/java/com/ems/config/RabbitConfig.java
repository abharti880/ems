
package com.ems.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    public static final String EMPLOYEE_EXCHANGE = "employee.exchange";
    public static final String LEAVE_EXCHANGE = "leave.exchange";

    @Value("${ems.queue.employee}")
    private String employeeQueue;

    @Value("${ems.queue.leave}")
    private String leaveQueue;

    @Bean
    public Queue employeeQueue() {
        return QueueBuilder.durable(employeeQueue).build();
    }

    @Bean
    public Queue leaveQueue() {
        return QueueBuilder.durable(leaveQueue).build();
    }

    @Bean
    public DirectExchange employeeExchange() {
        return new DirectExchange(EMPLOYEE_EXCHANGE);
    }

    @Bean
    public DirectExchange leaveExchange() {
        return new DirectExchange(LEAVE_EXCHANGE);
    }

    @Bean
    public Binding employeeBinding() {
        return BindingBuilder
                .bind(employeeQueue())
                .to(employeeExchange())
                .with("employee.created");
    }

    @Bean
    public Binding leaveBinding() {
        return BindingBuilder
                .bind(leaveQueue())
                .to(leaveExchange())
                .with("leave.updated");
    }

    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
