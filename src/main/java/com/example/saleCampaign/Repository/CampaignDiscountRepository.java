package com.example.saleCampaign.Repository;

import com.example.saleCampaign.Model.CampaignDiscount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CampaignDiscountRepository extends JpaRepository<CampaignDiscount,Integer> {
}
