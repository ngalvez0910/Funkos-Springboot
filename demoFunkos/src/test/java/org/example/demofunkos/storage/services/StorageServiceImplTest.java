package org.example.demofunkos.storage.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.Resource;
import org.springframework.mock.web.MockMultipartFile;
import org.example.demofunkos.storage.config.StorageConfig;
import org.example.demofunkos.storage.exceptions.StorageException;
import org.example.demofunkos.storage.exceptions.StorageNotFound;
import java.net.URI;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class StorageServiceImplTest {

    @Mock
    private StorageConfig config;

    @InjectMocks
    private StorageServiceImpl storageService;

    @BeforeEach
    public void setUp() {
        storageService = new StorageServiceImpl(config);
    }

    @Test
public void testConstructorThrowsExceptionWhenLocationEmpty() {
    StorageConfig config = mock(StorageConfig.class);
    when(config.getLocation()).thenReturn("  ");

    assertThrows(StorageException.class, () -> {
        new StorageServiceImpl(config);
    });
}
@Test
public void testStoreShouldThrowStorageExceptionWhenFileIsEmpty() {
    // Arrange
    MockMultipartFile emptyFile = new MockMultipartFile("test.txt", "test.txt", "text/plain", new byte[0]);

    // Act & Assert
    StorageException exception = assertThrows(StorageException.class, () -> {
        storageService.store(emptyFile);
    });

    assertEquals("Failed to store empty file.", exception.getMessage());
}
@Test
public void testLoadAsResourceThrowsStorageNotFoundWhenFileNotExistsOrNotReadable() throws Exception {
    // Arrange
    String filename = "non-existent-file.txt";
    Path filePath = mock(Path.class);
    Resource resource = mock(Resource.class);
    when(storageService.load(filename)).thenReturn(filePath);
    when(filePath.toUri()).thenReturn(new URI("file:///non-existent-file.txt"));
    when(resource.exists()).thenReturn(false);
    when(resource.isReadable()).thenReturn(false);

    // Act & Assert
    StorageNotFound exception = assertThrows(StorageNotFound.class, () -> {
        storageService.loadAsResource(filename);
    });

    assertEquals("Could not read file: " + filename, exception.getMessage());
}
@Test
public void testDeleteAllShouldDeleteAllFilesAndDirectories() throws IOException {
    // Arrange
    Path testDir = Files.createTempDirectory("test-storage");
    Path testFile = testDir.resolve("test-file.txt");
    Files.write(testFile, "test content".getBytes());
    
    StorageConfig config = mock(StorageConfig.class);
    when(config.getLocation()).thenReturn(testDir.toString());
    StorageServiceImpl storageService = new StorageServiceImpl(config);

    // Act
    storageService.deleteAll();

    // Assert
    assertFalse(Files.exists(testFile), "Test file should not exist");
    assertFalse(Files.exists(testDir), "Test directory should not exist");
}
@Test
public void testLoadAllReturnsEmptyStreamWhenNoFilesExist() throws IOException {
    // Arrange
    Path emptyDir = Files.createTempDirectory("empty-test-dir");
    StorageConfig config = mock(StorageConfig.class);
    when(config.getLocation()).thenReturn(emptyDir.toString());
    StorageServiceImpl storageService = new StorageServiceImpl(config);

    // Act
    Stream<Path> result = storageService.loadAll();

    // Assert
    assertNotNull(result);
    assertEquals(0, result.count());

    // Clean up
    Files.delete(emptyDir);
}
}