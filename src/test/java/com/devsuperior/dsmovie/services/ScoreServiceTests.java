package com.devsuperior.dsmovie.services;

import static org.mockito.ArgumentMatchers.any;

import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.devsuperior.dsmovie.dto.MovieDTO;
import com.devsuperior.dsmovie.dto.ScoreDTO;
import com.devsuperior.dsmovie.entities.MovieEntity;
import com.devsuperior.dsmovie.entities.ScoreEntity;
import com.devsuperior.dsmovie.entities.UserEntity;
import com.devsuperior.dsmovie.repositories.MovieRepository;
import com.devsuperior.dsmovie.repositories.ScoreRepository;
import com.devsuperior.dsmovie.services.exceptions.ResourceNotFoundException;
import com.devsuperior.dsmovie.tests.MovieFactory;
import com.devsuperior.dsmovie.tests.ScoreFactory;
import com.devsuperior.dsmovie.tests.UserFactory;

@ExtendWith(SpringExtension.class)
public class ScoreServiceTests {

	@InjectMocks
	private ScoreService service;

	@Mock
	private UserService userService;

	@Mock
	private MovieRepository movieRepository;

	@Mock
	private ScoreRepository scoreRepository;
	
	private Long existingMovieId;
	private Long nonExistingMovieId;
	private ScoreDTO scoreDTO;
	private UserEntity userEntity;
	private MovieEntity movieEntity;
	private ScoreEntity scoreEntity;
	
	@BeforeEach
    void setUp() throws Exception {
        existingMovieId = 1L;
        nonExistingMovieId = 100L;

        // Criando os mocks necessários
        scoreDTO = ScoreFactory.createScoreDTO();

        userEntity = UserFactory.createUserEntity();

        movieEntity = MovieFactory.createMovieEntity();

        scoreEntity = ScoreFactory.createScoreEntity();

        // Configurando os mocks
        Mockito.when(userService.authenticated()).thenReturn(userEntity);

        Mockito.when(movieRepository.findById(existingMovieId)).thenReturn(Optional.of(movieEntity));
        Mockito.when(movieRepository.findById(nonExistingMovieId)).thenReturn(Optional.empty());

        Mockito.when(scoreRepository.saveAndFlush(any(ScoreEntity.class))).thenReturn(scoreEntity);
        Mockito.when(movieRepository.save(any(MovieEntity.class))).thenReturn(movieEntity);
    }

	@Test
	public void saveScoreShouldReturnMovieDTO() {
		MovieDTO result = service.saveScore(scoreDTO);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(existingMovieId, result.getId());
        Assertions.assertEquals("Test Movie", result.getTitle());
        
	}

	@Test
	public void saveScoreShouldThrowResourceNotFoundExceptionWhenNonExistingMovieId() {
		scoreDTO = new ScoreDTO(nonExistingMovieId, 4.5);

		 Assertions.assertThrows(ResourceNotFoundException.class, () -> {
	            service.saveScore(scoreDTO);
	        });
	}
}
