package com.rovo.mypathalerts.repositories;

import com.rovo.mypathalerts.models.WorkExperience;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
@RedisHash
public interface WorkExperienceRepository extends CrudRepository<WorkExperience, String>{

}
