package com.pm.billingservice.grpc;

import billing.BillingRequest;
import billing.BillingResponse;
import billing.BillingServiceGrpc;
import com.pm.billingservice.model.Billing;
import com.pm.billingservice.repository.BillingRepository;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

@GrpcService
public class BillingGrpcServiceImpl extends BillingServiceGrpc.BillingServiceImplBase {
    private static final Logger log = LoggerFactory.getLogger(
            BillingGrpcServiceImpl.class);

    private final BillingRepository billingRepository;

    public BillingGrpcServiceImpl(BillingRepository billingRepository) {
        this.billingRepository = billingRepository;
    }

    @Override
    public void createBillingAccount(BillingRequest billingRequest,
                                     StreamObserver<BillingResponse> responseObserver) {

        log.info("createBillingAccount request received {}", billingRequest.toString());

        // Business logic - e.g save to database, perform calculates etc
        Billing billing = new Billing(billingRequest.getPatientId(),billingRequest.getName(),billingRequest.getEmail());
        billing.setAccountId(UUID.randomUUID());
        billing.setAccountStatus("ACTIVE");
        billingRepository.save(billing);

        BillingResponse response = BillingResponse.newBuilder()
                .setAccountId(billing.getAccountId().toString())
                .setStatus(billing.getAccountStatus())
                .build();
        log.info("createBillingAccount request response {}", response.toString());

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
