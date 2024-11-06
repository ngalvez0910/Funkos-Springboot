package org.example.demofunkos.funkos.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.example.demofunkos.categoria.models.Categoria;
import org.example.demofunkos.funkos.dto.FunkoDto;
import org.example.demofunkos.funkos.mappers.FunkoMapper;
import org.example.demofunkos.funkos.models.Funko;
import org.example.demofunkos.funkos.services.FunkoServiceImpl;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(MockitoExtension.class)
class FunkoControllerTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockBean
    FunkoServiceImpl service;

    @Autowired
    MockMvc mvc;

    FunkoMapper mapper = new FunkoMapper();
    Funko funkoTest = new Funko();
    Categoria categoriaTest = new Categoria();
    String myEndpoint = "/funkos";

    @Autowired
    private FunkoControllerTest(FunkoServiceImpl service) {
        this.service = service;
    }

    @BeforeEach
    void setUp() {
        categoriaTest.setId(UUID.fromString("12d45756-3895-49b2-90d3-c4a12d5ee081"));
        categoriaTest.setNombre("PELICULA");
        categoriaTest.setActivado(true);
        objectMapper.registerModule(new JavaTimeModule());

        funkoTest.setId(1L);
        funkoTest.setNombre("Darth Vader");
        funkoTest.setPrecio(10.99);
        funkoTest.setCategoria(categoriaTest);
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Test
    void getAll() throws Exception {
        when(service.getAll()).thenReturn(List.of(funkoTest));

        MockHttpServletResponse response = mvc.perform(
                get(myEndpoint)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        List<Funko> res = objectMapper.readValue(response.getContentAsString(),
                objectMapper.getTypeFactory().constructCollectionType(List.class, Funko.class));

        assertAll(
                () -> assertEquals(response.getStatus(), HttpStatus.OK.value()),
                () -> assertFalse(res.isEmpty()),
                () -> assertTrue(res.stream().anyMatch(r -> r.getId().equals(funkoTest.getId())))
        );

        verify(service, times(1)).getAll();
    }

    @Test
    void getById() throws Exception {
        when(service.getById(String.valueOf(1L))).thenReturn(funkoTest);

        MockHttpServletResponse response = mvc.perform(
                get(myEndpoint + "/1")
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        Funko res = objectMapper.readValue(response.getContentAsString(), Funko.class);

        assertAll(
                () -> assertEquals(response.getStatus(), HttpStatus.OK.value()),
                () -> assertEquals(res.getId(), funkoTest.getId()),
                () -> assertEquals(res.getNombre(), funkoTest.getNombre()),
                () -> assertEquals(res.getPrecio(), funkoTest.getPrecio()),
                () -> assertEquals(res.getCategoria(), funkoTest.getCategoria())
        );

        verify(service, times(1)).getById(String.valueOf(1L));
    }

    @Test
    void save() throws Exception {
        Categoria nuevaCategoria = new Categoria();
        nuevaCategoria.setId(UUID.fromString("5790bdd4-8898-4c61-b547-bc26952dc2a3"));
        nuevaCategoria.setNombre("DISNEY");
        nuevaCategoria.setActivado(true);

        FunkoDto nuevoFunko = new FunkoDto();
        nuevoFunko.setNombre("Mickey Mouse");
        nuevoFunko.setPrecio(7.95);
        nuevoFunko.setCategoria("DISNEY");

        when(service.save(nuevoFunko)).thenReturn(mapper.toFunko(nuevoFunko, nuevaCategoria));

        MockHttpServletResponse response = mvc.perform(
                        post(myEndpoint)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(nuevoFunko)))
                .andReturn().getResponse();

        Funko res = objectMapper.readValue(response.getContentAsString(), Funko.class);

        assertAll(
                () -> assertEquals(response.getStatus(), HttpStatus.CREATED.value()),
                () -> assertEquals(res.getId(), mapper.toFunko(nuevoFunko, nuevaCategoria).getId()),
                () -> assertEquals(res.getNombre(), nuevoFunko.getNombre()),
                () -> assertEquals(res.getPrecio(), nuevoFunko.getPrecio()),
                () -> assertEquals(res.getCategoria(), mapper.toFunko(nuevoFunko, nuevaCategoria).getCategoria())
        );

        verify(service, times(1)).save(nuevoFunko);
    }

    @Test
    void update() throws Exception {
        Categoria updatedCategoria = new Categoria();
        updatedCategoria.setId(UUID.fromString("5790bdd4-8898-4c61-b547-bc26952dc2a3"));
        updatedCategoria.setNombre("SUPERHEROE");
        updatedCategoria.setActivado(true);

        FunkoDto updateFunko = new FunkoDto();
        updateFunko.setNombre("Goku");
        updateFunko.setPrecio(15.99);
        updateFunko.setCategoria(updatedCategoria.getNombre());

        Funko funko = new Funko();
        funko.setNombre(updateFunko.getNombre());
        funko.setPrecio(updateFunko.getPrecio());
        funko.setCategoria(updatedCategoria);

        when(service.update(String.valueOf(2L), updateFunko)).thenReturn(mapper.toFunkoUpdate(updateFunko, funko, updatedCategoria));

        MockHttpServletResponse response = mvc.perform(
                put(myEndpoint + "/2")
                       .contentType(MediaType.APPLICATION_JSON)
                       .content(objectMapper.writeValueAsString(updateFunko)))
               .andReturn().getResponse();

        Funko res = objectMapper.readValue(response.getContentAsString(), Funko.class);

        assertAll(
                () -> assertEquals(response.getStatus(), HttpStatus.OK.value()),
                () -> assertEquals(res.getNombre(), updateFunko.getNombre()),
                () -> assertEquals(res.getPrecio(), updateFunko.getPrecio()),
                () -> assertEquals(res.getCategoria().getNombre(), updateFunko.getCategoria())
        );

        verify(service, times(1)).update(String.valueOf(2L), updateFunko);
    }

    @Test
    void delete() throws Exception {
        when(service.delete(String.valueOf(1L))).thenReturn(funkoTest);

        MockHttpServletResponse response = mvc.perform(
                MockMvcRequestBuilders.delete(myEndpoint + "/1")
                       .accept(MediaType.APPLICATION_JSON))
               .andReturn().getResponse();

        assertEquals(response.getStatus(), HttpStatus.OK.value());

        verify(service, times(1)).delete(String.valueOf(1L));
    }
}