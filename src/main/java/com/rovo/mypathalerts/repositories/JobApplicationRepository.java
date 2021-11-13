package com.rovo.mypathalerts.repositories;

import com.rovo.mypathalerts.models.JobApplication;
import com.rovo.mypathalerts.models.JobPosting;
import com.rovo.mypathalerts.models.JobSeeker;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
@RedisHash
@Repository
public interface JobApplicationRepository extends PagingAndSortingRepository<JobApplication, String>  {

	public List<JobApplication> findByJobPosting(JobPosting jobPosting);
	
	public List<JobApplication> findByApplicant(JobSeeker applicant);

	public JobApplication findByApplicantAndJobPosting(JobSeeker applicant, JobPosting jobPosting);
	
	public List<JobApplication> findByApplicantAndApplicationStatus(JobSeeker applicant, int applicationStatus);

	public JobApplication findByApplicationId(String applicationId);
}
