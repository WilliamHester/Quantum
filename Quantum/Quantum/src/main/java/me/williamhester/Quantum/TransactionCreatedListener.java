package me.williamhester.Quantum;

import me.williamhester.Quantum.models.Transaction;

/**
 * Created by William on 3/18/14.
 */
public interface TransactionCreatedListener {

    public static final int TRANSACTION_VALUE = 0;
    public static final int TRANSACTION_LOCATION = 1;
    public static final int TRANSACTION_MEMO = 2;

    public void onCreateTransaction(Transaction transaction);
    public void onDeleteTransaction(Transaction transaction);
    public void onEditTransaction(Transaction transaction, int changed, Object changedObject);

}
