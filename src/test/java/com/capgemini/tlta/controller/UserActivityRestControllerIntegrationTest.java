package com.capgemini.tlta.controller;

import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import com.capgemini.Technologylearningandtrackingappsprint2.TechnologyLearningAndTrackingAppSprint2Application;
import com.capgemini.tlta.model.Assessment;
import com.capgemini.tlta.model.LearningActivity;
import com.capgemini.tlta.model.RegisterUser;
import com.capgemini.tlta.model.Role;
import com.capgemini.tlta.model.UserActivity;
import com.capgemini.tlta.repository.AssessmentActivityRepository;
import com.capgemini.tlta.repository.LearningActivityRepository;
import com.capgemini.tlta.repository.RegisterUserRepository;
import com.capgemini.tlta.repository.UserActivityRepository;
import com.capgemini.tlta.sevice.LearningActivityDO;
import com.capgemini.tlta.sevice.UserActivityDO;
/**
 * The Class UserActivityRestControllerIntegrationTest.
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT, classes = TechnologyLearningAndTrackingAppSprint2Application.class)
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(replace = Replace.NONE)
public class UserActivityRestControllerIntegrationTest {
	
	@Autowired
	private MockMvc mvc;

	@Autowired
	private RegisterUserRepository userRepository;
	
	@Autowired
	private LearningActivityRepository learningRepository;
	
	@Autowired
	private UserActivityRepository userActivityRepository;
	
	@Autowired
	private AssessmentActivityRepository assessmentRepository;
	
	/**
	 * Reset db.
	 */
	@AfterEach
	public void resetDb() {
		learningRepository.deleteAll();
		userRepository.deleteAll();
		userActivityRepository.deleteAll();
	}
	
	/**
	 * When valid input then create user activity.
	 *
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws Exception the exception
	 */
	@Test
	public void whenValidInput_thenCreateUserActivity() throws IOException, Exception {
		Assessment assessment = new Assessment("Java", "MCQ", new Date(), 2.0d);
		assessment = assessmentRepository.save(assessment);
		
		LearningActivityDO learningActivityDO = new LearningActivityDO("Java Basics", "http://www.java.com", 
				"Beginner", 2.0d, new Date(), assessment.getId());
		LearningActivity learningActivity = new LearningActivity(learningActivityDO);
		learningActivity = learningRepository.save(learningActivity);
		
		RegisterUser user = new RegisterUser("Bob", "Moore", "bob@gmail.com", "bob@123", Role.USER);
		user = userRepository.save(user);
		
		UserActivityDO activityDo = new UserActivityDO(user.getId(), learningActivity.getId(),
				null, null);
		
		mvc.perform(post("/api/userActivity/")
				.contentType(MediaType.APPLICATION_JSON)
				.content(JsonUtil.toJson(activityDo)))
				.andDo(print())
				.andExpect(status().isOk());
		
		List<UserActivity> found = userActivityRepository.findAll();
		assertNotNull(found.get(0));
	}
	
    /**
     * Given user activity when get user activities then status 200.
     *
     * @throws Exception the exception
     */
    @Test
    public void givenUserActivity_whenGetUserActivities_thenStatus200() throws Exception {
    	
		Assessment assessment1 = new Assessment("Java", "MCQ", new Date(), 2.0d);
		LearningActivityDO learningActivityDO1 = new LearningActivityDO("Java Basics", "http://www.java.com", 
				"Beginner", 2.0d, new Date(), assessment1.getId());
		RegisterUser user1 = new RegisterUser("Bob", "Moore", "bob@gmail.com", "bob@123", Role.USER);
		setup(assessment1, learningActivityDO1, user1);
		
		
		Assessment assessment2 = new Assessment("React", "Theory", new Date(), 1.0d);
		LearningActivityDO learningActivityDO2 = new LearningActivityDO("React Basics", "http://www.react.com", 
				"Beginner", 8.0d, new Date(), assessment2.getId());
		RegisterUser user2 = new RegisterUser("Alex", "Moore", "alex@gmail.com", "alex@123", Role.USER);
		setup(assessment2, learningActivityDO2, user2);
		
        mvc.perform(get("/api/userActivity/").contentType(MediaType.APPLICATION_JSON))
          .andDo(print())
          .andExpect(status().isOk())
          .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
          .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(2))));
    }
    
    
    /**
     * When delete activity then no activity should be found.
     *
     * @throws Exception the exception
     */
    @Test
    public void whenDeleteActivity_thenNoActivityShouldBeFound() throws Exception {
    	
    	Assessment assessment = new Assessment("Java", "MCQ", new Date(), 2.0d);
		assessment = assessmentRepository.save(assessment);
		
		LearningActivityDO learningActivityDO = new LearningActivityDO("Java Basics", "http://www.java.com", 
				"Beginner", 2.0d, new Date(), assessment.getId());
		LearningActivity learningActivity = new LearningActivity(learningActivityDO);
		learningActivity = learningRepository.save(learningActivity);
		
		RegisterUser user = new RegisterUser("Bob", "Moore", "bob@gmail.com", "bob@123", Role.USER);
		user = userRepository.save(user);
		
		UserActivity userActivity = new UserActivity(user, learningActivity, "register", null, null);
		userActivityRepository.save(userActivity);
		userActivityRepository.flush();
		
		mvc.perform(delete("/api/userActivity/{id}", userActivity.getUserActivityId()))
					.andExpect(status().isOk())
					.andDo(print());
		
		mvc.perform(get("/api/userActivity/").contentType(MediaType.APPLICATION_JSON))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$", hasSize(equalTo(0))));
    }
    
    /**
     * When update activity then activity should be updated.
     *
     * @throws Exception the exception
     */
    @Test
    public void whenUpdateActivity_thenActivityShouldBeUpdated() throws Exception {
    	
    	Assessment assessment = new Assessment("Java", "MCQ", new Date(), 2.0d);
		assessment = assessmentRepository.save(assessment);
		
		LearningActivityDO learningActivityDO = new LearningActivityDO("Java Basics", "http://www.java.com", 
				"Beginner", 2.0d, new Date(), assessment.getId());
		LearningActivity learningActivity = new LearningActivity(learningActivityDO);
		learningActivity = learningRepository.save(learningActivity);
		
		RegisterUser user = new RegisterUser("Bob", "Moore", "bob@gmail.com", "bob@123", Role.USER);
		user = userRepository.save(user);
		
		UserActivity userActivity = new UserActivity(user, learningActivity, "register", null, null);
		userActivityRepository.save(userActivity);
		userActivityRepository.flush();
		
		String updatedStatus = "Pending";
		
		mvc.perform(put("/api/userActivity/updateStatus/{id}/{status}", userActivity.getUserActivityId(), updatedStatus))
					.andDo(print())
					.andExpect(status().isOk());
		
		mvc.perform(get("/api/userActivity/").contentType(MediaType.APPLICATION_JSON))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$", hasSize(equalTo(1))))
        .andExpect(jsonPath("$[0].status", is(updatedStatus)));
    }
    
    /**
     * Setup.
     *
     * @param assessment the assessment
     * @param learningActivityDO the learning activity DO
     * @param user the user
     */
    private void setup(Assessment assessment, LearningActivityDO learningActivityDO, RegisterUser user) {
    	assessment = assessmentRepository.save(assessment);
    	
    	LearningActivity learningActivity = new LearningActivity(learningActivityDO);
		learningActivity = learningRepository.save(learningActivity);
		
		user = userRepository.save(user);
		
		UserActivity userActivity = new UserActivity(user, learningActivity, "register", null, null);	
		userActivityRepository.saveAndFlush(userActivity);
    }
}