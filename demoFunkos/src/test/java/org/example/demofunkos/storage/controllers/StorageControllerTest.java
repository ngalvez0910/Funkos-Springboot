package org.example.demofunkos.storage.controllers;

import jakarta.servlet.http.HttpServletRequest;
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
import java.io.IOException;

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
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void serveFile() throws Exception {
        String filename = "test-image.png";
        String filePath = "imgs/test-image.png";

        when(storageService.loadAsResource(filename)).thenReturn(resource);
        when(request.getServletContext().getMimeType(filePath)).thenReturn(MediaType.IMAGE_JPEG_VALUE);
        when(resource.getFile()).thenReturn(new File(filePath));

        MockHttpServletResponse response = mvc.perform(
                        get(endpoint + "/" + filename)
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        assertAll(
                () -> assertEquals(HttpStatus.OK.value(), response.getStatus()),
                () -> assertEquals(MediaType.IMAGE_JPEG_VALUE, response.getContentType()),
                () -> assertTrue(response.getContentLength() > 0)
        );

        verify(storageService, times(1)).loadAsResource(filename);
    }

    @Test
    void serveFileWithUnknownMimeType() throws Exception {
        String filename = "test-image.png";
        String filePath = "imgs/test-image.png";

        when(storageService.loadAsResource(filename)).thenReturn(resource);
        when(resource.getFile()).thenReturn(new File(filePath));
        when(request.getServletContext().getMimeType(filePath)).thenReturn(null);

        MockHttpServletResponse response = mvc.perform(
                        get(endpoint + "/" + filename)
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        assertAll(
                () -> assertEquals(HttpStatus.OK.value(), response.getStatus()),
                () -> assertEquals(MediaType.APPLICATION_OCTET_STREAM_VALUE, response.getContentType()),
                () -> assertEquals(resource, response.getContentLength() > 0)
        );

        verify(storageService, times(1)).loadAsResource(filename);
    }

    @Test
    void serveFileThrowsException() throws Exception {
        String filename = "test-image.png";

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
