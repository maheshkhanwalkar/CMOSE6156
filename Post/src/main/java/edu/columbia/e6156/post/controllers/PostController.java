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

    private static final String SNS_TOPIC_NAME = "PostEventNotification";

    @Autowired
    public PostController(PostRepository repository, ImageService imageService, SnsTopic snsTopic) {
        this.repository = repository;
        this.imageService = imageService;
        this.snsTopic = snsTopic;
        this.snsTopic.setName(SNS_TOPIC_NAME);
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
    public UUID createPost(@RequestParam UUID userId, @RequestParam String subject,
                           @RequestBody MultipartFile image) throws IOException {
        if(userId == null || subject == null) {
            throw new IllegalArgumentException("no user id or subject provided");
        }

        if(image == null || image.isEmpty()) {
            throw new IllegalArgumentException("no image provided");
        }

        // Create and upload the new image to the service
        UUID imageId = imageService.create(userId, image.getBytes());

        Post post = createPostObject(userId, imageId, subject);
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

    private Post createPostObject(UUID userId, UUID imageId, String subject) {
        Date creation = new Date();
        UUID postId = UUID.randomUUID();

        return new Post(postId, userId, imageId, subject, creation, creation);
    }

    private void updateElementIfPresent(String update, Consumer<String> updater) {
        if(update == null) {
            return;
        }

        updater.accept(update);
    }
}
