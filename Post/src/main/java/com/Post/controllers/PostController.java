package com.Post.controllers;

import com.Post.dao.PostDao;
import com.Post.model.Post;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;
import java.util.UUID;

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
}
