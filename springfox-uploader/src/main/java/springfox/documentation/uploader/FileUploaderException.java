package springfox.documentation.uploader;

import java.io.IOException;

public class FileUploaderException extends IOException {

    public FileUploaderException(final String message) {
        super(message);
    }

    public FileUploaderException(final Throwable cause) {
        super(cause);
    }

    public FileUploaderException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
