package com.devsuperior.dsmovie.services;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.devsuperior.dsmovie.entities.UserEntity;
import com.devsuperior.dsmovie.projections.UserDetailsProjection;
import com.devsuperior.dsmovie.repositories.UserRepository;
import com.devsuperior.dsmovie.tests.UserDetailsFactory;
import com.devsuperior.dsmovie.tests.UserFactory;
import com.devsuperior.dsmovie.utils.CustomUserUtil;

@ExtendWith(SpringExtension.class)
@ContextConfiguration
public class UserServiceTests {

	@InjectMocks
	private UserService service;

	@Mock
	private UserRepository repository;

	@Mock
	private CustomUserUtil userUtil;

	private String validUsername = "maria@gmail.com";
	private String invalidUsername = "joao@gmail.com";
	private UserEntity userEntity;

	@BeforeEach
	void setUp() throws Exception {
		userEntity = UserFactory.createUserEntity();

		Mockito.when(userUtil.getLoggedUsername()).thenReturn(validUsername);

		Mockito.when(repository.findByUsername(validUsername)).thenReturn(Optional.of(userEntity));
		Mockito.when(repository.findByUsername(invalidUsername)).thenReturn(Optional.empty());
	}

	@Test
	public void authenticatedShouldReturnUserEntityWhenUserExists() {
		UserEntity result = service.authenticated();

		Assertions.assertNotNull(result);
		Assertions.assertEquals(validUsername, result.getUsername());
	}

	@Test
	public void authenticatedShouldThrowUsernameNotFoundExceptionWhenUserDoesNotExists() {
		Mockito.when(userUtil.getLoggedUsername()).thenReturn(invalidUsername);
		Assertions.assertThrows(UsernameNotFoundException.class, () -> {
			service.authenticated();
		});
	}

	@Test
	public void loadUserByUsernameShouldReturnUserDetailsWhenUserExists() {
		List<UserDetailsProjection> projections = UserDetailsFactory.createCustomAdminClientUser(validUsername);

		Mockito.when(repository.searchUserAndRolesByUsername(validUsername)).thenReturn(projections);

		UserDetails result = service.loadUserByUsername(validUsername);

		Assertions.assertNotNull(result);
		Assertions.assertEquals(validUsername, result.getUsername());
		Assertions.assertEquals(projections.get(0).getPassword(), result.getPassword());
		Assertions.assertFalse(result.getAuthorities().isEmpty());
	}

	@Test
	public void loadUserByUsernameShouldThrowUsernameNotFoundExceptionWhenUserDoesNotExists() {
		Mockito.when(repository.searchUserAndRolesByUsername(invalidUsername)).thenReturn(List.of());

        Assertions.assertThrows(UsernameNotFoundException.class, () -> {
            service.loadUserByUsername(invalidUsername);
        });
	}
}
