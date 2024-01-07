package com.cihan.orderservice.controllers.feign;


import com.cihan.orderservice.model.InventoryResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "inventoryWS", url = "${application-properties.inventory-url}")
public interface InventoryFeignClient {

    @RequestMapping(method = RequestMethod.GET)
    List<InventoryResponse> isInStock(@RequestParam List<String> skuCodes);

}
