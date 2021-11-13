package com.rovo.mypathalerts.services;

import com.rovo.mypathalerts.exceptions.JobApplicationExceptions;
import com.rovo.mypathalerts.exceptions.JobPostingException;
import com.rovo.mypathalerts.exceptions.JobSeekerExceptions;
import com.rovo.mypathalerts.models.JobApplication;
import com.rovo.mypathalerts.models.JobPosting;
import com.rovo.mypathalerts.models.JobSeeker;
import com.rovo.mypathalerts.repositories.JobApplicationRepository;
import com.rovo.mypathalerts.utils.JobApplicationStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class JobApplicationService {

	@Autowired
	JobApplicationRepository jobApplicationRepository;

	@Autowired
	JobSeekerService jobSeekerService;

	@Autowired
	JobPostingService jobPostingService;

	@Autowired
	EmailService emailService;

	/**
	 * Create the new Job Application
	 * 
	 * @param parameters
	 * @return JobApplication
	 * @throws JobSeekerExceptions
	 * @throws JobPostingException
	 * @throws JobApplicationExceptions
	 */
	public JobApplication createJobApplication(Map<String, Object> parameters)
			throws JobSeekerExceptions, JobPostingException, JobApplicationExceptions {

		String jobSeekerId = (String) parameters.get("applicant");
		String jobPostingId = (String) parameters.get("jobPostingId");

		JobSeeker jobSeeker = jobSeekerService.getProfile(jobSeekerId);
		JobPosting jobPosting = jobPostingService.getJobPosting(jobPostingId);

		List<JobApplication> jobApplications = jobApplicationRepository.findByApplicantAndApplicationStatus(jobSeeker,
				0);
		if (jobApplications.size() >= 5) {
			throw new JobApplicationExceptions("You already have 5 pending job applications");
		}

		JobApplication jobApplicationExist = jobApplicationRepository.findByApplicantAndJobPosting(jobSeeker,
				jobPosting);
		if (jobApplicationExist != null) {
			throw new JobApplicationExceptions("You have already applied to this job with application Id : "
					+ jobApplicationExist.getApplicationId());
		}

		JobApplication jobApplication = new JobApplication();

		jobApplication.setApplicationStatus(0);
		jobApplication.setApplicant(jobSeeker);
		jobApplication.setJobPosting(jobPosting);

		if (parameters.containsKey("resume")) {
			jobApplication.setResume((String) parameters.get("resume"));
		}

		jobApplicationRepository.save(jobApplication);
		StringBuilder subject = new StringBuilder();
		subject.append("Thank you for your interest at " + jobPosting.getCompany().getCompanyName());

		StringBuilder message = new StringBuilder();
		message.append("Dear " + jobSeeker.getFirstname());
		message.append("\n\nWe would like to thank you for applying for the following job at "
				+ jobPosting.getCompany().getCompanyName());
		message.append("\nPosition : " + jobPosting.getTitle());
		message.append("\nRequisition Id : " + jobPosting.getRequisitionId());
		message.append(
				"\n\nOne of our representative will review your application and get back to you, should your skills matches the requirements");
		message.append("\n\n\nBest Regards,");
		message.append("\nTalent Acquisition Team");
		message.append("\n" + jobPosting.getCompany().getCompanyName());

		emailService.sendMail(jobSeeker.getEmail(), subject.toString(), message.toString());
		return jobApplication;
	}

	/**
	 * Get the JobApplication from JobPosting
	 * 
	 * @param jobPosting
	 * @return List<JobApplication>
	 */
	public List<JobApplication> getJobApplicationByJobPosting(JobPosting jobPosting) throws JobApplicationExceptions {
		List<JobApplication> jobApplications = jobApplicationRepository.findByJobPosting(jobPosting);
		if (jobApplications.size() == 0)
			throw new JobApplicationExceptions("No Job application found");
		return jobApplications;
	}

	/**
	 * Get JobApplications by job applicant
	 * 
	 * @param jobseeker
	 * @return List<JobApplication>
	 * @throws JobApplicationExceptions
	 */
	public List<JobApplication> getJobApplicationByJobSeeker(JobSeeker jobseeker) throws JobApplicationExceptions {
		List<JobApplication> jobApplications = jobApplicationRepository.findByApplicant(jobseeker);
		List<JobApplication> openJobApplication = new ArrayList<>();
		for (JobApplication jobApp : jobApplications) {
			if (jobApp.getJobPosting().getStatus() == 0) {
				openJobApplication.add(jobApp);
			}
		}
		if (openJobApplication.size() == 0) {
			throw new JobApplicationExceptions("No Job application found");
		}
		return openJobApplication;
	}

	/**
	 * Updates the job application status accessed by company
	 * 
	 * @param parameters
	 * @return JobApplication
	 * @throws JobApplicationExceptions
	 */
	public JobApplication updateStatusByCompany(Map<String, Object> parameters) throws JobApplicationExceptions {

		String jobApplicationId = (String) parameters.get("jobApplicationId");
		int status = Integer.parseInt(parameters.get("status").toString());

		JobApplication jobApplication = jobApplicationRepository.findByApplicationId(jobApplicationId);
		JobSeeker applicant = jobApplication.getApplicant();
		int oldStatus = jobApplication.getApplicationStatus();

		if (jobApplication.getJobPosting().getStatus() == 0) {
			jobApplication.setApplicationStatus(status);
		} else {
			if (oldStatus > status) {
				throw new JobApplicationExceptions(
						"Application status can not be set to '" + JobApplicationStatus.getStatus(status) + "' from '"
								+ JobApplicationStatus.getStatus(oldStatus) + "'");
			}
			throw new JobApplicationExceptions("Job posting has been filled or cancelled");
		}

		jobApplicationRepository.save(jobApplication);

		// Send the mail about the status change
		StringBuilder subject = new StringBuilder();
		subject.append("Your application status has been changed");
		StringBuilder message = new StringBuilder();
		message.append("Dear " + applicant.getFirstname());
		message.append("\n\nYour job application status has been updated from '"
				+ JobApplicationStatus.getStatus(oldStatus) + "' to '" + JobApplicationStatus.getStatus(status) + "'.");
		message.append("\n\nCompany : " + jobApplication.getJobPosting().getCompany().getCompanyName());
		message.append("\nPosition : " + jobApplication.getJobPosting().getTitle());
		message.append("\nRequisition Id : " + jobApplication.getJobPosting().getRequisitionId());
		message.append("\nApplication Id : " + jobApplication.getApplicationId());
		message.append("\n\nBest Regards,");
		message.append("\nTalent Acquisition Team " + jobApplication.getJobPosting().getCompany().getCompanyName());

		emailService.sendMail(applicant.getEmail(), subject.toString(), message.toString());

		return jobApplication;
	}

	/**
	 * Updates the status of job application when accessed by the job applicant
	 * 
	 * @param parameters
	 * @return JobApplication
	 * @throws JobApplicationExceptions
	 */
	public JobApplication updateStatusByApplicant(Map<String, Object> parameters) throws JobApplicationExceptions {

		String jobApplicationId = (String) parameters.get("jobApplicationId");
		int status = Integer.parseInt(parameters.get("status").toString());

		JobApplication jobApplication = jobApplicationRepository.findByApplicationId(jobApplicationId);
		JobSeeker applicant = jobApplication.getApplicant();
		int oldStatus = jobApplication.getApplicationStatus();

		if (status == 2) {
			if (oldStatus > 2) {
				throw new JobApplicationExceptions("You can not cancel the application once you have offered a job");
			} else {
				jobApplication.setApplicationStatus(status);
			}
		}

		if (jobApplication.getApplicationStatus() == 3) {
			jobApplication.setApplicationStatus(status);

			if (status == 4) {
				// send email to all remaining applicants
				List<JobApplication> jobsApps = jobApplicationRepository
						.findByJobPosting(jobApplication.getJobPosting());
				for (JobApplication app1 : jobsApps) {
					if (app1.getApplicant() != jobApplication.getApplicant()) {
						app1.setApplicationStatus(2);

						StringBuilder subject = new StringBuilder();
						subject.append("Your application status has been changed");
						StringBuilder message = new StringBuilder();
						message.append("Dear " + app1.getApplicant().getFirstname());
						message.append("\n\nYour job application status has been updated from '"
								+ JobApplicationStatus.getStatus(oldStatus) + "' to '"
								+ JobApplicationStatus.getStatus(2) + "' as position has been filled");
						message.append("\n\nCompany : " + app1.getJobPosting().getCompany().getCompanyName());
						message.append("\nPosition : " + app1.getJobPosting().getTitle());
						message.append("\nRequisition Id : " + app1.getJobPosting().getRequisitionId());
						message.append("\nApplication Id : " + app1.getApplicationId());
						message.append("\n\nBest Regards,");
						message.append(
								"\nTalent Acquisition Team " + app1.getJobPosting().getCompany().getCompanyName());

						emailService.sendMail(app1.getApplicant().getEmail(), subject.toString(), message.toString());
					}
				}
			}

		} else {
			if (jobApplication.getApplicationStatus() == 4) {
				throw new JobApplicationExceptions("You have already accepted the job offer");
			}
			throw new JobApplicationExceptions("You can not accept job offer unless you have been offered a job");
		}
		jobApplicationRepository.save(jobApplication);

		StringBuilder subject = new StringBuilder();
		subject.append("Your application status has been changed");
		StringBuilder message = new StringBuilder();
		message.append("Dear " + applicant.getFirstname());
		message.append("\n\nYour job application status has been updated from '"
				+ JobApplicationStatus.getStatus(oldStatus) + "' to '" + JobApplicationStatus.getStatus(status) + "'.");
		message.append("\n\nCompany : " + jobApplication.getJobPosting().getCompany().getCompanyName());
		message.append("\nPosition : " + jobApplication.getJobPosting().getTitle());
		message.append("\nRequisition Id : " + jobApplication.getJobPosting().getRequisitionId());
		message.append("\nApplication Id : " + jobApplication.getApplicationId());
		message.append("\n\nBest Regards,");
		message.append("\nTalent Acquisition Team " + jobApplication.getJobPosting().getCompany().getCompanyName());

		emailService.sendMail(applicant.getEmail(), subject.toString(), message.toString());

		return jobApplication;
	}

	public HashMap<String, String> getErrorResponse(String errorcode, String error) {
		HashMap<String, String> errorMap = new HashMap<String, String>();
		errorMap.put("code", errorcode);
		errorMap.put("msg", error);
		return errorMap;
	}
}
