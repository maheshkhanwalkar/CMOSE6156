package com.UserProfile.dao;

import com.UserProfile.model.UserProfile;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.UUID;


@Component
@Transactional
public interface UserProfileDao extends PagingAndSortingRepository<UserProfile, UUID> {
    Optional<UserProfile> findByUserId(UUID userId);

    Optional<UserProfile> findById(UUID id);

    List<UserProfile> findAllByIdIn(Iterable<UUID> ids);

    UserProfile save(UserProfile userProfile);

    Optional<UserProfile> findByEmail(String email);

    Integer deleteUserProfileById(UUID id);

}