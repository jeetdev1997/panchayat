package com.panchayat.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.panchayat.entity.Building;

@Repository
public interface BuildingRepository extends JpaRepository<Building, Long> {

}
