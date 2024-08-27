package com.panchayat.service.ratecard;

import com.panchayat.dto.RateCardDTO;

public interface IRateCardService {
	boolean save(RateCardDTO rateCardDTO);
	RateCardDTO getAll();
	boolean update(RateCardDTO rateCardDTO);

}
