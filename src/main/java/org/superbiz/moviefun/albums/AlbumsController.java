package org.superbiz.moviefun.albums;

import org.apache.tika.Tika;
import org.apache.tika.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.superbiz.moviefun.blob.Blob;
import org.superbiz.moviefun.blob.BlobStore;
import org.superbiz.moviefun.blob.FileStore;
import org.superbiz.moviefun.blob.S3Store;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Optional;

import static java.lang.ClassLoader.getSystemResource;
import static java.lang.String.format;
import static java.nio.file.Files.readAllBytes;

@Controller
@RequestMapping("/albums")
public class AlbumsController {

    private final AlbumsBean albumsBean;
    Logger logger=LoggerFactory.getLogger("AlbumsController");

  /*  @Autowired
    FileStore fileStore;
*/
    @Autowired
    BlobStore s3Store;

    public AlbumsController(AlbumsBean albumsBean) {
        this.albumsBean = albumsBean;
      //  this.s3Store=s3Store;
    }


    @GetMapping
    public String index(Map<String, Object> model) {
        model.put("albums", albumsBean.getAlbums());
        return "albums";
    }

    @GetMapping("/{albumId}")
    public String details(@PathVariable long albumId, Map<String, Object> model) {
        model.put("album", albumsBean.find(albumId));
        return "albumDetails";
    }

    @PostMapping("/{albumId}/cover")
    public String uploadCover(@PathVariable long albumId, @RequestParam("file") MultipartFile uploadedFile) throws IOException {
        Blob blob=new Blob(getCoverBlobName(albumId), uploadedFile.getInputStream(), uploadedFile.getContentType());
        logger.info("uploadedFile.getInputStream()" + uploadedFile.getInputStream().available());
        s3Store.put(blob);
       // fileStore.put(blob);
        //saveUploadToFile(uploadedFile, getCoverFile(albumId));

        return format("redirect:/albums/%d", albumId);
    }

    @GetMapping("/{albumId}/cover")
    public HttpEntity<byte[]> getCover(@PathVariable long albumId) throws IOException, URISyntaxException {
      /*  Path coverFilePath = getExistingCoverPath(albumId);


        byte[] imageBytes = readAllBytes(coverFilePath);
        HttpHeaders headers = createImageHttpHeaders(coverFilePath, imageBytes);

        return new HttpEntity<>(imageBytes, headers);*/

        String coverBlobName = getCoverBlobName(albumId);
     //   Optional<Blob> coverfile= fileStore.get(coverBlobName);
        Optional<Blob> coverfile= s3Store.get(coverBlobName);
        Blob blob=coverfile.get();
        logger.info("blob exists" + blob.name);
        logger.info("blob.inputStream.available()" + blob.inputStream.available());

        byte[] bytes = IOUtils.toByteArray(blob.inputStream);
        HttpHeaders headers =new HttpHeaders();

        headers.setContentType(MediaType.valueOf(blob.contentType));
        headers.setContentLength(bytes.length);

        logger.info("MediaType.valueOf(blob.contentType) " + MediaType.valueOf(blob.contentType));

        return new HttpEntity<>(bytes, headers);

    }


    private void saveUploadToFile(@RequestParam("file") MultipartFile uploadedFile, File targetFile) throws IOException {
        targetFile.delete();
        targetFile.getParentFile().mkdirs();
        targetFile.createNewFile();

        try (FileOutputStream outputStream = new FileOutputStream(targetFile)) {
            outputStream.write(uploadedFile.getBytes());
        }
    }

   /* private HttpHeaders createImageHttpHeaders(Path coverFilePath, byte[] imageBytes) throws IOException {
        String contentType = new Tika().detect(coverFilePath);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(contentType));
        headers.setContentLength(imageBytes.length);
        return headers;
    }*/

    private File getCoverFile(@PathVariable long albumId) {
        String coverFileName = format("covers/%d", albumId);
        return new File(coverFileName);
    }

    private String getCoverBlobName(@PathVariable long albumId) {
        String coverFileName = format("covers/%d", albumId);
        return  coverFileName;
    }



}
