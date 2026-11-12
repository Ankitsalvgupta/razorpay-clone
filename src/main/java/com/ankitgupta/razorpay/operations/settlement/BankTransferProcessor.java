package com.ankitgupta.razorpay.operations.settlement;

import com.ankitgupta.razorpay.common.entity.Money;
import com.ankitgupta.razorpay.operations.settlement.dto.BankTransferResult;

import java.util.UUID;

public interface BankTransferProcessor {

    BankTransferResult initiate(UUID settlementId, UUID merchantId, Money amount, String bankAccount, String ifsc);
}
