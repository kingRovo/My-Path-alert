package com.rovo.mypathalerts.repositories;

import com.rovo.mypathalerts.models.Education;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@RedisHash
@Repository
public interface EducationRepository extends CrudRepository<Education, String>{

}
