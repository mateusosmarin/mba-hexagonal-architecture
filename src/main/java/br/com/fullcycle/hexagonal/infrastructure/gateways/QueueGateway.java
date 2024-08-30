package br.com.fullcycle.hexagonal.infrastructure.gateways;

public interface QueueGateway {
    void publish(String message);
}
