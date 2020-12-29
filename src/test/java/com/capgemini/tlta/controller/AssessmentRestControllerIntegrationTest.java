package com.capgemini.tlta.controller;

import static org.assertj.core.api.Assertions.assertThat;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasSize;
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
import com.capgemini.tlta.repository.AssessmentActivityRepository;
import com.capgemini.tlta.repository.LearningActivityRepository;

/**
 * The Class AssessmentRestControllerIntegrationTest.
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT, classes = TechnologyLearningAndTrackingAppSprint2Application.class)
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(replace = Replace.NONE)
public class AssessmentRestControllerIntegrationTest {
	@Autowired
	private MockMvc mvc;

	@Autowired
	private AssessmentActivityRepository repository;
	
	@Autowired
	private LearningActivityRepository learningRepository;
	
	/**
	 * Reset db.
	 */
	@AfterEach
	public void resetDb() {
		learningRepository.deleteAll();
		repository.deleteAll();
	}

	/**
	 * When valid input then create assessment.
	 *
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws Exception the exception
	 */
	@Test
	public void whenValidInput_thenCreateAssessment() throws IOException, Exception {
		Assessment java = new Assessment("Java","MCQ",new Date(),4d);
		mvc.perform(post("/api/assessments/")
				.contentType(MediaType.APPLICATION_JSON)
				.content(JsonUtil.toJson(java)));

		List<Assessment> found = repository.findAll();
		assertThat(found)
		.extracting(Assessment::getAssessmentName).containsOnly("Java");
	}

	/**
	 * Given assessments when get assessments then status 200.
	 *
	 * @throws Exception the exception
	 */
	@Test
	public void givenAssessments_whenGetAssessments_thenStatus200() throws Exception {
		createTestAssessment("Java","MCQ",new Date(),4d);
		createTestAssessment("Jpa","MCQ",new Date(),4d);

		mvc.perform(get("/api/assessments/").contentType(MediaType.APPLICATION_JSON)).andDo(print())
				.andExpect(status().isOk()).andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(2))))
				.andExpect(jsonPath("$[0].assessmentName", is("Java")))
				.andExpect(jsonPath("$[1].assessmentName", is("Jpa")));

	}
	
	@Test
    public void whenDeleteAssessment_thenNoAssessmentShouldBeFound() throws Exception {
        Integer assessmentId = createTestAssessment("Jpa","MCQ",new Date(),4d);
        
        mvc.perform(delete("/api/assessments/{id}", assessmentId).contentType(MediaType.APPLICATION_JSON))
          .andDo(print())
          .andExpect(status().isOk());
        
        mvc.perform(get("/api/assessments/").contentType(MediaType.APPLICATION_JSON))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$", hasSize(equalTo(0))));
    }
	
	@Test
    public void whenUpdateAssessment_thenAssessmentShouldBeUpdated() throws Exception {
    	Assessment assessment = new Assessment("Java", "MCQ", new Date(), 2.0d);
		assessment = repository.saveAndFlush(assessment);
		
		Assessment updatedAssessment = assessment;
		updatedAssessment.setAssessmentName("Java Test");
		updatedAssessment.setAssessmentTimeDuration(1d);
		
		mvc.perform(put("/api/assessments/")
					.contentType(MediaType.APPLICATION_JSON)
					.content(JsonUtil.toJson(updatedAssessment)))
					.andDo(print())
					.andExpect(status().isOk());
		
		mvc.perform(get("/api/assessments/").contentType(MediaType.APPLICATION_JSON))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$", hasSize(equalTo(1))))
        .andExpect(jsonPath("$[0].assessmentName", is("Java Test")))
        .andExpect(jsonPath("$[0].assessmentTimeDuration", is(1d)));
    }
    
	
	/**
	 * Creates the test assessment.
	 *
	 * @param name the name
	 */
	private Integer createTestAssessment(String name,String type,Date date,Double time) {
		Assessment assessment = new Assessment(name,type,date,time);
		repository.saveAndFlush(assessment);
		return assessment.getId();
	}
}
