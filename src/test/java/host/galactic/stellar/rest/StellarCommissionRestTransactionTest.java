package host.galactic.stellar.rest;

import host.galactic.stellar.StellarBaseTest;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.fail;

@QuarkusTest
public class StellarCommissionRestTransactionTest extends StellarBaseTest {
    @Test
    public void testCreateTransaction() {
        fail("Implement testCreateTransaction()");
    }

    @Test
    public void testCreateTransactionButNotEnoughChannelAccounts() {
        fail("Implement testCreateTransactionButNotEnoughChannelAccounts()");
    }

    @Test
    public void testCreateTransactionInvalidVotingId() {
        fail("Implement testCreateTransactionInvalidVotingId()");
    }

    @Test
    public void testCreateTransactionButAlreadySentIt() {
        fail("Implement testCreateTransactionButAlreadySentIt()");
    }

    @Test
    public void testGetTransactionForSignature() {
        fail("Implement testGetTransactionForSignature()");
    }

    @Test
    public void testTryGettingNonExistingTransactionForSignature() {
        fail("Implement testTryGettingNonExistingTransactionForSignature()");
    }
}
