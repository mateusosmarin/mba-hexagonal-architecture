package br.com.fullcycle.hexagonal.infrastructure.dtos;

public record CreateEventDTO(String name, String date, Integer totalSpots, String partnerId) {
}
