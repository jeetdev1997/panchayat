package com.panchayat.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.panchayat.entity.Property;

@Repository
public interface PropertyRepository extends JpaRepository<Property, Long> {
	
	Optional<Property> findByOwnerNameAndPropertyNo(String ownerName, String propertyNo);
	Optional<Property> findByOwnerName(String ownername);
}
