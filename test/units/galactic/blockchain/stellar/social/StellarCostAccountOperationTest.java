package units.galactic.blockchain.stellar.social;

import galactic.blockchain.api.Account;
import galactic.blockchain.stellar.social.StellarCostAccountOperation;
import org.junit.Before;
import org.junit.Test;
import org.stellar.sdk.AccountRequiresMemoException;
import org.stellar.sdk.responses.AccountResponse;
import units.galactic.blockchain.stellar.voting.StellarMock;

import java.io.IOException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

public class StellarCostAccountOperationTest {
    private StellarMock stellarMock;

    private StellarCostAccountOperation operation;

    @Before
    public void setup() throws AccountRequiresMemoException, IOException {
        stellarMock = new StellarMock();
        operation = new StellarCostAccountOperation();

        operation.init(stellarMock.configuration);
    }

    @Test
    public void testGetAccount() throws IOException {
        // Given
        when(stellarMock.server.accounts().account(anyString())).thenReturn(new AccountResponse("some-acc-id", 1L));
        when(stellarMock.configuration.getSocialCostAccountOf()).thenReturn(new Account("mock-secret", "mock-public"));

        // When
        Account costAccount = operation.getAccount();

        // Then
        assertThat(costAccount.publik, equalTo("mock-public"));
        assertThat(costAccount.secret, equalTo("mock-secret"));
    }
}
