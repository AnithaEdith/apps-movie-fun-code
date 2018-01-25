package org.superbiz.moviefun.blob;

import org.apache.tika.io.IOUtils;

import java.io.*;
import java.nio.file.Path;
import java.util.Optional;
import org.apache.tika.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

//@Component
public class FileStore implements BlobStore {


    @Override
    public void put(Blob blob) throws IOException {
        Logger logger= LoggerFactory.getLogger("FileStore");

        File file = new File(blob.name);
        file.delete();
        file.getParentFile().mkdirs();
        file.createNewFile();

        try (FileOutputStream outputStream = new FileOutputStream(file)) {
            IOUtils.copy(blob.inputStream, outputStream);
            logger.info("file copied" + blob.name);
            logger.info("outputStream.getChannel().size()" + outputStream.getChannel().size());
        }
    }

    @Override
    public Optional<Blob> get(String name) throws IOException {
        File file = new File(name);
        Tika tika = new Tika();

        if (file.exists()) {
            InputStream in = new FileInputStream(file);
            Blob blobcoverfile = new Blob(
                    name,
                    new FileInputStream(file),
                    tika.detect(file)
            );
            return Optional.of(blobcoverfile);
        } else {
            return Optional.empty();
        }
    }
}