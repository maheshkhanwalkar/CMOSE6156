package com.UserProfile.controllers;

import com.UserProfile.dao.UserProfileDao;
import com.UserProfile.model.UserProfile;
import org.apache.catalina.User;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/userprofile")
public class HelloController {

	private final UserProfileDao userProfileDao;

	public HelloController(UserProfileDao userProfileDao) {
		this.userProfileDao = userProfileDao;
	}

	@PostMapping()
	public UserProfile addUserProfile(@RequestParam Map<String, String> params) {
		UserProfile newUser = new UserProfile(params.get("userName"), params.get("email"));
		UserProfile userProfile = userProfileDao.save(newUser);
		return userProfile;
	}

	@GetMapping("/findByEmail/{email}")
	public Optional<UserProfile> getUserByEmail(@PathVariable String email) {
		return userProfileDao.findByEmail(email);
	}

	@GetMapping("/findById/{id}")
	public Optional<UserProfile> getUserById(@PathVariable UUID id) {
		return userProfileDao.findById(id);
	}

	@GetMapping("/findByUserId/{userId}")
	public Optional<UserProfile> getUserByUserId(@PathVariable UUID userId) {
		return userProfileDao.findByUserId(userId);
	}

	@DeleteMapping("/deleteById/{id}")
	public ResponseEntity<Map<String, Boolean>> deleteById(@PathVariable UUID id) {
		Map<String, Boolean> response = new HashMap<>();
		boolean is_deleted = (userProfileDao.deleteUserProfileById(id) == 1);
		response.put("deleted", is_deleted);
		return ResponseEntity.ok(response);
	}

	@PutMapping("/updateById/{id}")
	public ResponseEntity<UserProfile> updateUserProfile(@PathVariable UUID id,
														 @RequestBody UserProfile newUserProfile) {
		UserProfile userProfile = userProfileDao.findById(id).get();
		userProfile.setEmail(newUserProfile.getEmail());
		userProfile.setUserName(newUserProfile.getUserName());
		UserProfile response = userProfileDao.save(userProfile);
		return ResponseEntity.ok(response);
	}
}
