package com.rovo.mypathalerts.models;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.List;

@Entity
public class JobApplication {

	
	@Id
	@GeneratedValue(generator = "jobApplicationIdGenerator")
	@GenericGenerator(name = "jobApplicationIdGenerator", strategy = "edu.sjsu.utils.JobApplicationIdGenerator")
	private String applicationId; //generate id - use existing id generation logic of ariline system.
	
	@OneToOne
	private JobSeeker applicant;
	
	@OneToOne
	private JobPosting jobPosting;
	
	private String resume;
	
	private int applicationStatus; 	// 0 - Pending, 1 - Offered, 2 - Rejected, 4 - Offer Accepted, 5 - Offer Rejected, 6 - cancelled
									// 0 - By default, 1, 2, 6 - changed by company
									// 4, 5 - changed by job seeker
	
	//private Interview interview; -- to be updated - logic not clear & it's bonus
	@OneToMany
	private List<Interview> interviews;
	
	public List<Interview> getInterviews() {
		return interviews;
	}

	public void setInterviews(List<Interview> interviews) {
		this.interviews = interviews;
	}

	public JobApplication() {
		super();
	}

	public String getApplicationId() {
		return applicationId;
	}

	public void setApplicationId(String applicationId) {
		this.applicationId = applicationId;
	}

	public JobSeeker getApplicant() {
		return applicant;
	}

	public void setApplicant(JobSeeker applicant) {
		this.applicant = applicant;
	}

	public JobPosting getJobPosting() {
		return jobPosting;
	}

	public void setJobPosting(JobPosting jobPosting) {
		this.jobPosting = jobPosting;
	}

	public int getApplicationStatus() {
		return applicationStatus;
	}

	public void setApplicationStatus(int applicationStatus) {
		this.applicationStatus = applicationStatus;
	}

	public String getResume() {
		return resume;
	}

	public void setResume(String resume) {
		this.resume = resume;
	}
	
}
