package com.panchayat.service.ratecard;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.panchayat.dto.RateCardDTO;
import com.panchayat.entity.RateCard;
import com.panchayat.repository.RateCardRepository;
import com.panchayat.utils.PanchayatHelpher;

@Service
public class RateCardServiceImpl implements IRateCardService {
	@Autowired
	RateCardRepository rateCardRepository;

	@Override
	public boolean save(RateCardDTO rateCardDTO) {
		boolean isSaved = false;
		System.out.println("rate card dto ==> " + rateCardDTO.toString());
		RateCard rateCard = PanchayatHelpher.convertRateCardDTO(rateCardDTO);
		System.out.println("rate card entity ==> " + rateCard.toString());
		rateCardRepository.save(rateCard);
		isSaved = true;
		return isSaved;
	}

	@Override
	public RateCardDTO getAll() {
		// TODO Auto-generated method stub
		List<RateCard> list= rateCardRepository.findAll();
		RateCardDTO cardDTO = PanchayatHelpher.convertRateCardEntity(list.get(0));
		return cardDTO;
	}

	@Override
	public boolean update(RateCardDTO rateCardDTO) {
		// TODO Auto-generated method stub
		return false;
	}

}
