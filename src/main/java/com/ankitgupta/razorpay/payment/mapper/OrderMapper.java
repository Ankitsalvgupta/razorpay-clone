package com.ankitgupta.razorpay.payment.mapper;

import com.ankitgupta.razorpay.payment.dto.response.OrderResponse;
import com.ankitgupta.razorpay.payment.entity.OrderRecord;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface OrderMapper {

    OrderResponse toResponse(OrderRecord orderRecord);
}
