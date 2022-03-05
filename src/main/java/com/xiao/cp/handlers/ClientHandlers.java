package com.xiao.cp.handlers;

import eu.chargetime.ocpp.feature.profile.ClientCoreEventHandler;
import eu.chargetime.ocpp.model.Confirmation;
import eu.chargetime.ocpp.model.Request;
import eu.chargetime.ocpp.model.core.*;

import java.lang.reflect.Type;
import java.util.function.BiConsumer;

public class ClientHandlers {
    private Confirmation receivedConfirmation;
    private Request receivedRequest;
    private Throwable receivedException;

    public ClientCoreEventHandler createClientCoreEventHandler(){
        return new ClientCoreEventHandler() {
            @Override
            public ChangeAvailabilityConfirmation handleChangeAvailabilityRequest(
                    ChangeAvailabilityRequest request) {
                // debug
                System.out.println(request);

                // handle events
                receivedRequest = request;
                return new ChangeAvailabilityConfirmation(AvailabilityStatus.Accepted);
            }

            @Override
            public GetConfigurationConfirmation handleGetConfigurationRequest(
                    GetConfigurationRequest request) {
                // debug
                System.out.println(request);
                // handle events
                receivedRequest = request;
                return new GetConfigurationConfirmation();
            }

            @Override
            public ChangeConfigurationConfirmation handleChangeConfigurationRequest(
                    ChangeConfigurationRequest request) {
                receivedRequest = request;
                ChangeConfigurationConfirmation confirmation =
                        new ChangeConfigurationConfirmation();
                confirmation.setStatus(ConfigurationStatus.Accepted);
                return confirmation;
            }

            @Override
            public ClearCacheConfirmation handleClearCacheRequest(ClearCacheRequest request) {
                receivedRequest = request;
                ClearCacheConfirmation confirmation = new ClearCacheConfirmation();
                confirmation.setStatus(ClearCacheStatus.Accepted);
                return confirmation;
            }

            @Override
            public DataTransferConfirmation handleDataTransferRequest(
                    DataTransferRequest request) {
                receivedRequest = request;
                DataTransferConfirmation confirmation = new DataTransferConfirmation();
                confirmation.setStatus(DataTransferStatus.Accepted);
                return confirmation;
            }

            @Override
            public RemoteStartTransactionConfirmation handleRemoteStartTransactionRequest(
                    RemoteStartTransactionRequest request) {
                receivedRequest = request;
                return new RemoteStartTransactionConfirmation(RemoteStartStopStatus.Accepted);
            }

            @Override
            public RemoteStopTransactionConfirmation handleRemoteStopTransactionRequest(
                    RemoteStopTransactionRequest request) {
                receivedRequest = request;
                return new RemoteStopTransactionConfirmation(RemoteStartStopStatus.Accepted);
            }

            @Override
            public ResetConfirmation handleResetRequest(ResetRequest request) {
                receivedRequest = request;
                return new ResetConfirmation(ResetStatus.Accepted);
            }

            @Override
            public UnlockConnectorConfirmation handleUnlockConnectorRequest(
                    UnlockConnectorRequest request) {
                receivedRequest = request;
                return new UnlockConnectorConfirmation(UnlockStatus.Unlocked);
            }
        };
    }

    public void clearMemory() {
        receivedConfirmation = null;
        receivedException = null;
        receivedRequest = null;
    }

    public BiConsumer<Confirmation, Throwable> generateWhenCompleteHandler() {
        return (confirmation, throwable) -> receivedConfirmation = confirmation;
    }

    public boolean wasLatestRequest(Type requestType) {
        return requestType != null
                && receivedRequest != null
                && requestType.equals(receivedRequest.getClass());
    }

    public <T extends Request> T getReceivedRequest(T requestType) {
        if (wasLatestRequest(requestType.getClass())) return (T) receivedRequest;
        return null;
    }

    public boolean wasLatestConfirmation(Type confirmationType) {
        return confirmationType != null
                && receivedConfirmation != null
                && confirmationType.equals(receivedConfirmation.getClass());
    }

    public <T extends Confirmation> T getReceivedConfirmation(T confirmationType) {
        if (wasLatestConfirmation(confirmationType.getClass())) return (T) receivedConfirmation;
        return null;
    }

//    public boolean wasLatestExcption(Type exceptionType) {
//        return exceptionType != null
//                && receivedException != null
//                && exceptionType.equals(receivedException.getClass());
//    }
//
//    public <T extends Throwable> T getReceivedException(T exceptionType) {
//        if (wasLatestConfirmation(exceptionType.getClass())) return (T) receivedException;
//        return null;
//    }

}
