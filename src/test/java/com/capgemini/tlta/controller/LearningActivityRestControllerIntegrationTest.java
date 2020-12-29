package com.capgemini.tlta.controller;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
import com.capgemini.tlta.repository.AssessmentActivityRepository;
import com.capgemini.tlta.repository.LearningActivityRepository;
import com.capgemini.tlta.sevice.LearningActivityDO;

/**
 * The Class LearningActivityRestControllerIntegrationTest.
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT, classes = TechnologyLearningAndTrackingAppSprint2Application.class)
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(replace = Replace.NONE)
public class LearningActivityRestControllerIntegrationTest {
	@Autowired
	private MockMvc mvc;

	@Autowired
	private AssessmentActivityRepository assessmentRepository;

	@Autowired
	private LearningActivityRepository learningRepository;
	
	/**
	 * Reset db.
	 */
	@AfterEach
	public void resetDb() {
		learningRepository.deleteAll();
		assessmentRepository.deleteAll();
	}
	
	@Test
	public void whenValidInput_thenCreateLearningActivity() throws IOException, Exception {
		Assessment assessment = new Assessment("Java", "MCQ", new Date(), 2.0d);
		assessment = assessmentRepository.save(assessment);
		
		LearningActivityDO learningActivityDO = new LearningActivityDO("Java Basics", "http://www.java.com", 
				"Beginner", 2.0d, new Date(), assessment.getId());
			
		mvc.perform(post("/api/learningActivity/")
				.contentType(MediaType.APPLICATION_JSON)
				.content(JsonUtil.toJson(learningActivityDO)))
				.andDo(print())
				.andExpect(status().isOk());
		
		List<LearningActivity> found = learningRepository.findAll();
		assertNotNull(found.get(0));
	}
	
	/**
	 * Given learning activities when get learning activities then status 200.
	 *
	 * @throws Exception the exception
	 */
	@Test
	public void givenLearningActivities_whenGetLearningActivities_thenStatus200() throws Exception {
		LearningActivityDO java = new LearningActivityDO("Java","http://java.com","intermediate",3d,new Date());
		LearningActivityDO jpa = new LearningActivityDO("Jpa","http://java.com","intermediate",3d,new Date());
		
		createTestLearningActivity(java);
		createTestLearningActivity(jpa);

		mvc.perform(get("/api/learningActivity/").contentType(MediaType.APPLICATION_JSON))
				.andDo(print())
				.andExpect(status().isOk()).andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(2))))
				.andExpect(jsonPath("$[0].activityName", is("Java")))
				.andExpect(jsonPath("$[1].activityName", is("Jpa")));

	}
	
	
    @Test
    public void whenDeleteLearningActivity_thenNoLearningActivityShouldBeFound() throws Exception {
    	Assessment assessment = new Assessment("Java", "MCQ", new Date(), 2.0d);
		assessment = assessmentRepository.save(assessment);
		
		LearningActivityDO learningActivityDO = new LearningActivityDO("Java Basics", "http://www.java.com", 
				"Beginner", 2.0d, new Date(), assessment.getId());
		
		Integer idToBeDeleted =  createTestLearningActivity(learningActivityDO);

		mvc.perform(delete("/api/learningActivity/{id}", idToBeDeleted))
					.andExpect(status().isOk())
					.andDo(print());
		
		mvc.perform(get("/api/learningActivity/").contentType(MediaType.APPLICATION_JSON))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$", hasSize(equalTo(0))));
    }
    
    
    @Test
    public void whenUpdateLearningActivity_thenLearningActivityShouldBeUpdated() throws Exception {
    	Assessment assessment = new Assessment("Java", "MCQ", new Date(), 2.0d);
		assessment = assessmentRepository.save(assessment);
		
		LearningActivityDO learningActivityDO = new LearningActivityDO("Java Basics", "http://www.java.com", 
				"Beginner", 2.0d, new Date(), assessment.getId());
		LearningActivity learningActivity = new LearningActivity(learningActivityDO);
		learningActivity = learningRepository.save(learningActivity);
		
		LearningActivity updatedLearningActivity = learningActivity;
		updatedLearningActivity.setActivityName("Java Programming Basics");
		updatedLearningActivity.setActivityLevel("Intermmediate");
		
		mvc.perform(put("/api/learningActivity/")
					.contentType(MediaType.APPLICATION_JSON)
					.content(JsonUtil.toJson(updatedLearningActivity)))
					.andDo(print())
					.andExpect(status().isOk());
		
		mvc.perform(get("/api/learningActivity/").contentType(MediaType.APPLICATION_JSON))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$", hasSize(equalTo(1))))
        .andExpect(jsonPath("$[0].activityName", is("Java Programming Basics")))
        .andExpect(jsonPath("$[0].activityLevel", is("Intermmediate")));
    }
    
    
	/**
	 * Creates the test learning activity.
	 *
	 * @param name the name
	 */
	private Integer createTestLearningActivity(LearningActivityDO activityDo) {
		LearningActivity learningActivity = new LearningActivity(activityDo);
		learningActivity = learningRepository.saveAndFlush(learningActivity);
		return learningActivity.getId();
	}
}
