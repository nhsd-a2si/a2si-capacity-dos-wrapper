package com.nhsd.a2si.client.dos;

import https.nww_pathwaysdos_nhs_uk.app.api.webservices.*;

import javax.xml.ws.Holder;

/**
 * The DOS client is what the DOS Wrapper uses to call the real DOS System
 * An interface is defined because we have defined a real DOS Client (making SOAP calls to the DOS System) and
 * a DOS Stub client that returns predefined responses.
 */
public interface DosClient extends PathWayServiceSoap {

    void checkCapacitySummary(UserInfo userInfo, Case c, Holder<String> transactionId,
                              Holder<ArrayOfServiceCareSummaryDestination> checkCapacitySummaryResult);

    ArrayOfHospitalScores getHospitalScores(UserInfo userInfo);

    ArrayOfServices serviceDetailsById(UserInfo userInfo, String serviceId);

    void serviceAnalysis(UserInfo userInfo, SACase sac, Holder<String> transactionId,
                         Holder<ArrayOfServiceAnalysisResults> serviceAnalysisResultArray);

}
