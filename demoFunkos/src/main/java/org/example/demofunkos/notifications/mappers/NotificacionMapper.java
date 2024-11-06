package org.example.demofunkos.notifications.mappers;

import org.example.demofunkos.funkos.models.Funko;
import org.example.demofunkos.notifications.dto.NotificacionDto;
import org.springframework.stereotype.Component;

@Component
public class NotificacionMapper {
    public NotificacionDto toNotificationDto(Funko funko) {
        return new NotificacionDto(
                funko.getId(),
                funko.getNombre(),
                funko.getCategoria().getNombre(),
                funko.getPrecio(),
                funko.getCreatedAt().toString(),
                funko.getUpdatedAt().toString()
        );
    }
}
