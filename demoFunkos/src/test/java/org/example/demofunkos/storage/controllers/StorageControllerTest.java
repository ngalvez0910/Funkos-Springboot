package org.example.demofunkos.storage.controllers;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.example.demofunkos.storage.services.StorageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ByteArrayResource;

import java.nio.file.Paths;
import java.util.stream.Stream;

@WebMvcTest(StorageController.class)
@ExtendWith(MockitoExtension.class)
class StorageControllerTest {
    @InjectMocks
    private StorageController storageController;

    @Mock
    private StorageService storageService;

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(storageController).build();
    }

    @Test
    void shouldReturnUploadFormViewWhenListingUploadedFiles() throws Exception {
        when(storageService.loadAll()).thenReturn(Stream.of(Paths.get("file1.txt"), Paths.get("file2.txt")));

        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("uploadForm"))
                .andExpect(model().attributeExists("files"));
    }

    @Test
    void shouldReturnCorrectContentDispositionHeaderForFileDownload() throws Exception {
        Resource mockResource = new ByteArrayResource("file content".getBytes());
        when(storageService.loadAsResource(anyString())).thenReturn(mockResource);

        mockMvc.perform(get("/files/testfile.txt"))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"testfile.txt\""))
                .andExpect(content().bytes("file content".getBytes()));
    }

    @Test
    void shouldCallStorageServiceStoreWithCorrectFileDuringUpload() throws Exception {
        MockMultipartFile mockFile = new MockMultipartFile(
                "file",
                "testfile.txt",
                MediaType.TEXT_PLAIN_VALUE,
                "test content".getBytes()
        );

        mockMvc.perform(multipart("/").file(mockFile))
                .andExpect(status().is3xxRedirection())
                .andExpect(header().string("Location", "/"));

        verify(storageService).store(mockFile);
    }
}