package com.xiao.cp.model;

import com.xiao.cp.config.ApiConfigurations;
import com.xiao.cp.handlers.ClientHandlers;
import eu.chargetime.ocpp.*;
import eu.chargetime.ocpp.feature.profile.*;
import eu.chargetime.ocpp.model.Request;
import eu.chargetime.ocpp.model.core.*;
import eu.chargetime.ocpp.model.firmware.DiagnosticsStatus;
import eu.chargetime.ocpp.model.firmware.DiagnosticsStatusNotificationRequest;
import eu.chargetime.ocpp.model.firmware.FirmwareStatus;
import eu.chargetime.ocpp.model.firmware.FirmwareStatusNotificationRequest;
import eu.chargetime.ocpp.model.smartcharging.*;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;

@Slf4j
@Component
@AllArgsConstructor
public class ChargePoint {
    private final ClientCoreProfile core;
//    private final ClientSmartChargingProfile smartCharging;
//    private final ClientRemoteTriggerProfile remoteTrigger;
//    private final ClientFirmwareManagementProfile firmware;
//    private final ClientLocalAuthListProfile localAuthList;
//    private final ClientReservationProfile reservation;
    private IClientAPI client;
    private ClientHandlers clientHandlers;
    private ApiConfigurations apiConfigurations;
    private String url;

    public ChargePoint(){
        clientHandlers = new ClientHandlers();

        core = new ClientCoreProfile(clientHandlers.createClientCoreEventHandler());

//        smartCharging =
//                new ClientSmartChargingProfile(
//                        new ClientSmartChargingEventHandler() {
//                            @Override
//                            public SetChargingProfileConfirmation handleSetChargingProfileRequest(
//                                    SetChargingProfileRequest request) {
//
//                                System.out.println(request);
//
//                                return new SetChargingProfileConfirmation(ChargingProfileStatus.Accepted);
//                            }
//
//                            @Override
//                            public ClearChargingProfileConfirmation handleClearChargingProfileRequest(
//                                    ClearChargingProfileRequest request) {
//
//                                System.out.println(request);
//
//                                return new ClearChargingProfileConfirmation(ClearChargingProfileStatus.Accepted);
//                            }
//
//                            @Override
//                            public GetCompositeScheduleConfirmation handleGetCompositeScheduleRequest(
//                                    GetCompositeScheduleRequest request) {
//
//                                System.out.println(request);
//
//                                return new GetCompositeScheduleConfirmation(GetCompositeScheduleStatus.Accepted);
//                            }
//                        });

        client = new JSONClient(core);
        url = "http://springbootocppmysqlaws-env.eba-2hue2jqm.eu-north-1.elasticbeanstalk.com/";
    }

    public void connect() throws Exception{
        client.connect(
                url,
                new ClientEvents() {
                    @Override
                    public void connectionOpened() {
                        System.out.println("[CLIENT] connection opened: " + url);
                    }

                    @Override
                    public void connectionClosed() {}
                });
    }

    public void disconnect() {
        client.disconnect();
    }

    public void sendAuthorizeRequest(String idToken) throws Exception {
        Request request = core.createAuthorizeRequest(idToken);
        send(request);
    }

    public void sendBootNotification(String vendor, String model) throws Exception {
        Request request = core.createBootNotificationRequest(vendor, model);
        send(request);
    }

    public void sendIncompleteAuthorizeRequest() throws Exception {
        Request request = new AuthorizeRequest();
        send(request);
    }

    public void sendHeartbeatRequest() throws Exception {
        Request request = core.createHeartbeatRequest();
        send(request);
    }

    public void sendMeterValuesRequest() throws Exception {
        try {
            Request request = core.createMeterValuesRequest(42, ZonedDateTime.now(), "42");
            send(request);
        } catch (PropertyConstraintException ex) {
            ex.printStackTrace();
        }
    }

    public void sendStartTransactionRequest() throws Exception {
        try {
            Request request = core.createStartTransactionRequest(41, "some id", 42, ZonedDateTime.now());
            send(request);
        } catch (PropertyConstraintException ex) {
            ex.printStackTrace();
        }
    }

    public void sendStopTransactionRequest() throws Exception {
        StopTransactionRequest request = core.createStopTransactionRequest(42, ZonedDateTime.now(), 42);
        send(request);
    }

    public void sendDataTransferRequest(String vendorId, String messageId, String data) {
        try {
            DataTransferRequest request = core.createDataTransferRequest(vendorId);
            request.setMessageId(messageId);
            request.setData(data);

            send(request);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void sendStatusNotificationRequest() {
        try {
            StatusNotificationRequest request =
                    core.createStatusNotificationRequest(
                            42, ChargePointErrorCode.NoError, ChargePointStatus.Available);
            send(request);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void sendDiagnosticsStatusNotificationRequest(DiagnosticsStatus status) throws Exception {
        DiagnosticsStatusNotificationRequest request = new DiagnosticsStatusNotificationRequest(status);
        send(request);
    }

    public void sendFirmwareStatusNotificationRequest(FirmwareStatus status) throws Exception {
        FirmwareStatusNotificationRequest request = new FirmwareStatusNotificationRequest(status);
        send(request);
    }

    private void send(Request request) throws Exception {
        try {
            client
                    .send(request)
                    .whenComplete(clientHandlers.generateWhenCompleteHandler());
        } catch (UnsupportedFeatureException ex) {
            ex.printStackTrace();
        }
    }

}
