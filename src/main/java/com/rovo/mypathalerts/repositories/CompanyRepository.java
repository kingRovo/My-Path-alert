package com.rovo.mypathalerts.repositories;

import com.rovo.mypathalerts.models.Company;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
@RedisHash
@Repository
public interface CompanyRepository extends CrudRepository<Company, String> {

	public Company findByCompanyName(String companyName);
	
	public Company findByEmailAndPassword(String email, String password);
	
	public Company findByEmail(String email);
	
	public Company findByCompanyNameAndVerificationCode(String companyName, String verificationCode);
	
	public List<Company> findByCompanyNameIn(List<String> companyName);
}
