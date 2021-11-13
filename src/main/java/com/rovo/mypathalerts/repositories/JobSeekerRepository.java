package com.rovo.mypathalerts.repositories;

import com.rovo.mypathalerts.models.JobSeeker;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
@RedisHash
public interface JobSeekerRepository extends CrudRepository<JobSeeker, String>{

	public JobSeeker findByEmailAndPassword(String email, String password);
	
	public JobSeeker findByEmail(String email);
	
	public JobSeeker findByJobseekerid(String jobseekerid);
	
	public JobSeeker findByJobseekeridAndVerificationCode(String id, String verificationCode);
}
