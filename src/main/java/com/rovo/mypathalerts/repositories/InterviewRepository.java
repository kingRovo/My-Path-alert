package com.rovo.mypathalerts.repositories;

import com.rovo.mypathalerts.models.Interview;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@RedisHash
@Repository
public interface InterviewRepository extends CrudRepository<Interview, String> {

	public Interview findByStatus(int status);
	
	public Interview findByInterviewNo(int interviewNo);
}
