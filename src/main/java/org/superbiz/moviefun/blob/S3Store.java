package org.superbiz.moviefun.blob;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.util.IOUtils;
import org.apache.tika.Tika;

import org.springframework.stereotype.Component;

import java.io.*;
import java.util.Optional;

//@Component
public class S3Store implements BlobStore {
    private final AmazonS3 s3;
    private final String bucketName;

    Tika tika = new Tika();

    public S3Store( AmazonS3 s3, String bucketName) {
        this.s3=s3;
        this.bucketName=bucketName;
    }

    @Override
    public void put(Blob blob) throws IOException {
        s3.putObject(bucketName, blob.name, blob.inputStream, new ObjectMetadata());
    }

    @Override
    public Optional<Blob> get(String name) throws IOException {
        S3Object s3file= s3.getObject(bucketName,name);
        if (s3file!=null) {
            S3ObjectInputStream content = s3file.getObjectContent();
            byte[] bytes = IOUtils.toByteArray(content);

            Blob blobcoverfile = new Blob(
                    name,
                    new ByteArrayInputStream(bytes),
                    tika.detect(bytes)
            );
            return Optional.of(blobcoverfile);
        } else {
            return Optional.empty();
        }
    }
}
