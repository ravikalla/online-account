package com.synechron.onlineacc;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.runner.RunWith;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;

import com.cucumber.listener.Reporter;

import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;
//import io.specto.hoverfly.junit.core.HoverflyConfig;
//import io.specto.hoverfly.junit.rule.HoverflyRule;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit.WireMockRule;

@ActiveProfiles("test")
@RunWith(Cucumber.class)
@CucumberOptions(features = "classpath:features"
        , tags = {"@Regression"}
        , glue={"com.synechron.onlineacc.stepdef"}
		, plugin = { "pretty", "html:target/cucumber/cucumber-html-report", "json:target/cucumber/cucumber.json",
				"junit:target/cucumber/cucumber.xml",
				"com.cucumber.listener.ExtentCucumberFormatter:target/extent-report.html" })
public class BDDTest {
//	@ClassRule
//	public static HoverflyRule hoverflyRule = HoverflyRule.inCaptureOrSimulationMode("externalaudit_virtual_data.json",HoverflyConfig.configs().proxyLocalHost(true));

	@ClassRule
	public static WireMockRule wireMockRule = new WireMockRule(7001);

	@BeforeClass
	public static void init() {
		wireMockRule.resetMappings();
		wireMockRule.resetScenarios();
		wireMockRule.resetRequests();

		String responseDepositSuccess = "deposit success";
		String responseWithdrawSuccess = "withdraw success";

		List<String> amountList = Arrays.asList("0.0", "500.0", "1000.0", "5000.0", "998200.0", "1000750.0", "1000000.0", "1.0E15", "1.0000000000005E15");

		for (String amount : amountList) {
			wireMockRule.stubFor(WireMock.get(WireMock.urlMatching("/audit/deposit/" + amount))
				.willReturn(
					WireMock.aResponse()
						.withBody(responseDepositSuccess)
						.withStatus(HttpStatus.OK.value())
						.withHeader("Content-Type", "application/json;charset=UTF-8")
				)
			);
		}
		for (String amount : amountList) {
			wireMockRule.stubFor(WireMock.get(WireMock.urlMatching("/audit/withdraw/" + amount))
					.willReturn(
						WireMock.aResponse()
							.withBody(responseWithdrawSuccess)
							.withStatus(HttpStatus.OK.value())
							.withHeader("Content-Type", "application/json;charset=UTF-8")
					)
				);
		}
	}

	@AfterClass
    public static void teardown() {
        Reporter.loadXMLConfig(new File("src/test/resources/extent-config.xml"));
        Reporter.setSystemInfo("user", System.getProperty("user.name"));
        Reporter.setSystemInfo("os", "Mac OSX");
        Reporter.setTestRunnerOutput("Sample test runner output message");
    }
}