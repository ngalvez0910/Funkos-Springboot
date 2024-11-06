package org.example.demofunkos.storage.services;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.stream.Stream;

import lombok.extern.slf4j.Slf4j;
import org.example.demofunkos.storage.controllers.StorageController;
import org.example.demofunkos.storage.exceptions.StorageException;
import org.example.demofunkos.storage.exceptions.StorageNotFound;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

@Service
@Slf4j
public class StorageServiceImpl implements StorageService {

    private final Path rootLocation;

    @Autowired
    public StorageServiceImpl(@Value("${upload.root-location}") String path) {
        this.rootLocation = Paths.get(path);
    }

    @Override
    public void init() {
        log.info("Inicializando almacenamiento");
        try {
            Files.createDirectories(rootLocation);
        } catch (IOException e) {
            throw new StorageException("No se puede inicializar el almacenamiento " + e);
        }
    }

    @Override
    public String store(MultipartFile file) {
        String filename = StringUtils.cleanPath(file.getOriginalFilename());
        String extension = StringUtils.getFilenameExtension(filename);
        String justFilename = filename.replace("." + extension, "");
        String storedFilename = System.currentTimeMillis() + "_" + justFilename + "." + extension;

        try {
            if (file.isEmpty()) {
                throw new StorageNotFound("Fichero vacío " + filename);
            }
            if (filename.contains("..")) {
                throw new StorageNotFound(
                        "No se puede almacenar un fichero con una ruta relativa fuera del directorio actual "
                                + filename);
            }

            try (InputStream inputStream = file.getInputStream()) {
                log.info("Almacenando fichero " + filename + " como " + storedFilename);
                Files.copy(inputStream, this.rootLocation.resolve(storedFilename),
                        StandardCopyOption.REPLACE_EXISTING);
                return storedFilename;
            }

        } catch (IOException e) {
            throw new StorageException("Fallo al almacenar fichero " + filename + " " + e);
        }
    }

    @Override
    public Stream<Path> loadAll() {
        log.info("Cargando todos los ficheros almacenados");
        try {
            return Files.walk(this.rootLocation, 1)
                    .filter(path -> !path.equals(this.rootLocation))
                    .map(this.rootLocation::relativize);
        } catch (IOException e) {
            throw new StorageException("Fallo al leer ficheros almacenados " + e);
        }
    }

    @Override
    public Path load(String filename) {
        log.info("Cargando fichero " + filename);
        return rootLocation.resolve(filename);
    }

    @Override
    public Resource loadAsResource(String filename) {
        log.info("Cargando fichero " + filename);
        try {
            Path file = load(filename);
            Resource resource = new UrlResource(file.toUri());
            if (resource.exists() || resource.isReadable()) {
                return resource;
            } else {
                throw new StorageNotFound("No se puede leer fichero: " + filename);
            }
        } catch (MalformedURLException e) {
            throw new StorageException("Error al cargar fichero " + filename + " " + e);
        }
    }

    @Override
    public void delete(String filename) {
        String justFilename = StringUtils.getFilename(filename);
        try {
            log.info("Eliminando fichero " + filename);
            Path file = load(justFilename);
            Files.deleteIfExists(file);
        } catch (IOException e) {
            throw new StorageException("No se puede eliminar el fichero " + filename + " " + e);
        }
    }

    @Override
    public void deleteAll() {
        log.info("Eliminando todos los ficheros almacenados");

        try {

            Files.walk(this.rootLocation)
                    .filter(path -> !path.equals(this.rootLocation))
                    .forEach(path -> {
                        try {
                            Files.delete(path);
                        } catch (IOException e) {
                            throw new StorageException("No se puede eliminar el fichero: " + path + " " + e);
                        }
                    });
        } catch (IOException e) {
            throw new StorageException("Fallo al eliminar ficheros almacenados " + e);
        }
    }

    @Override
    public String getUrl(String filename) {
        log.info("Obteniendo URL del fichero " + filename);
        return MvcUriComponentsBuilder
                .fromMethodName(StorageController.class, "serveFile", filename, null)
                .build().toUriString();
    }
}