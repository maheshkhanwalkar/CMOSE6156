package edu.columbia.e6156.post.controllers;

import edu.columbia.e6156.post.service.ImageService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@RestController
@CrossOrigin
@AllArgsConstructor
public class ImageController {
    private final ImageService service;

    @PostMapping("/v1/image/upload")
    public UUID uploadImage(@RequestParam UUID userId, MultipartFile imageFile) throws IOException {
        byte[] bytes = imageFile.getBytes();
        return service.create(userId, bytes);
    }

    @GetMapping(value = "/v1/image/{imageId}", produces = "image/*")
    public @ResponseBody byte[] retrieveImage(@PathVariable UUID imageId) {
        return service.getImage(imageId);
    }

    @DeleteMapping("/v1/image/{imageId}")
    public void deleteImage(@PathVariable UUID imageId) {
        service.delete(imageId);
    }
}
