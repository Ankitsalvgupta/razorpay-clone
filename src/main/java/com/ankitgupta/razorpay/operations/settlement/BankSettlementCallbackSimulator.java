package com.ankitgupta.razorpay.operations.settlement;

import com.ankitgupta.razorpay.common.enums.SettlementStatus;
import com.ankitgupta.razorpay.common.util.RandomizerUtil;
import com.ankitgupta.razorpay.operations.entity.Settlement;
import com.ankitgupta.razorpay.operations.repository.SettlementRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Component
public class BankSettlementCallbackSimulator {

    private final SettlementRepository settlementRepository;
    private final SettlementTransactionExecutor settlementTransactionExecutor;

    @Scheduled(fixedDelayString =  "5000")
    public void processCallbacks() {
        List<Settlement> settlements = settlementRepository.findByStatus(SettlementStatus.TRANSFER_PENDING);
        if(settlements.isEmpty()) return;

        for(Settlement settlement: settlements) {
            simulateCallback(settlement);
        }
    }

    private void simulateCallback(Settlement settlement) {
        log.info("Initiating settlement callback for settlementId: {}", settlement.getId());
        String utrNumber = "UTR_" + RandomizerUtil.randomBase64(12);
        settlementTransactionExecutor.resolverTransfer(settlement.getId(), null, null);
    }
}
