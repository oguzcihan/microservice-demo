package com.cihan.inventoryservice.services;

import com.cihan.inventoryservice.dtos.InventoryResponse;
import com.cihan.inventoryservice.repository.InventoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class InventoryService {

    private final InventoryRepository inventoryRepository;

    @Transactional(readOnly = true)
    @SneakyThrows //deneme için kullanıldı checked expection yerine geçer
    public List<InventoryResponse> isInStock(List<String> skuCodes) {
        log.info("Wait started");
        Thread.sleep(10000);
        log.info("Wait ended");

        return inventoryRepository.findBySkuCodeIn(skuCodes).stream()
                .map(inventory ->
                    InventoryResponse.builder().skuCode(inventory.getSkuCode())
                            .isInStock(inventory.getQuantity()>0)
                            .build()
                ).toList();
    }
}
