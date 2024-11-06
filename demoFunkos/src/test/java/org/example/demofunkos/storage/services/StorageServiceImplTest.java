package org.example.demofunkos.storage.services;

import org.example.demofunkos.storage.exceptions.StorageException;
import org.example.demofunkos.storage.exceptions.StorageNotFound;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class StorageServiceImplTest {

    @Mock
    private MultipartFile multipartFile;

    @InjectMocks
    private StorageServiceImpl storageServiceImpl;

    private final Path rootLocation = Paths.get("imgs");

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        storageServiceImpl = new StorageServiceImpl(rootLocation.toString());
    }

    @Test
    void init() {
        storageServiceImpl.init();
        assertTrue(Files.exists(rootLocation));
    }

    @Test
    void store() throws IOException {
        String filename = "test-image.png";
        when(multipartFile.getOriginalFilename()).thenReturn(filename);
        when(multipartFile.isEmpty()).thenReturn(false);
        when(multipartFile.getInputStream()).thenReturn(mock(InputStream.class));

        String storedFilename = storageServiceImpl.store(multipartFile);
        assertTrue(storedFilename.contains("test-image"));
        verify(multipartFile, times(1)).getInputStream();
    }

    @Test
    void storeEmptyFile() {
        String filename = "test-image.png";
        when(multipartFile.getOriginalFilename()).thenReturn(filename);
        when(multipartFile.isEmpty()).thenReturn(true);

        assertThrows(StorageNotFound.class, () -> storageServiceImpl.store(multipartFile));
    }

    @Test
    void storeFileWithRelativePath() {
        String filename = "../test-image.png";
        when(multipartFile.getOriginalFilename()).thenReturn(filename);
        when(multipartFile.isEmpty()).thenReturn(false);

        assertThrows(StorageNotFound.class, () -> storageServiceImpl.store(multipartFile));
    }

    @Test
    void loadAll() throws IOException {
        Files.createDirectories(rootLocation);
        Files.createFile(rootLocation.resolve("test-image.png"));
        Files.createFile(rootLocation.resolve("test-image2.png"));

        Stream<Path> files = storageServiceImpl.loadAll();
        assertEquals(2, files.count());
    }

    @Test
    void load() {
        Path path = storageServiceImpl.load("test-image.png");
        assertEquals(rootLocation.resolve("test-image.png"), path);
    }

    @Test
    void loadAsResource() throws MalformedURLException {
        Path path = rootLocation.resolve("test-image.png");
        Resource resource = new UrlResource(path.toUri());

        when(resource.exists()).thenReturn(true);
        when(resource.isReadable()).thenReturn(true);

        Resource returnedResource = storageServiceImpl.loadAsResource("test-image.png");
        assertNotNull(returnedResource);
    }

    @Test
    void loadAsResourceNotFound() throws MalformedURLException {
        Path path = rootLocation.resolve("test-image.png");
        Resource resource = new UrlResource(path.toUri());

        when(resource.exists()).thenReturn(false);
        when(resource.isReadable()).thenReturn(false);

        assertThrows(StorageNotFound.class, () -> storageServiceImpl.loadAsResource("test-image.png"));
    }

    @Test
    void delete() throws IOException {
        Files.createDirectories(rootLocation);
        Files.createFile(rootLocation.resolve("test-image.png"));

        storageServiceImpl.delete("test-image.png");
        assertFalse(Files.exists(rootLocation.resolve("test-image.png")));
    }

    @Test
    void deleteAll() throws IOException {
        Files.createDirectories(rootLocation);
        Files.createFile(rootLocation.resolve("test-image1.png"));
        Files.createFile(rootLocation.resolve("test-image2.png"));

        storageServiceImpl.deleteAll();
        assertEquals(0, Files.list(rootLocation).count());
    }

    @Test
    void getUrl() {
        String url = storageServiceImpl.getUrl("test-image.png");
        assertTrue(url.contains("test-image.png"));
    }
}
