package com.rovo.mypathalerts.repositories;

import com.rovo.mypathalerts.models.Skill;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
@RedisHash
@Repository
public interface SkillRepository extends CrudRepository<Skill, String> {

	public Skill findBySkill(String skill);
	
	public List<Skill> findBySkillIn(List<String> skill);
}
