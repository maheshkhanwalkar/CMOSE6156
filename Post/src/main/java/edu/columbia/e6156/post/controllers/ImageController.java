package edu.columbia.e6156.post.controllers;

import edu.columbia.e6156.post.config.BeanConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.util.UUID;

@RestController
@CrossOrigin
public class ImageController {
    private final S3Client client;
    private final String imageBucket;

    @Autowired
    public ImageController(final S3Client client, @Qualifier(BeanConstants.IMAGE_S3_BUCKET) final String imageBucket) {
        this.client = client;
        this.imageBucket = imageBucket;
    }

    @PostMapping("/v1/image/upload")
    public UUID uploadImage(MultipartFile imageFile) throws IOException {
        byte[] bytes = imageFile.getBytes();
        UUID imageId = UUID.randomUUID();

        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(imageBucket).key(imageId.toString()).build();
        client.putObject(request, RequestBody.fromBytes(bytes));

        return imageId;
    }

    @GetMapping("/v1/image/{imageId}")
    public @ResponseBody byte[] retrieveImage(@PathVariable UUID imageId) {
        GetObjectRequest request = GetObjectRequest.builder()
                .bucket(imageBucket).key(imageId.toString()).build();
        return client.getObjectAsBytes(request).asByteArray();
    }

    @DeleteMapping("/v1/image/{imageId}")
    public boolean deleteImage(@PathVariable UUID imageId) {
        DeleteObjectRequest request = DeleteObjectRequest.builder()
                .bucket(imageBucket).key(imageId.toString()).build();

        client.deleteObject(request);
        return true;
    }
}
