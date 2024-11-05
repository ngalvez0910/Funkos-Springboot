package org.example.demofunkos.storage.exceptions;

import java.io.Serial;

public class StorageException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 43876691117560211L;

    public StorageException(String mensaje) {
        super(mensaje);
    }
}
