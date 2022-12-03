package edu.columbia.e6156.post.controllers;

import edu.columbia.e6156.post.aws.SnsTopic;
import edu.columbia.e6156.post.dao.PostRepository;
import edu.columbia.e6156.post.model.Post;
import edu.columbia.e6156.post.service.ImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.Date;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

@RestController
@CrossOrigin
public final class PostController {
    private final PostRepository repository;
    // TODO - replace with API call to image service, rather than directly using
    private final ImageService imageService;
    private final SnsTopic snsTopic;

    @Autowired
    public PostController(PostRepository repository, ImageService imageService, SnsTopic snsTopic) {
        this.repository = repository;
        this.imageService = imageService;
        this.snsTopic = snsTopic;
        this.snsTopic.setName("PostEventNotification");
    }

    @GetMapping("/health")
    public ResponseEntity<String> getHealth() {
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/v1/post/{postId}")
    public Post getPost(@PathVariable UUID postId) {
        Optional<Post> post = repository.findById(postId);

        return post.orElseThrow(() -> {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, postId + " does not exist");
        });
    }

    @GetMapping("/v1/posts")
    public Page<Post> getPosts(Pageable p) {
        return repository.findAll(p);
    }

    @PostMapping("/v1/post")
    public UUID createPost(@RequestParam Post post, @RequestBody MultipartFile image) throws IOException {
        if(image.isEmpty() && post.getPostId() == null) {
            throw new IllegalArgumentException("no image provided");
        }

        if(!image.isEmpty()) {
            // Create and upload the new image to the service
            UUID imageId = imageService.create(post.getUserId(), image.getBytes());
            post.setImageId(imageId);
        }

        post.setPostId(UUID.randomUUID());
        repository.save(post);

        // Send a 'create post' message to SNS to propagate to downstream feed service
        snsTopic.send(Map.of("userId", post.getUserId(), "postId", post.getPostId(), "eventType", "CREATE"));
        return post.getPostId();
    }

    @PutMapping("/v1/post/{postId}")
    public ResponseEntity updatePost(@PathVariable UUID postId, @RequestBody Post updatedPost) {
        Optional<Post> opt = repository.findById(postId);

        if(opt.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        final Post original = opt.get();

        updateElementIfPresent(updatedPost.getSubject(), original::setSubject);
        original.setUpdated(new Date());

        repository.save(updatedPost);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping("/v1/post/{postId}")
    public ResponseEntity deletePost(@PathVariable UUID postId) {
        try {
            repository.deleteById(postId);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(HttpStatus.OK);
    }

    private void updateElementIfPresent(String update, Consumer<String> updater) {
        if(update == null) {
            return;
        }

        updater.accept(update);
    }
}
