package com.nhsd.a2si.client.dos;

import https.nww_pathwaysdos_nhs_uk.app.api.webservices.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.xml.ws.Holder;


/**
 * The DOS Client is used to call the real DOS SOAP API from within the DOS Wrapper code
 */


@Profile({
        "doswrapper-local-dos-soap-local-wiremock-cpsc-stub-na",
        "doswrapper-local-dos-soap-local-wiremock-cpsc-rest-local",
        "doswrapper-local-dos-soap-uat-cpsc-stub-na",
        "doswrapper-local-dos-soap-uat-cpsc-rest-local",
        "doswrapper-local-dos-soap-prod-cpsc-stub-na",
        "doswrapper-local-dos-soap-prod-cpsc-rest-local",
        "doswrapper-aws-dos-soap-uat-cpsc-stub-na",
        "doswrapper-aws-dos-soap-uat-cpsc-rest-aws",
        "doswrapper-aws-dos-soap-aws-wiremock-cpsc-stub-na",
        "doswrapper-aws-dos-soap-aws-wiremock-cpsc-rest-aws",
        "doswrapper-aws-dos-soap-prod-cpsc-rest-aws"})
@Component
public class DosSoapClient implements DosClient {

    DosSoapClient(PathWayServiceSoap pathWayServiceSoap) {
        this.pathWayServiceSoap = pathWayServiceSoap;
    }

    private static final Logger logger = LoggerFactory.getLogger(DosSoapClient.class);

    private PathWayServiceSoap pathWayServiceSoap;

    @PostConstruct
    public void logPostConstruct() {

        logger.info("DosSoapClient pathWayServiceSoap is {}", pathWayServiceSoap);

    }

    @Override
    public void checkCapacitySummary(UserInfo userInfo, Case c, Holder<String> transactionId,
                                     Holder<ArrayOfServiceCareSummaryDestination> checkCapacitySummaryResult) {

        logger.debug("Getting Check Capacity Summary for UserInfo {} and Case {}", userInfo, c);

        try {
            pathWayServiceSoap.checkCapacitySummary(userInfo, c, transactionId, checkCapacitySummaryResult);

            logger.debug("Returning Check Capacity Summary Result for UserInfo {} and Case {} " +
                            "with a transaction Id of {} and a Check Capacity Result of {}",
                    userInfo, c, transactionId.value, checkCapacitySummaryResult.value);

        } catch (Exception e) {
            logger.error("Exception thrown getting Check Capacity Summary, exception is: {}", e);
            throw e;

        }
    }

    @Override
    public ArrayOfHospitalScores getHospitalScores(UserInfo userInfo) {

        logger.debug("Getting Hospital Scores for UserInfo {}", userInfo);

        ArrayOfHospitalScores arrayOfHospitalScores;
        try {

            arrayOfHospitalScores = pathWayServiceSoap.getHospitalScores(userInfo);

            logger.debug("Returning Hospital Scores for UserInfo {} " +
                    "with value {}", userInfo, arrayOfHospitalScores);

        } catch (Exception e) {
            logger.error("Exception thrown getting Hospital Scores, exception is: {}", e);
            throw e;
        }

        System.out.println(arrayOfHospitalScores);

        return arrayOfHospitalScores;
    }

    @Override
    public ArrayOfServices serviceDetailsById(UserInfo userInfo, String serviceId) {

        logger.debug("Getting Service Details for UserInfo {} and Service Id {}", userInfo, serviceId);

        ArrayOfServices arrayOfServices;
        try {
            arrayOfServices = pathWayServiceSoap.serviceDetailsById(userInfo, serviceId);

            logger.debug("Returning Service Details for UserInfo {} and Service Id {} " +
                    "with value {}", userInfo, serviceId, arrayOfServices);

        } catch (Exception e) {
            logger.error("Exception thrown getting Service Details By Id, exception is: {}", e);
            throw e;
        }

        return arrayOfServices;
    }

    @Override
    public void serviceAnalysis(UserInfo userInfo, SACase sac, Holder<String> transactionId,
                                Holder<ArrayOfServiceAnalysisResults> serviceAnalysisResultArray) {

        logger.debug("Getting Service Analysis for UserInfo {} and Service Analysis Case {}", userInfo, sac);

        try {
            pathWayServiceSoap.serviceAnalysis(userInfo, sac, transactionId, serviceAnalysisResultArray);

            logger.debug("Returning Service Analysis Results for UserInfo {} and Service Analysis Case {} " +
                    "with a transaction Id of {} and Service Analysis Results of {}", userInfo, sac, transactionId, serviceAnalysisResultArray);

        } catch (Exception e) {
            logger.error("Exception thrown getting Service Analysis, exception is: {}", e);
            throw e;
        }
    }

    @Override
    public String toString() {
        return "DosSoapClient{" +
                "pathWayServiceSoap=" + pathWayServiceSoap +
                '}';
    }
}