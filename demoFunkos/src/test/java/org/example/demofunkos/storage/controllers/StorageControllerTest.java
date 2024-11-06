package org.example.demofunkos.storage.controllers;

import org.example.demofunkos.storage.exceptions.StorageNotFound;
import org.example.demofunkos.storage.services.StorageService;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class StorageControllerTest {

    @MockBean
    private StorageService storageService;

    @Autowired
    private MockMvc mvc;

    private String myEndpoint = "/files";
    private File myFile;

    @BeforeEach
    void setUp() {
        try {
            myFile = File.createTempFile("data", "uno.txt");
        } catch (IOException e) {
            fail("Failed to create temporary file: " + e.getMessage());
        }
    }

    @Test
    void getAllFiles() throws Exception {
        given(storageService.loadAll()).willReturn(Stream.of(Paths.get(myFile.toURI())));

        MockHttpServletResponse response = mvc.perform(get(myEndpoint))
                .andExpect(status().isOk())
                .andExpect(model().attribute("files", Matchers.contains("http://localhost/files/uno.txt")))
                .andReturn().getResponse();

        assertAll(
                () -> assertEquals(HttpStatus.OK.value(), response.getStatus()),
                () -> assertTrue(response.getContentAsString().contains("http://localhost/files/uno.txt"))
        );
    }

    @Test
    void listUploadedFiles_returnsEmptyGetAll() throws Exception {
        given(storageService.loadAll()).willReturn(Stream.empty());

        MockHttpServletResponse response = mvc.perform(get(myEndpoint))
                .andExpect(status().isOk())
                .andExpect(model().attribute("files", Matchers.empty()))
                .andReturn().getResponse();

        assertAll(
                () -> assertEquals(HttpStatus.OK.value(), response.getStatus()),
                () -> assertTrue(response.getContentAsString().contains("[]"))
        );
    }

    @Test
    void getFileByName() throws Exception {
        given(storageService.loadAsResource("uno.txt")).willReturn(new FileSystemResource(myFile));
    
        MockHttpServletResponse response = mvc.perform(get("/files/uno.txt"))
                .andExpect(status().isOk())
                .andReturn().getResponse();
    
        assertAll(
                () -> assertEquals(HttpStatus.OK.value(), response.getStatus()),
                () -> assertTrue(response.getContentAsString().contains("data"))
        );
    }

    /*
    @Test
    void handleFileUpload() throws Exception {
        given(storageService.store(myFile)).willReturn(Paths.get(myFile.toURI()));

        MockHttpServletResponse response = mvc.perform(post(myEndpoint)
                        .param("file", myFile.getName()))
                .andExpect(status().isCreated())
                .andReturn().getResponse();

        assertAll(
                () -> assertEquals(HttpStatus.CREATED.value(), response.getStatus()),
                () -> assertTrue(response.getContentAsString().contains("File uploaded"))
        );
    }

     */

    @Test
    public void should404WhenMissingFile() throws Exception {
        given(storageService.loadAsResource("test.txt")).willThrow(StorageNotFound.class);

        MockHttpServletResponse response = mvc.perform(get("/files/test.txt"))
                .andExpect(status().isNotFound())
                .andReturn().getResponse();

        assertAll(
                () -> assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus())
        );
    }
}