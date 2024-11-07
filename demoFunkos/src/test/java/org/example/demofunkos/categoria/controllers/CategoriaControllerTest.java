package org.example.demofunkos.categoria.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.example.demofunkos.categoria.dto.CategoriaDto;
import org.example.demofunkos.categoria.mappers.CategoriaMapper;
import org.example.demofunkos.categoria.models.Categoria;
import org.example.demofunkos.categoria.services.CategoriaServiceImpl;
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

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(MockitoExtension.class)
class CategoriaControllerTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockBean
    CategoriaServiceImpl service;

    @Autowired
    MockMvc mvc;

    CategoriaMapper mapper = new CategoriaMapper();
    Categoria categoriaTest = new Categoria();
    String myEndpoint = "/categorias";

    @Autowired
    private CategoriaControllerTest(CategoriaServiceImpl service) {
        this.service = service;
    }

    @BeforeEach
    void setUp() {
        categoriaTest.setId(UUID.fromString("12d45756-3895-49b2-90d3-c4a12d5ee081"));
        categoriaTest.setNombre("DISNEY");
        categoriaTest.setCreatedAt(LocalDateTime.now());
        categoriaTest.setUpdatedAt(LocalDateTime.now());
        categoriaTest.setActivado(true);
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Test
    void getAll() throws Exception {
        when(service.getAll()).thenReturn(List.of(categoriaTest));

        MockHttpServletResponse response = mvc.perform(
                        get(myEndpoint)
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        List<Categoria> res = objectMapper.readValue(response.getContentAsString(),
                objectMapper.getTypeFactory().constructCollectionType(List.class, Categoria.class));

        assertAll(
                () -> assertEquals(response.getStatus(), HttpStatus.OK.value()),
                () -> assertFalse(res.isEmpty()),
                () -> assertTrue(res.stream().anyMatch(r -> r.getId().equals(categoriaTest.getId())))
        );

        verify(service, times(1)).getAll();
    }

    @Test
    void getById() throws Exception {
        when(service.getById(UUID.fromString("12d45756-3895-49b2-90d3-c4a12d5ee081"))).thenReturn(categoriaTest);

        MockHttpServletResponse response = mvc.perform(
                        get(myEndpoint + "/12d45756-3895-49b2-90d3-c4a12d5ee081")
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        Categoria res = objectMapper.readValue(response.getContentAsString(), Categoria.class);

        assertAll(
                () -> assertEquals(response.getStatus(), HttpStatus.OK.value()),
                () -> assertEquals(res.getId(), categoriaTest.getId()),
                () -> assertEquals(res.getNombre(), categoriaTest.getNombre()),
                () -> assertEquals(res.getActivado(), categoriaTest.getActivado())
        );

        verify(service, times(1)).getById(UUID.fromString("12d45756-3895-49b2-90d3-c4a12d5ee081"));
    }

    @Test
    void save() throws Exception {
        CategoriaDto nuevoCategoria = new CategoriaDto();
        nuevoCategoria.setNombre("DISNEY");
        nuevoCategoria.setActivado(true);

        when(service.save(nuevoCategoria)).thenReturn(mapper.fromDto(nuevoCategoria));

        MockHttpServletResponse response = mvc.perform(
                        post(myEndpoint)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(nuevoCategoria)))
                .andReturn().getResponse();

        Categoria res = objectMapper.readValue(response.getContentAsString(), Categoria.class);

        assertAll(
                () -> assertEquals(response.getStatus(), HttpStatus.CREATED.value()),
                () -> assertEquals(res.getId(), mapper.fromDto(nuevoCategoria).getId()),
                () -> assertEquals(res.getNombre(), mapper.fromDto(nuevoCategoria).getNombre()),
                () -> assertEquals(res.getActivado(), nuevoCategoria.getActivado())
        );

        verify(service, times(1)).save(nuevoCategoria);
    }

    @Test
    void update() throws Exception {
        CategoriaDto updatedCategoria = new CategoriaDto();
        updatedCategoria.setNombre("SUPERHEROES");
        updatedCategoria.setActivado(true);

        Categoria expectedCategoria = new Categoria();
        expectedCategoria.setId(UUID.fromString("12d45756-3895-49b2-90d3-c4a12d5ee081"));
        expectedCategoria.setNombre("SUPERHEROES");
        expectedCategoria.setActivado(true);

        when(service.update(UUID.fromString("12d45756-3895-49b2-90d3-c4a12d5ee081"), updatedCategoria)).thenReturn(expectedCategoria);

        MockHttpServletResponse response = mvc.perform(
                        patch(myEndpoint + "/12d45756-3895-49b2-90d3-c4a12d5ee081")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(updatedCategoria)))
                .andReturn().getResponse();

        assertEquals(HttpStatus.OK.value(), response.getStatus());

        Categoria res = objectMapper.readValue(response.getContentAsString(), Categoria.class);
        assertAll(
                () -> assertEquals(res.getId(), expectedCategoria.getId()),
                () -> assertEquals(res.getNombre(), expectedCategoria.getNombre()),
                () -> assertEquals(res.getActivado(), expectedCategoria.getActivado())
        );

        verify(service, times(1)).update(UUID.fromString("12d45756-3895-49b2-90d3-c4a12d5ee081"), updatedCategoria);
    }


    @Test
    void delete() throws Exception {
        CategoriaDto deletedCategoria = new CategoriaDto();
        deletedCategoria.setNombre("SUPERHEROES");
        deletedCategoria.setActivado(true);

        when(service.update(UUID.fromString("12d45756-3895-49b2-90d3-c4a12d5ee081"), deletedCategoria)).thenReturn(mapper.fromDto(deletedCategoria));

        MockHttpServletResponse response = mvc.perform(
                        patch(myEndpoint + "/12d45756-3895-49b2-90d3-c4a12d5ee081")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(deletedCategoria)))
                .andReturn().getResponse();

        assertEquals(HttpStatus.OK.value(), response.getStatus());

        Categoria res = objectMapper.readValue(response.getContentAsString(), Categoria.class);

        assertAll(
                () -> assertEquals(response.getStatus(), HttpStatus.OK.value()),
                () -> assertEquals(res.getId(), mapper.fromDto(deletedCategoria).getId()),
                () -> assertEquals(res.getNombre(), mapper.fromDto(deletedCategoria).getNombre()),
                () -> assertEquals(res.getActivado(), deletedCategoria.getActivado())
        );

        verify(service, times(1)).update(UUID.fromString("12d45756-3895-49b2-90d3-c4a12d5ee081"), deletedCategoria);
    }

    @Test
    void nombreIsBlank() throws Exception {
        CategoriaDto nuevoCategoria = new CategoriaDto();
        nuevoCategoria.setNombre("");
        nuevoCategoria.setActivado(true);

        MockHttpServletResponse response = mvc.perform(
                        post(myEndpoint)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(nuevoCategoria)))
                .andReturn().getResponse();

        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());

        String responseContent = response.getContentAsString();

        assertTrue(responseContent.contains("El nombre no puede estar vacio"));
    }

    @Test
    void testValidationExceptionHandler() throws Exception {
        mvc.perform(post(myEndpoint)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"nombre\": \"\" }"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.nombre").value("El nombre no puede estar vacio"))
                .andReturn();
    }
}