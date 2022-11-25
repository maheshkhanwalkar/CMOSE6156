package edu.columbia.e6156.post.dao;

import edu.columbia.e6156.post.model.Image;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface ImageRepository extends CrudRepository<Image, UUID> {

    @Query("SELECT userId, imageId, created FROM Image WHERE userId = :userId")
    List<Image> getImagesByUserId(@Param("userId") UUID userId);
}
