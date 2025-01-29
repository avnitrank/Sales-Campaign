package com.example.saleCampaign.Service; 

import com.example.saleCampaign.Model.Campaign;
import com.example.saleCampaign.Model.CampaignDiscount;
import com.example.saleCampaign.Model.Product;
import com.example.saleCampaign.Model.ResponseDTO;
import com.example.saleCampaign.Repository.CampaignDiscountRepository;
import com.example.saleCampaign.Repository.CampaignRepository;
import com.example.saleCampaign.Repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class CampaignService {
    @Autowired
    private CampaignRepository campaignRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    CampaignDiscountRepository campaignDiscountRepository;

    @Autowired
    private ProductService productService;

    public ResponseDTO<Campaign> saveCampaign(Campaign campaign){
        try {
            campaignRepository.save(campaign);
            for (CampaignDiscount campaignDiscount : campaign.getCampaignDiscounts()) {
                campaignDiscount.setCampaign(campaign);
//                CampaignDiscount temp = new CampaignDiscount();
//                temp.setProductId(campaignDiscount.getProductId());
//                temp.setDiscount(campaignDiscount.getDiscount());
//                temp.setCampaign(campaign);
//                campaignDiscount.setDiscount(campaignDiscount.getDiscount());
//                campaignDiscount.setProductId(campaignDiscount.getProductId());
                campaignDiscountRepository.save(campaignDiscount);
            }
            return new ResponseDTO<>(campaign, HttpStatus.OK, "save campaign successfully");
        } catch (Exception e) {
            return new ResponseDTO<>(null, HttpStatus.INTERNAL_SERVER_ERROR, "failed to save " + e.getMessage());
        }
    }


    public ResponseDTO<List<Campaign>> getAllCampaigns() {
        List<Campaign> campaigns = campaignRepository.findAll();
        return new ResponseDTO<>(campaigns, HttpStatus.OK, "get all campaigns successfully");
    }


}
