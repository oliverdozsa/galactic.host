package galactic.blockchain.api.voting;

import galactic.blockchain.api.Account;
import galactic.blockchain.api.BlockchainOperation;

import java.util.List;

public interface RefundBalancesOperation extends BlockchainOperation {
    int maxNumOfAccountsToRefundInOneTransaction();
    void refundBalancesWithPayment(Account destination, List<Account> accountsToRefund);
    void refundBalancesWithTermination(Account destination, List<Account> accountsToTerminate);
}
