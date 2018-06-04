package com.nhsd.a2si.client.dos;

import com.nhsd.a2si.endpoint.doswrapper.DosWrapperSoapEndpoint;
import https.nww_pathwaysdos_nhs_uk.app.api.webservices.*;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import javax.xml.ws.Holder;

import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Created by Mike.Lythgoe on 27/06/2017.
 */
/*
  This Test requires a Wiremock instance running on localhost on port 7010
  (i.e. http://127.0.0.1:7010/app/api/webservices will be the full URL used)
  Because of this external dependency, this class should not be run automatically as
  part of build processes, hence the use of te @Ignore annotation.
 */
@Ignore
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@Configuration
@ActiveProfiles("doswrapper-local-dos-soap-local-wiremock-cpsc-stub-na")
public class DosClientToWireMockIntegrationTests {

    @Autowired
    private DosWrapperSoapEndpoint dosWrapperSoapEndpoint;

    private UserInfo userInfo;

    @Before
    public void createUserInfo() {

        ObjectFactory objectFactory = new ObjectFactory();
        userInfo = objectFactory.createUserInfo();
        userInfo.setUsername("dosStubClientUsername");
        userInfo.setPassword("dosStubClientPassword");

    }

    @Test
    public void testCheckCapacitySummary() {

        Case c = new ObjectFactory().createCase();

        Holder<String> transactionId = new Holder<>();
        Holder<ArrayOfServiceCareSummaryDestination> checkCapacitySummaryResult = new Holder<>();

        dosWrapperSoapEndpoint.checkCapacitySummary(userInfo, c, transactionId, checkCapacitySummaryResult);

        assertEquals(checkCapacitySummaryResult.value.getServiceCareSummaryDestination().get(0).getId(),1323782502);
    }

    @Test
    public void testHospitalName() {
        List<Hospital> hospitalList = dosWrapperSoapEndpoint.getHospitalScores(userInfo).getHospital();

        assertEquals(dosWrapperSoapEndpoint.getHospitalScores(userInfo).getHospital().get(0).getHospitalName(),"Hospital One");
    }

    @Test
    public void testexistingServiceIdReturnsServiceWithValidODSDCode() {

        assertEquals(dosWrapperSoapEndpoint.serviceDetailsById(userInfo, "12345").getService().get(0).getOdsCode(), "ODS-0001");
    }

    // This test doesn't work if we're using a stub that returns the data regardless
    @Test
    public void testNonexistentServiceIdReturnsNull() {

        assertEquals(0, dosWrapperSoapEndpoint.serviceDetailsById(userInfo, "does-not-exist").getService().size());

    }
}
