package org.example.demofunkos.storage.controllers;

import jakarta.servlet.http.HttpServletRequest;
import org.example.demofunkos.storage.exceptions.StorageBadRequest;
import org.example.demofunkos.storage.exceptions.StorageException;
import org.example.demofunkos.storage.services.StorageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(MockitoExtension.class)
class StorageControllerTest {

    @MockBean
    private StorageService storageService;

    @Autowired
    private MockMvc mvc;

    private final String endpoint = "/funkos/files";

    @MockBean
    private HttpServletRequest request;

    @MockBean
    private Resource resource;

    @BeforeEach
    void setUp() throws IOException {
        MockitoAnnotations.openMocks(this);

        Path sourcePath = Paths.get("test_imgs", "test-image20.png");
        if (Files.notExists(sourcePath)) {
            Files.createDirectories(sourcePath.getParent());
            Files.createFile(sourcePath);
        }

        Path destination = Paths.get("imgs/test-image20.png");
        Files.createDirectories(destination.getParent());

        Files.copy(sourcePath, destination, StandardCopyOption.REPLACE_EXISTING);
    }


    @Test
    void serveFile() throws Exception {
        String filename = "test-image20.png";

        Resource resource = mock(Resource.class);
        File testFile = Files.createFile(Path.of("test_imgs/test-image20.png")).toFile();
        when(storageService.loadAsResource(filename)).thenReturn(resource);
        when(resource.getInputStream()).thenReturn(new FileInputStream(testFile));

        MockHttpServletResponse response = mvc.perform(
                        get(endpoint + "/" + filename)
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        assertAll(
                () -> assertEquals(HttpStatus.OK.value(), response.getStatus()),
                () -> assertTrue(response.getContentLength() > 0)
        );

        verify(storageService, times(1)).loadAsResource(filename);
    }

    @Test
    void serveFileThrowsException() throws Exception {
        String filename = "test-image20.kei";

        when(storageService.loadAsResource(filename)).thenReturn(resource);
        when(resource.getFile()).thenThrow(new IOException());

        MockHttpServletResponse response = mvc.perform(
                        get(endpoint + "/" + filename)
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        assertAll(
                () -> assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus()),
                () -> assertTrue(response.getContentAsString().contains("No se puede determinar el tipo de fichero"))
        );

        verify(storageService, times(1)).loadAsResource(filename);
    }
}
