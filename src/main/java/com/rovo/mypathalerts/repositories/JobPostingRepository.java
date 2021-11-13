package com.rovo.mypathalerts.repositories;

import com.rovo.mypathalerts.models.Company;
import com.rovo.mypathalerts.models.JobPosting;
import com.rovo.mypathalerts.models.Skill;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
@RedisHash
@Repository
public interface JobPostingRepository extends PagingAndSortingRepository<JobPosting, String> {

	public JobPosting findByRequisitionId(String requisitionId);

	public List<JobPosting> findByCompany(Company company);

	public Page<JobPosting> findByStatus(int status, Pageable pageable);

	public List<JobPosting> findByStatusAndCompanyInAndLocationInAndSalaryGreaterThanAndSalaryLessThan(int status,
			List<Company> company, List<String> locations, int salaryMin, int salaryMax, Pageable pageable);

	public List<JobPosting> findByStatusAndCompanyInAndLocationInAndSalary(int status, List<Company> company,
			List<String> locations, int salary, Pageable pageable);

	public List<JobPosting> findByStatusAndCompanyInAndLocationInAndSkillsInAndJobDescriptionContainingIgnoreCaseOrResponsibilitiesContainingIgnoreCaseOrTitleContainingIgnoreCase(
			int status, List<Company> company, List<String> locations, List<Skill> skills, String jobDescription,
			String responsibilities, String title);

	public List<JobPosting> findByStatusAndCompanyInAndLocationIn(int status, List<Company> company,
			List<String> locations);

	public List<JobPosting> findByStatusAndSkillsInOrJobDescriptionContainingIgnoreCaseOrResponsibilitiesContainingIgnoreCaseOrTitleContainingIgnoreCaseOrLocationContainingIgnoreCase(
			int status, List<Skill> skills, String jobDescription, String responsibilities, String title, String location, Pageable pageable);
		
}
