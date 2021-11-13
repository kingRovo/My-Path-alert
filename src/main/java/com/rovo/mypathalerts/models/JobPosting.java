package com.rovo.mypathalerts.models;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.List;

@Entity
public class JobPosting {

	@Id
	@GeneratedValue(generator = "jobPostingIdGenerator")
	@GenericGenerator(name = "jobPostingIdGenerator", strategy = "edu.sjsu.utils.JobPostingIdGenerator")
	private String requisitionId;
	
	private String title;
	
	@ManyToOne
	private Company company;
	
	private String jobDescription;
	
	private String responsibilities;
	
	@ManyToMany
	private List<Skill> skills;
	
	private int salary;
	
	private int status; //3 statuses - 0 - open, 1 - filled, 2 - cancelled
	
	private String location;
	
	public JobPosting() {
		super();
	}

	public String getRequisitionId() {
		return requisitionId;
	}

	public void setRequisitionId(String requisitionId) {
		this.requisitionId = requisitionId;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

	public String getJobDescription() {
		return jobDescription;
	}

	public void setJobDescription(String jobDescription) {
		this.jobDescription = jobDescription;
	}

	public String getResponsibilities() {
		return responsibilities;
	}

	public void setResponsibilities(String responsibilities) {
		this.responsibilities = responsibilities;
	}

	public List<Skill> getSkills() {
		return skills;
	}

	public void setSkills(List<Skill> skills) {
		this.skills = skills;
	}

	public int getSalary() {
		return salary;
	}

	public void setSalary(int salary) {
		this.salary = salary;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}
}
