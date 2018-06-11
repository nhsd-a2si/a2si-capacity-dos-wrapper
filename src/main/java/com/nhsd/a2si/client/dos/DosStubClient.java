package com.nhsd.a2si.client.dos;

import https.nww_pathwaysdos_nhs_uk.app.api.webservices.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.xml.ws.Holder;

/**
 * The DOS Stub client returns predefined responses and makes no SOAP calls over HTTP
 * It is useful when no DOS System (real or mocked) can be used
 */
@Profile({
        "doswrapper-local-dos-stub-na-cpsc-stub-na",
        "doswrapper-local-dos-stub-na-cpsc-rest-local",
        "doswrapper-aws-dos-stub-na-cpsc-stub-na",
        "doswrapper-aws-dos-stub-na-cpsc-rest-aws"})
@Component
public class DosStubClient implements DosClient {

    private static final Logger logger = LoggerFactory.getLogger(DosStubClient.class);

    public DosStubClient() {
        System.out.println("Creating DOS Stub Client");
    }

    @PostConstruct
    public void logPostConstruct() {

        logger.info("DosStubClient created");

    }

    public void checkCapacitySummary(UserInfo userInfo, Case c, Holder<String> transactionId,
                                     Holder<ArrayOfServiceCareSummaryDestination> checkCapacitySummaryResult) {

        logger.debug("Getting Check Capacity Summary for UserInfo {} and Case {}", userInfo, c);

        ObjectFactory objectFactory = new ObjectFactory();

        transactionId.value = "TransId";

        ArrayOfServiceCareSummaryDestination arrayOfServiceCareSummaryDestination =
                objectFactory.createArrayOfServiceCareSummaryDestination();
        checkCapacitySummaryResult.value = arrayOfServiceCareSummaryDestination;

        ServiceCareSummaryDestination serviceCareSummaryDestination =
                objectFactory.createServiceCareSummaryDestination();

        serviceCareSummaryDestination.setId(132175);
        serviceCareSummaryDestination.setCapacity(Capacity.HIGH);
        serviceCareSummaryDestination.setName("Dental - Bradford, West Yorkshire (Olney Dental Care)");
        serviceCareSummaryDestination.setContactDetails("01274 720980");
        serviceCareSummaryDestination.setAddress("5 Lister Avenue, Bradford, BD4 7QP");
        serviceCareSummaryDestination.setPostcode("BD4 7QP");
        serviceCareSummaryDestination.setNorthings(431436);
        serviceCareSummaryDestination.setEastings(417867);
        serviceCareSummaryDestination.setUrl(null);
        ServiceDetails serviceDetail1 = objectFactory.createServiceDetails();
        serviceDetail1.setId(12);
        serviceDetail1.setName("Dental Services");
        serviceCareSummaryDestination.setServiceType(serviceDetail1);
        serviceCareSummaryDestination.setOdsCode("1323782502");
        serviceCareSummaryDestination.setDistance("0.3");
        serviceCareSummaryDestination.setNotes("Disposition Instructions: HOW IS A REFERRAL MADE TO THIS SERVICE? ");
        serviceCareSummaryDestination.setReferralText("");

        serviceCareSummaryDestination.setOpenAllHours(false);

        ServiceCareItemRotaSession serviceCareItemRotaSession = objectFactory.createServiceCareItemRotaSession();
        serviceCareItemRotaSession.setStartDayOfWeek(DayOfWeek.MONDAY);
        TimeOfDay timeOfDay = objectFactory.createTimeOfDay();
        timeOfDay.setHours((short) 9);
        timeOfDay.setMinutes((short) 0);
        serviceCareItemRotaSession.setStartTime(timeOfDay);
        serviceCareItemRotaSession.setStatus("Open");

        ArrayOfServiceCareItemRotaSession arrayOfServiceCareItemRotaSession =
                objectFactory.createArrayOfServiceCareItemRotaSession();
        arrayOfServiceCareItemRotaSession.getServiceCareItemRotaSession().add(serviceCareItemRotaSession);

        serviceCareSummaryDestination.setRotaSessions(arrayOfServiceCareItemRotaSession);

        arrayOfServiceCareSummaryDestination.getServiceCareSummaryDestination().add(serviceCareSummaryDestination);

        checkCapacitySummaryResult.value = arrayOfServiceCareSummaryDestination;

        logger.debug("Returning Check Capacity Summary Result for UserInfo {} and Case {} " +
                        "with a transaction Id of {} and a Check Capacity Result of {}",
                userInfo, c, transactionId.value, checkCapacitySummaryResult.value);


    }

    public ArrayOfHospitalScores getHospitalScores(UserInfo userInfo) {

        logger.debug("Getting Hospital Scores for UserInfo {}", userInfo);

        ObjectFactory objectFactory = new ObjectFactory();

        ArrayOfHospitalScores arrayOfHospitalScores = objectFactory.createArrayOfHospitalScores();

        Hospital hospital = objectFactory.createHospital();
        hospital.setHospitalName("Hospital One");
        hospital.setNote("*----------------Note---------------*");
        hospital.setTotalScore(99);
        hospital.setUpdateDate("2017-01-01");

        arrayOfHospitalScores.getHospital().add(hospital);

        logger.debug("Returning Hospital Scores for UserInfo {} " +
                "with value {}", userInfo, arrayOfHospitalScores);

        return arrayOfHospitalScores;
    }

    public ArrayOfServices serviceDetailsById(UserInfo userInfo, String serviceId) {

        logger.debug("Getting Service Details for UserInfo {} and Service Id {}", userInfo, serviceId);

        ObjectFactory objectFactory = new ObjectFactory();

        ArrayOfServices arrayOfServices = objectFactory.createArrayOfServices();

        if (!serviceId.equals("does-not-exist")) {

            ServiceDetail serviceDetail1 = objectFactory.createServiceDetail();
            serviceDetail1.setId("id1");
            serviceDetail1.setOdsCode("ODS-0001");

            ArrayOfContactDetails arrayOfContactDetails = objectFactory.createArrayOfContactDetails();
            serviceDetail1.setContactDetails(arrayOfContactDetails);

            ContactDetail contactDetail1 = objectFactory.createContactDetail();
            contactDetail1.setTag(ContactType.EMAIL);
            contactDetail1.setValue("me@ome.com");
            contactDetail1.setOrder(1);
            contactDetail1.setName("me");

            arrayOfContactDetails.getContactDetail().add(contactDetail1);

            serviceDetail1.setContactDetails(arrayOfContactDetails);

            arrayOfServices.getService().add(serviceDetail1);
        }

        logger.debug("Returning Service Details for UserInfo {} and Service Id {} " +
                "with value {}", userInfo, serviceId, arrayOfServices);



        return arrayOfServices;
    }

    public void serviceAnalysis(UserInfo userInfo, SACase sac, Holder<String> transactionId,
                                Holder<ArrayOfServiceAnalysisResults> serviceAnalysisResultArray) {

        // TO DO - CREATE STUB DATA
    }

    @Override
    public String toString() {
        return "DosStubClient{}";
    }
}
