package com.hoaxify;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import com.hoaxify.shared.GenericResponse;
import com.hoaxify.user.User;
import com.hoaxify.user.UserRepository;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class UserControllerTest {
	
	@Autowired
	TestRestTemplate testRestTemplate; 
	
	@Autowired
	UserRepository userRepository;
	
	@Before
	public void cleanUp() {
		userRepository.deleteAll();
	}
	
	@Test
	public void postUser_whenUserIsValid_receiveOk() {
		User user = createUser();
		ResponseEntity<Object> response = testRestTemplate.postForEntity("/api/1.0/users", user, Object.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
	}
	
	@Test
	public void postUser_whenUserIsValid_saveItToDatabase() {
		User user = createUser();
		testRestTemplate.postForEntity("/api/1.0/users", user, Object.class);
		assertThat(userRepository.count()).isEqualTo(1);
	}
	
	@Test
	public void postUser_whenUserIsValid_receiveSuccessMessage() {
		User user = createUser();
		ResponseEntity<GenericResponse> response = testRestTemplate.postForEntity("/api/1.0/users", user, GenericResponse.class);
		assertThat(response.getBody().getMessage()).isNotNull();
	}
	
	@Test
	public void postUser_whenUserIsValid_passwordIsHashedInDatabase() {
		User user = createUser();
		testRestTemplate.postForEntity("/api/1.0/users", user, Object.class);
		List<User> users = userRepository.findAll();
		User inDb = users.get(0);
		assertThat(inDb.getPassword()).isNotEqualTo(user.getPassword());
	}

	private User createUser() {
		User user = new User();
		user.setUserName("test-user");
		user.setDisplayName("display-name");
		user.setPassword("B4password");
		return user;
	}
}
