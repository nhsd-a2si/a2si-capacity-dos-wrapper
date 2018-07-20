package com.nhsd.a2si.endpoint.doswrapper;

import com.nhsd.a2si.capacityinformation.domain.CapacityInformation;
import com.nhsd.a2si.client.dos.DosClient;
import com.nhsd.a2si.capacityserviceclient.CapacityServiceClient;
import https.nww_pathwaysdos_nhs_uk.app.api.webservices.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;

import javax.annotation.PostConstruct;
import javax.xml.ws.Holder;

/*
 * Created by Mike.Lythgoe on 26/06/2017.
 */

/**
 * The Dos Wrapper SOAP endpoint is the class that actually handles the request (after SOAP unmarshalling)
 * and handles the request, generally by calling the DOS system, and possibly by also calling the
 * Capacity Service
 */
@Component
public class DosWrapperSoapEndpoint extends PathWayServiceSoap12Impl {

    private static final Logger logger = LoggerFactory.getLogger(DosWrapperSoapEndpoint.class);

    private DosClient dosClient;

    private CapacityServiceClient capacityServiceClient;

    @Autowired
    public DosWrapperSoapEndpoint(DosClient dosClient, CapacityServiceClient capacityServiceClient) {
        this.dosClient = dosClient;
        this.capacityServiceClient = capacityServiceClient;
    }

    @PostConstruct
    public void logPostConstruct() {

        logger.info("dosClient is {}", dosClient);
        logger.info("capacityServiceClient is {}", capacityServiceClient);

    }

    @Override
    public void serviceAnalysis(UserInfo userInfo, SACase sac, Holder<String> transactionId,
                                Holder<ArrayOfServiceAnalysisResults> serviceAnalysisResultArray) {

        dosClient.serviceAnalysis(userInfo, sac, transactionId, serviceAnalysisResultArray);

    }

    @Override
    public ArrayOfHospitalScores getHospitalScores(UserInfo userInfo) {

        return dosClient.getHospitalScores(userInfo);
    }

    @Override
    public void checkCapacitySummary(UserInfo userInfo, Case c, Holder<String> transactionId,
                                     Holder<ArrayOfServiceCareSummaryDestination> checkCapacitySummaryResult) {

        Holder<ArrayOfServiceCareSummaryDestination> newCheckCapacitySummaryResult = new Holder<>();

        // call the DOS Check Capacity Summary method
        dosClient.checkCapacitySummary(userInfo, c, transactionId, newCheckCapacitySummaryResult);

        // For each Summary Care Destination in the result,
        // make a call to the capacity service to get the capacity information
        // using the case reference as the key (this is in the request)
        // Add the Capacity Information to the referral text field of each Service Care Summary Destination object

        boolean capacityServiceResponsive = true;

        int countOfResponses = 0;
        int countOfNonNullResponses = 0;
        int countOfServices = newCheckCapacitySummaryResult.value.getServiceCareSummaryDestination().size();
        for (ServiceCareSummaryDestination serviceCareSummaryDestination :
                newCheckCapacitySummaryResult.value.getServiceCareSummaryDestination()) {

            if (capacityServiceResponsive) {
                try {

                    logger.debug("Getting Capacity Information for Service Id: {}",
                            serviceCareSummaryDestination.getId());

                    CapacityInformation capacityInformation =
                            capacityServiceClient.getCapacityInformation(
                                    String.valueOf(serviceCareSummaryDestination.getId()));

                    logger.debug("Got Capacity Information for Service Id: {} with value of: {}",
                            serviceCareSummaryDestination.getId(), capacityInformation);

                    countOfResponses++;
                    if (capacityInformation != null) {
                    		String sMessage = capacityInformation.getMessage();
                    		if (sMessage != null && sMessage.length() > 0) {
	                    		countOfNonNullResponses++;
	                    		serviceCareSummaryDestination.setNotes(sMessage + ".\n\n" + serviceCareSummaryDestination.getNotes());
                    		}
                    }

                } catch(ResourceAccessException resourceAccessException) {
                    capacityServiceResponsive = false;
                    logger.error("Unable to get response from Capacity Service - possible timeout");
                }
                 catch (Exception e) {
                    capacityServiceResponsive = false;
                    logger.error("Unable to get response from Capacity Service");
                }
            }
        }

        if (capacityServiceResponsive) {
        		logger.info("DOS returned {} services, of which {} had waiting times (TransactionId={}, CaseRef={}, CaseID={})", countOfServices, countOfNonNullResponses, transactionId.value, c.getCaseRef(), c.getCaseId());
        } else {
    			logger.info("DOS returned {} services, of which {} returned waiting times, however the capacity service became unresponsive and only {} were checked for waiting times (TransactionId={}, CaseRef={}, CaseID={})", countOfServices, countOfNonNullResponses, countOfResponses, transactionId.value, c.getCaseRef(), c.getCaseId());
        }
        
        checkCapacitySummaryResult.value = newCheckCapacitySummaryResult.value;

    }

    @Override
    public ArrayOfServices serviceDetailsById(UserInfo userInfo, String serviceId) {

        ArrayOfServices arrayOfServices = dosClient.serviceDetailsById(userInfo, serviceId);
        return arrayOfServices;
    }

    @Override
    public String toString() {
        return "DosWrapperSoapEndpoint{" +
                "dosClient=" + dosClient +
                ", capacityServiceClient=" + capacityServiceClient +
                '}';
    }
}
