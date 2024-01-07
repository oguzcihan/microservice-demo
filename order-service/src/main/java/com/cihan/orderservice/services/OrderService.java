package com.cihan.orderservice.services;

import com.cihan.orderservice.controllers.feign.InventoryFeignClient;
import com.cihan.orderservice.dtos.OrderLineItemsDto;
import com.cihan.orderservice.dtos.OrderRequest;
import com.cihan.orderservice.model.InventoryResponse;
import com.cihan.orderservice.model.Order;
import com.cihan.orderservice.model.OrderLineItems;
import com.cihan.orderservice.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;
    private final WebClient.Builder webClientBuilder;
    private final InventoryFeignClient inventoryFeignClient;

    public void placeOrder(OrderRequest orderRequest) {
        Order order = new Order();
        order.setOrderNumber(UUID.randomUUID().toString());

        List<OrderLineItems> orderLineItems = orderRequest.getOrderLineItemsDtoList()
                .stream()
                .map(this::mapToDto)
                .toList();

        order.setOrderLineItemsList(orderLineItems);

        List<String> skuCodes = order.getOrderLineItemsList().stream()
                .map(OrderLineItems::getSkuCode).toList();

        //sipariş verirken inventory den stock ta var mı yok mu kontrol eder
        List<InventoryResponse> inventoryResponseList = inventoryFeignClient.isInStock(skuCodes);
        boolean allProducts = inventoryResponseList.stream().allMatch(InventoryResponse::isInStock);


        if (allProducts) {
            orderRepository.save(order);
        } else {
            throw new IllegalArgumentException("Product is not stock, please try again later!");
        }

    }

    private OrderLineItems mapToDto(OrderLineItemsDto orderLineItemsDto) {

        OrderLineItems orderLineItems = new OrderLineItems();
        orderLineItems.setPrice(orderLineItemsDto.getPrice());
        orderLineItems.setSkuCode(orderLineItemsDto.getSkuCode());
        orderLineItems.setQuantity(orderLineItemsDto.getQuantity());

        return orderLineItems;
    }
}

//call inventory service, and place order if products is in
//stock
//        InventoryResponse[] result = webClientBuilder.build().get()
//                .uri("http://inventory-service/api/inventory",
//                        uriBuilder -> uriBuilder.queryParam("skuCodes", skuCodes).build())
//                .retrieve()
//                .bodyToMono(InventoryResponse[].class)
//                .block();
//boolean allProduct = Arrays.stream(result).allMatch(InventoryResponse::isInStock);


