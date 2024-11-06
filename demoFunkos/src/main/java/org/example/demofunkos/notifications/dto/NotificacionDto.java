package org.example.demofunkos.notifications.dto;

public record NotificacionDto(
        Long id,
        String nombre,
        String categoria,
        Double precio,
        String createdAt,
        String updatedAt
) {
}