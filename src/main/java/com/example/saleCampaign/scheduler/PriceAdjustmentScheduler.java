package com.example.saleCampaign.scheduler;

import com.example.saleCampaign.Model.Campaign;
import com.example.saleCampaign.Model.CampaignDiscount;
import com.example.saleCampaign.Model.PriceHistory;
import com.example.saleCampaign.Model.Product;
import com.example.saleCampaign.Repository.CampaignRepository; 
import com.example.saleCampaign.Repository.PriceHistoryRepository; 
import com.example.saleCampaign.Repository.ProductRepository;
import com.example.saleCampaign.Service.ProductService; 
import org.springframework.beans.factory.annotation.Autowired; 
import org.springframework.scheduling.annotation.Scheduled; 
import org.springframework.stereotype.Component;  
import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@Component 
public class PriceAdjustmentScheduler {

    @Autowired
    private ProductRepository productRepository; 

    @Autowired 
    private CampaignRepository campaignRepository;

    @Autowired
    private ProductService productService;

    @Autowired
    PriceHistoryRepository priceHistoryRepository;
 
    @Scheduled(cron = "0 0 0 * * *")
    public void adjustProductPrices() {
        System.out.println("campaign started "+LocalDate.now());
        LocalDate today = LocalDate.now();
        List<Campaign> activeSales = campaignRepository.findAllByStartDate(today);

        for (Campaign campaign : activeSales) {
            List<CampaignDiscount> discounts = campaign.getCampaignDiscounts();
            for (CampaignDiscount discount : discounts) {
                Product product = productRepository.findById(discount.getProductId()).orElse(null);

                if (product != null) {
                    double discountAmount =  (product.getCurrentprice() * (discount.getDiscount() / 100));
                    double newPrice = (product.getCurrentprice() - discountAmount);
 
                    if (newPrice >= 0) {
                        product.setCurrentprice(newPrice);
                        product.setDiscount((product.getDiscount()+discount.getDiscount()));
                        productRepository.save(product);
                        priceHistoryRepository.save(new PriceHistory(product,product.getCurrentprice()+discountAmount,newPrice, discount.getDiscount(),discountAmount , LocalDate.now()));
                    }
                }
            }
        }
    }

    @Scheduled(cron = "0 59 23 * * *")
    public void revertPrice(){
        System.out.println("compaign end "+ LocalDate.now());
        LocalDate today = LocalDate.now();

        List<Campaign> endedSales = campaignRepository.findAllByEndDate(today);

        for (Campaign campaign : endedSales) {
            List<CampaignDiscount> discounts = campaign.getCampaignDiscounts();
            for (CampaignDiscount discount : discounts) {
                Product product = productRepository.findById(discount.getProductId()).orElse(null);
                if (product != null) {
                    LocalDate date = campaign.getStartDate();

                    //product history when compaign start
                    PriceHistory priceHistoryStart = priceHistoryRepository.findTopByProductIdAndDate(product.getId(), date);

                    //product latest history
                    PriceHistory priceHistoryLatest = priceHistoryRepository.findByProductIdOrderByDate(product.getId());

                    //product history before campaign start
                    PriceHistory priceHistorybeforeStart = priceHistoryRepository.findByProductIdAndBeforeDate(product.getId(),date);

                    if (priceHistoryStart != null) {
                        double previousPrice = priceHistoryLatest.getFinalPrice();
                        double currentprice = previousPrice+priceHistoryStart.getDiscountAmount();

                        product.setCurrentprice(currentprice);
                        product.setDiscount((float) (product.getDiscount()-priceHistoryStart.getDiscountInPercentage()));
                        productRepository.save(product);

                        //for price history
                        double finalPrice = currentprice;
                        double price = priceHistoryLatest.getPrice()+(priceHistorybeforeStart.getPrice()-priceHistoryStart.getPrice());
                        double discountAmount = price-finalPrice;
                        double discountInPercnetage = (discountAmount*100)/price;
                        priceHistoryRepository.save(new PriceHistory(product,price,finalPrice,discountInPercnetage,discountAmount,LocalDate.now()));
                    }
                }
            }
        }
    }
}
