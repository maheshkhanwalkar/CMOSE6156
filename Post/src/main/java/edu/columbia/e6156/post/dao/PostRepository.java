package edu.columbia.e6156.post.dao;

import edu.columbia.e6156.post.model.Post;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Component;

import java.util.UUID;


@Component
public interface PostRepository extends PagingAndSortingRepository<Post, UUID> {
}
