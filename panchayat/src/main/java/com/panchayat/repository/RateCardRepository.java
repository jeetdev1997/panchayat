package com.panchayat.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.panchayat.entity.RateCard;

@Repository
public interface RateCardRepository extends JpaRepository<RateCard, Long> {

}
