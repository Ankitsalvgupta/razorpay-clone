package com.ankitgupta.razorpay.merchant.mapper;

import com.ankitgupta.razorpay.merchant.dto.response.ApiKeyCreateResponse;
import com.ankitgupta.razorpay.merchant.dto.response.ApiKeyResponse;
import com.ankitgupta.razorpay.merchant.entity.ApiKey;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ApiKeyMapper {

    List<ApiKeyResponse> toResponseList(List<ApiKey> apiKeysList);
}
