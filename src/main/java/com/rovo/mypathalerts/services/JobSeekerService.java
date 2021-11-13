package com.rovo.mypathalerts.services;

import com.rovo.mypathalerts.exceptions.*;
import com.rovo.mypathalerts.models.*;
import com.rovo.mypathalerts.repositories.CompanyRepository;
import com.rovo.mypathalerts.repositories.JobSeekerRepository;
import com.rovo.mypathalerts.utils.VerificationCodeGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

@Transactional
@Service
public class JobSeekerService {

	@Autowired
	JobSeekerRepository jobSeekerRepository;

	@Autowired
	CompanyRepository companyRepository;

	@Autowired
	WorkExperienceService workExperienceService;

	@Autowired
	EmailService emailService;

	@Autowired
	SkillService skillService;

	@Autowired
	EducationService educationService;

	@Autowired
	JobPostingService jobPostingService;

	/**
	 * Signup the job seeker
	 * 
	 * @param parameters
	 * @return
	 */
	public JobSeeker createJobSeeker(HashMap<String, String> parameters) throws JobSeekerExceptions {

		Company company = companyRepository.findByEmail(parameters.get("email"));
		if (company != null) {
			throw new JobSeekerExceptions("Email ID already registered by company");
		}

		JobSeeker jobSeeker = new JobSeeker();
		jobSeeker.setEmail(parameters.get("email"));
		jobSeeker.setFirstname(parameters.get("firstname"));
		jobSeeker.setLastname(parameters.get("lastname"));
		jobSeeker.setPassword(parameters.get("password"));
		jobSeeker.setIsVerified(false);

		VerificationCodeGenerator verificationCodeGenerator = new VerificationCodeGenerator();
		String verificationCode = verificationCodeGenerator.getVerificationCode();

		jobSeeker.setVerificationCode(verificationCode);
		jobSeekerRepository.save(jobSeeker);
		return jobSeeker;
	}

	/**
	 * Verify the job seeker from the verification code send in email
	 * 
	 * @param id
	 * @param verificationCode
	 * @return
	 */
	public void verifyJobSeeker(String id, String verificationCode) throws JobSeekerExceptions {

		JobSeeker jobSeeker = jobSeekerRepository.findByJobseekeridAndVerificationCode(id, verificationCode);
		if (jobSeeker != null) {
			if (jobSeeker.getIsVerified()) {
				throw new JobSeekerExceptions("Already Verified! Please login to continue");
			}
			jobSeeker.setIsVerified(true);
			jobSeekerRepository.save(jobSeeker);
		} else {
			throw new JobSeekerExceptions("Invalid verification Code");
		}
	}

	/**
	 * Update the profile of jobSeeker
	 * 
	 * @param parameters
	 * @return UpdatedJobseeker
	 * @throws JobSeekerExceptions
	 * @throws WorkExperienceExceptions
	 * @throws SkillExceptions
	 * @throws EducationExceptions
	 */
	@SuppressWarnings({ "unchecked" })
	public JobSeeker updateProfile(Map<String, Object> parameters)
			throws JobSeekerExceptions, WorkExperienceExceptions, SkillExceptions, EducationExceptions {
		JobSeeker jobSeeker = jobSeekerRepository.findByJobseekerid((String) parameters.get("id"));

		if (jobSeeker == null) {
			throw new JobSeekerExceptions("No profile found");
		}

		if (parameters.containsKey("firstname")) {
			jobSeeker.setFirstname((String) parameters.get("firstname"));
		}
		if (parameters.containsKey("lastname")) {
			jobSeeker.setLastname((String) parameters.get("lastname"));
		}
		if (parameters.containsKey("password")) {
			jobSeeker.setPassword((String) parameters.get("password"));
		}
		if (parameters.containsKey("selfIntroduction")) {
			jobSeeker.setSelfIntroduction((String) parameters.get("selfIntroduction"));
		}
		if (parameters.containsKey("picture")) {
			URL pictureUrl;
			try {
				pictureUrl = new URL((String) parameters.get("picture"));
				jobSeeker.setPicture(pictureUrl);
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}
		}

		// Update the skills
		if (parameters.containsKey("skills")) {
			try {
				List<Skill> skillsList = updateSkillsList(parameters);
				jobSeeker.setSkills(skillsList);
			} catch (Exception ex) {
				throw new SkillExceptions("Error occurred while updating the skills. Try again later");
			}

		}

		// update the workExperience
		if (parameters.containsKey("workExperience")) {
			List<WorkExperience> oldWorkExp = jobSeeker.getWorkExp();
			try {
				workExperienceService.deleteWorkExperience(oldWorkExp);
				List<WorkExperience> workExpList = updateWorkExperience(parameters);
				jobSeeker.setWorkExp(workExpList);
			} catch (Exception ex) {
				throw new WorkExperienceExceptions(
						"Error occurred while updating the work experience. Try again later.");
			}
		}

		// Update the education
		if (parameters.containsKey("education")) {
			List<Education> oldEducationList = jobSeeker.getEducation();
			try {
				educationService.deleteEducation(oldEducationList);
				List<Education> educatonList = updateEducation(parameters);
				jobSeeker.setEducation(educatonList);
			} catch (Exception ex) {
				throw new EducationExceptions("Error occurred while updating the education. Try again later");
			}

		}

		jobSeekerRepository.save(jobSeeker);
		return jobSeeker;
	}

	/**
	 * Get the jobseeker's profile
	 * 
	 * @param id
	 * @return JobSeeker
	 */
	public JobSeeker getProfile(String id) throws JobSeekerExceptions {
		JobSeeker jobSeeker = jobSeekerRepository.findByJobseekerid(id);
		if (jobSeeker == null) {
			throw new JobSeekerExceptions("No profile found.");
		}
		return jobSeeker;
	}

	/**
	 * Update the skills
	 * 
	 * @param parameters
	 * @return List of updated Skill
	 */
	@SuppressWarnings("unchecked")
	public List<Skill> updateSkillsList(Map<String, Object> parameters) {
		List<Skill> skillsList = new ArrayList<>();
		List<LinkedHashMap<String, String>> skillsInput = (List<LinkedHashMap<String, String>>) parameters
				.get("skills");
		for (LinkedHashMap<String, String> skillMap : skillsInput) {
			Skill skill = null;
			try {
				skill = skillService.createSkill(skillMap.get("skill"));
				skillsList.add(skill);
			} catch (Exception ex) {
				System.out.println("Skills already exists, so not adding again");
			}
		}
		return skillsList;
	}

	/**
	 * Update the Work Experience.
	 * 
	 * @param parameters
	 * @return List of Updated WorkExperience
	 */
	@SuppressWarnings("unchecked")
	public List<WorkExperience> updateWorkExperience(Map<String, Object> parameters) {
		List<WorkExperience> workExpList = new ArrayList<>();
		List<LinkedHashMap<String, String>> workExpInput = (List<LinkedHashMap<String, String>>) parameters
				.get("workExperience");
		for (LinkedHashMap<String, String> workExp : workExpInput) {
			WorkExperience workExperience = workExperienceService.createWorkExperience(workExp);
			workExpList.add(workExperience);
		}
		return workExpList;
	}

	/**
	 * Update the education
	 * 
	 * @param parameters
	 * @return List of Updated education
	 */
	@SuppressWarnings("unchecked")
	public List<Education> updateEducation(Map<String, Object> parameters) {
		List<Education> educationList = new ArrayList<>();
		List<LinkedHashMap<String, String>> educationInput = (List<LinkedHashMap<String, String>>) parameters
				.get("education");
		for (LinkedHashMap<String, String> edu : educationInput) {
			Education newEdu = educationService.createEducation(edu);
			educationList.add(newEdu);
		}
		return educationList;
	}

	public JobSeeker addInterestedJobPosting(Map<String, Object> parameters) throws JobPostingException {

		JobSeeker jobSeeker = jobSeekerRepository.findByJobseekerid((String) parameters.get("applicant"));
		JobPosting jobPosting = jobPostingService.getJobPosting((String) parameters.get("jobPostingId"));

		if (jobSeeker.getInterestedJobs().contains(jobPosting)) {
			jobSeeker.getInterestedJobs().remove(jobPosting);
		} else {
			jobSeeker.getInterestedJobs().add(jobPosting);
		}
		jobSeekerRepository.save(jobSeeker);
		return jobSeeker;
	}

}
