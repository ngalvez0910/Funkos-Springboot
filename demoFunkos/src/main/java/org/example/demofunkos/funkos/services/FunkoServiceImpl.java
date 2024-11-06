package org.example.demofunkos.funkos.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.example.demofunkos.categoria.services.CategoriaService;
import org.example.demofunkos.funkos.dto.FunkoDto;
import org.example.demofunkos.funkos.mappers.FunkoMapper;
import org.example.demofunkos.funkos.models.Funko;
import org.example.demofunkos.funkos.repositories.FunkoRepository;
import org.example.demofunkos.notifications.config.WebSocketConfig;
import org.example.demofunkos.notifications.config.WebSocketHandler;
import org.example.demofunkos.notifications.dto.NotificacionDto;
import org.example.demofunkos.notifications.mappers.NotificacionMapper;
import org.example.demofunkos.notifications.models.Notificacion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@CacheConfig(cacheNames = {"funkos"})
public class FunkoServiceImpl implements FunkoService{
    private final FunkoRepository repository;
    private final FunkoMapper mapper;
    private final CategoriaService categoriaService;
    private final WebSocketConfig webSocketConfig;
    private WebSocketHandler webSocketHandler;
    private final NotificacionMapper notificacionMapper;
    private ObjectMapper objectMapper;

    @Autowired
    public FunkoServiceImpl(FunkoRepository repository, FunkoMapper mapper, CategoriaService categoriaService, WebSocketConfig webSocketConfig, NotificacionMapper notificacionMapper) {
        this.repository = repository;
        this.mapper = mapper;
        this.categoriaService = categoriaService;
        this.webSocketConfig = webSocketConfig;
        webSocketHandler = webSocketConfig.webSocketFunkosHandler();
        objectMapper = new ObjectMapper();
        this.notificacionMapper = notificacionMapper;
    }

    @Override
    public List<Funko> getAll() {
        return repository.findAll();
    }

    @Cacheable
    @Override
    public Funko getById(Long id) {
        return repository.findById(id).orElseThrow(
            () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "El Funko con id " + id + " no se ha encontrado.")
        );
    }

    @CachePut
    @Override
    public Funko save(FunkoDto funkoDto) {
        var categoria = categoriaService.getByNombre(funkoDto.getCategoria().toUpperCase());
        var funkoSaved = repository.save(mapper.toFunko(funkoDto, categoria));
        onChange(Notificacion.Tipo.CREATE, funkoSaved);
        return funkoSaved;
    }

    @CachePut
    @Override
    public Funko update(Long id, FunkoDto funkoDto) {
        var res = repository.findById(id).orElseThrow(
            () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "El Funko con id " + id + " no se ha encontrado.")
        );
        var categoria = categoriaService.getByNombre(funkoDto.getCategoria());
        res.setNombre(funkoDto.getNombre());
        res.setPrecio(funkoDto.getPrecio());
        res.setCategoria(categoria);
        var funkoUpdated = repository.save(res);
        onChange(Notificacion.Tipo.UPDATE, funkoUpdated);
        return funkoUpdated;
    }

    @CacheEvict
    @Override
    public Funko delete(Long id) {
        Funko funko = repository.findById(id).orElseThrow(
            () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "El Funko con id " + id + " no se ha encontrado.")
        );
        repository.deleteById(id);
        onChange(Notificacion.Tipo.DELETE, funko);
        return funko;
    }

    void onChange(Notificacion.Tipo tipo, Funko data) {
        log.debug("Servicio de funkos onChange con tipo: " + tipo + " y datos: " + data);

        if (webSocketHandler == null) {
            log.warn("No se ha podido enviar la notificación a los clientes ws, no se ha encontrado el servicio");
            webSocketHandler = this.webSocketConfig.webSocketFunkosHandler();
        }

        try {
            Notificacion<NotificacionDto> notificacion = new Notificacion<>(
                    "FUNKOS",
                    tipo,
                    notificacionMapper.toNotificationDto(data),
                    LocalDateTime.now().toString()
            );

            String json = objectMapper.writeValueAsString((notificacion));

            log.info("Enviando mensaje a los clientes ws");
            Thread senderThread = new Thread(() -> {
                try {
                    webSocketHandler.sendMessage(json);
                } catch (Exception e) {
                    log.error("Error al enviar el mensaje a través del servicio WebSocket", e);
                }
            });
            senderThread.start();
        } catch (JsonProcessingException e) {
            log.error("Error al convertir la notificación a JSON", e);
        }
    }

    public void setWebSocketHandler(WebSocketHandler webSocketHandler) {
        this.webSocketHandler = webSocketHandler;
    }
}