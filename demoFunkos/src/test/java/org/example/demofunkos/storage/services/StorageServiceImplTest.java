package org.example.demofunkos.storage.services;

import org.example.demofunkos.storage.exceptions.StorageNotFound;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.Resource;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ExtendWith(MockitoExtension.class)
class StorageServiceImplTest {

    @Mock
    private MultipartFile multipartFile;

    private Path mockRootLocation;

    private StorageServiceImpl storageServiceImpl;

    @BeforeEach
    void setUp() {
        mockRootLocation = Paths.get("test_imgs");
        storageServiceImpl = new StorageServiceImpl(mockRootLocation.toString());
    }

    @Test
    void init() {
        storageServiceImpl.init();
        assertTrue(Files.exists(mockRootLocation));
    }

    @Test
    void store() throws IOException {
        String filename = "test-image3.png";
        Files.createFile(mockRootLocation.resolve("test-image3.png"));
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
        Files.createDirectories(mockRootLocation);

        Stream<Path> files = storageServiceImpl.loadAll();
        assertEquals(2, files.count());
    }

    @Test
    void load() {
        Path path = storageServiceImpl.load("test-image.png");
        assertEquals(mockRootLocation.resolve("test-image.png"), path);
    }

    @Test
    void loadAsResource() {
        Resource returnedResource = storageServiceImpl.loadAsResource("test-image3.png");
        assertNotNull(returnedResource);
    }

    @Test
    void loadAsResourceNotFound() {
        assertThrows(StorageNotFound.class, () -> storageServiceImpl.loadAsResource("image.png"));
    }

    @Test
    void delete() throws IOException {
        Files.createDirectories(mockRootLocation);
        Files.createFile(mockRootLocation.resolve("test-image9.png"));

        storageServiceImpl.delete("test-image9.png");
        assertFalse(Files.exists(mockRootLocation.resolve("test-image9.png")));
    }

    @Test
    void deleteAll() throws IOException {

        Files.createDirectories(mockRootLocation);
        Files.createFile(mockRootLocation.resolve("test-image10.png"));
        Files.createFile(mockRootLocation.resolve("test-image11.png"));


        storageServiceImpl.deleteAll();
        assertEquals(0, Files.list(mockRootLocation).count());


        assertTrue(Files.exists(mockRootLocation));
    }

    @Test
    void getUrl() {

        String filename = "test-image.png";

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/files/" + filename);

        ServletRequestAttributes attributes = new ServletRequestAttributes(request);
        RequestContextHolder.setRequestAttributes(attributes);
        String url = storageServiceImpl.getUrl(filename);

        assertTrue(url.contains("/files/"));
        assertTrue(url.contains(filename));

        RequestContextHolder.resetRequestAttributes();
    }
}