package smokes;

import clients.CommissionTestClient;
import clients.VotingTestClient;
import controllers.CommissionControllerTest;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.RuleChain;
import play.inject.guice.GuiceApplicationBuilder;
import play.mvc.Result;
import requests.CommissionAccountCreationRequest;
import requests.CommissionInitRequest;
import requests.CreateVotingRequest;
import rules.RuleChainForTests;

import java.util.Arrays;

import static asserts.DbAsserts.assertThatTransactionIsStoredFor;
import static controllers.VotingRequestMaker.createValidVotingRequest;
import static extractors.CommissionResponseFromResult.*;
import static extractors.GenericDataFromResult.statusOf;
import static matchers.ResultHasHeader.hasLocationHeader;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.isEmptyString;
import static play.mvc.Http.HeaderNames.LOCATION;
import static play.mvc.Http.Status.CREATED;
import static play.mvc.Http.Status.OK;

public class ConductVotingSmokeTest {
    @Rule
    public RuleChain chain;

    private final RuleChainForTests ruleChainForTests;

    private CommissionTestClient testClient;
    private VotingTestClient votingTestClient;

    private static final String[] supportedNetworks = new String[]{
            "stellar"
    };

    public ConductVotingSmokeTest() {
        GuiceApplicationBuilder applicationBuilder = new GuiceApplicationBuilder();

        ruleChainForTests = new RuleChainForTests(applicationBuilder);
        chain = ruleChainForTests.getRuleChain();
    }

    @Before
    public void setup() {
        testClient = new CommissionTestClient(ruleChainForTests.getApplication());
        votingTestClient = new VotingTestClient(ruleChainForTests.getApplication());
    }

    @Test
    public void testVotingOnNetworks() throws InterruptedException {
        for(String network: supportedNetworks) {
            testVotingOnNetwork(network);
        }
    }

    private void testVotingOnNetwork(String networkName) throws InterruptedException {
        // Given
        InitData votingInitData = initVotingFor("Bob", networkName);
        Thread.sleep(15 * 1000); // So that some channel accounts are present.

        String message = createMessage(votingInitData.votingId, "someAccountId");

        CommissionTestClient.SignOnEnvelopeResult result = testClient.signOnEnvelope(votingInitData.publicKey, votingInitData.sessionJwt, message);
        assertThat(statusOf(result.http), equalTo(OK));
        assertThat(envelopeSignatureOf(result.http), notNullValue());
        assertThat(envelopeSignatureOf(result.http).length(), greaterThan(0));

        String envelopeSignatureBase64 = envelopeSignatureOf(result.http);
        CommissionAccountCreationRequest accountCreationRequest = CommissionTestClient.createAccountCreationRequest(message, envelopeSignatureBase64, result.envelope);
        Result accountCreationRequestResult = testClient.requestAccountCreation(accountCreationRequest);

        assertThat(statusOf(accountCreationRequestResult), equalTo(OK));
        assertThat(accountCreationTransactionOf(accountCreationRequestResult), notNullValue());
        assertThat(accountCreationTransactionOf(accountCreationRequestResult), not(isEmptyString()));
        assertThatTransactionIsStoredFor(accountCreationRequest.getRevealedSignatureBase64());

        // When
        Result transactionOfSignatureResult = testClient.transactionOfSignature(accountCreationRequest.getRevealedSignatureBase64());

        // Then
        assertThat(statusOf(transactionOfSignatureResult), equalTo(OK));
        assertThat(transactionOfSignature(transactionOfSignatureResult), notNullValue());
    }

    private InitData initVotingFor(String userId, String network) {
        // Given
        String votingId = createValidVoting(network);
        CommissionInitRequest initRequest = new CommissionInitRequest();
        initRequest.setVotingId(votingId);

        // When
        Result result = testClient.init(initRequest, userId);

        // Then
        assertThat(statusOf(result), equalTo(OK));

        InitData initData = new InitData();
        initData.votingId = votingId;
        initData.publicKey = publicKeyOf(result);
        initData.sessionJwt = sessionJwtOf(result);

        return initData;
    }

    private String createValidVoting(String network) {
        CreateVotingRequest createVotingRequest = createValidVotingRequest();
        createVotingRequest.setAuthorization(CreateVotingRequest.Authorization.EMAILS);
        createVotingRequest.setAuthorizationEmailOptions(Arrays.asList("john@mail.com", "doe@where.de", "some@one.com"));
        createVotingRequest.setNetwork(network);

        Result result = votingTestClient.createVoting(createVotingRequest);
        assertThat(statusOf(result), equalTo(CREATED));
        assertThat(result, hasLocationHeader());

        String locationUrl = result.headers().get(LOCATION);
        String[] locationUrlParts = locationUrl.split("/");
        String votingId = locationUrlParts[locationUrlParts.length - 1];

        return votingId;
    }

    private String createMessage(String votingId, String voterPublicAccountId) {
        return votingId + "|" + voterPublicAccountId;
    }

    private static class InitData {
        public String votingId;
        public String publicKey;
        public String sessionJwt;
    }
}