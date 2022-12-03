package edu.columbia.e6156.post.service;

import edu.columbia.e6156.post.dao.ImageRepository;
import edu.columbia.e6156.post.model.Image;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.util.Date;
import java.util.UUID;

@Service
@AllArgsConstructor
public final class ImageService {
    private final ImageRepository repository;
    private final S3Client client;
    private final String imageS3Bucket;

    public UUID create(UUID userId, byte[] bytes) {
        UUID imageId = UUID.randomUUID();

        createRecord(userId, imageId);
        uploadObject(imageId.toString(), bytes);

        return imageId;
    }

    public byte[] getImage(UUID imageId) {
        return getObject(imageId.toString());
    }

    public void delete(UUID imageId) {
        deleteRecord(imageId);
        deleteObject(imageId.toString());
    }

    private void uploadObject(String name, byte[] bytes) {
        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(imageS3Bucket).key(name).build();
        client.putObject(request, RequestBody.fromBytes(bytes));
    }

    private byte[] getObject(String name) {
        GetObjectRequest request = GetObjectRequest.builder()
                .bucket(imageS3Bucket).key(name).build();
        return client.getObjectAsBytes(request).asByteArray();
    }

    private void deleteObject(String name) {
        DeleteObjectRequest request = DeleteObjectRequest.builder()
                .bucket(imageS3Bucket).key(name).build();

        client.deleteObject(request);
    }

    private void createRecord(UUID userId, UUID imageId) {
        repository.save(new Image(userId, imageId, new Date()));
    }

    private void deleteRecord(UUID imageId) {
        repository.deleteById(imageId);
    }
}
