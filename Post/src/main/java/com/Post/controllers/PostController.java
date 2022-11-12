package com.Post.controllers;

import com.Post.dao.PostDao;
import com.Post.model.Post;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Date;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

@RestController
@CrossOrigin
public final class PostController {
    private final PostDao dao;

    @Autowired
    public PostController(final PostDao dao) {
        this.dao = dao;
    }

    @GetMapping("/v1/post/{postId}")
    public Post getPost(@PathVariable UUID postId) {
        Optional<Post> post = dao.findById(postId);

        return post.orElseThrow(() -> {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, postId + " does not exist");
        });
    }

    @GetMapping("/v1/posts")
    public Page<Post> getPosts(Pageable p) {
        return dao.findAll(p);
    }

    @PostMapping("/v1/post")
    public UUID createPost(@RequestBody Post post) {
        dao.save(post);
        return post.getId();
    }

    @PutMapping("/v1/post/{postId}")
    public boolean updatePost(@PathVariable UUID postId, @RequestBody Post updatedPost) {
        Optional<Post> opt = dao.findById(postId);

        if(opt.isEmpty()) {
            return false;
        }

        final Post original = opt.get();

        updateIfPresent(updatedPost.getBody(), original::setBody);
        updateIfPresent(updatedPost.getSubject(), original::setSubject);
        original.setUpdated(new Date());

        dao.save(updatedPost);
        return true;
    }

    private void updateIfPresent(String update, Consumer<String> updater) {
        if(update == null) {
            return;
        }

        updater.accept(update);
    }
}
