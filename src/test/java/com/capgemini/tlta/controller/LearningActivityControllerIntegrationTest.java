package com.capgemini.tlta.controller;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.internal.verification.VerificationModeFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import com.capgemini.Technologylearningandtrackingappsprint2.TechnologyLearningAndTrackingAppSprint2Application;
import com.capgemini.tlta.model.LearningActivity;
import com.capgemini.tlta.sevice.AssessmentActivityService;
import com.capgemini.tlta.sevice.LearningActivityDO;
import com.capgemini.tlta.sevice.LearningActivityService;

/**
 * The Class LearningActivityControllerIntegrationTest.
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT, classes = TechnologyLearningAndTrackingAppSprint2Application.class)
@AutoConfigureMockMvc
public class LearningActivityControllerIntegrationTest {
	@Autowired
	private MockMvc mvc;

	@MockBean
	private LearningActivityService service;

	@MockBean
	private AssessmentActivityService assessmentService;
	
	@MockBean
	private LearningActivityDO learningActivityDo; 
	
	/**
	 * Sets the up.
	 *
	 * @throws Exception the exception
	 */
	@BeforeEach
    public void setUp() throws Exception {
    }
	
	/**
	 * When post learning activity then create learning activity.
	 *
	 * @throws Exception the exception
	 */
	@Test
	public void whenPostLearningActivity_thenCreateLearningActivity() throws Exception {
		LearningActivityDO java = new LearningActivityDO("Java","http://java.com","intermediate",3d,new Date());
		
		LearningActivity java1 = new LearningActivity(java);
	
		given(service.addLearningActivityWithAssessment(Mockito.any())).willReturn(java1);

		mvc.perform(post("/api/learningActivity/")
				.contentType(MediaType.APPLICATION_JSON)
				.content(JsonUtil.toJson(java1)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.activityName", is("Java")));

		verify(service, VerificationModeFactory.times(1))
		.addLearningActivityWithAssessment(Mockito.any());
		reset(service);
	}
	
	/**
	 * Given learning activities when get learning activities then return json array.
	 *
	 * @throws Exception the exception
	 */
	@Test
	public void givenLearningActivities_whenGetLearningActivities_thenReturnJsonArray() throws Exception {
		LearningActivityDO java = new LearningActivityDO("Java","http://java.com","intermediate",3d,new Date());
		LearningActivityDO jpa = new LearningActivityDO("Jpa","http://java.com","intermediate",3d,new Date());
		LearningActivityDO cpp = new LearningActivityDO("Cpp","http://java.com","intermediate",3d,new Date());
		
		LearningActivity java1 = new LearningActivity(java);
		LearningActivity jpa1 = new LearningActivity(jpa);
		LearningActivity cpp1 = new LearningActivity(cpp);

		List<LearningActivity> allLearningActivities = Arrays.asList(java1, jpa1, cpp1);

		given(service.getAllLearningActivity()).willReturn(allLearningActivities);

		mvc.perform(get("/api/learningActivity/")
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", hasSize(3)))
				.andExpect(jsonPath("$[0].activityName", is(java.getActivityName())))
				.andExpect(jsonPath("$[1].activityName", is(jpa.getActivityName())))
				.andExpect(jsonPath("$[2].activityName", is(cpp.getActivityName())));
		verify(service, VerificationModeFactory.times(1)).getAllLearningActivity();
		reset(service);
	}

}
