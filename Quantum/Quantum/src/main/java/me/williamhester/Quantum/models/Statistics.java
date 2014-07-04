package me.williamhester.Quantum.models;

import java.util.List;

import me.williamhester.Quantum.models.Date;
import me.williamhester.Quantum.models.Transaction;

/**
 * Created by William Hester on 7/6/13.
 */
public class Statistics {

    private List<Transaction> mTransactions;

    public Statistics(List<Transaction> transactions) {
        mTransactions = transactions;
    }

    public double getAverageSpentPerDay() {
        return (double)getTotalMoneySpent()
                / getDaysSince(mTransactions.get(mTransactions.size() - 1)) / 100;
    }

    public double getAverageSpentPerWeek() {
        return (double) getTotalMoneySpent()
                / getWeeksSince(mTransactions.get(mTransactions.size() - 1)) / 100;
    }

    private int getDaysSince(Transaction first) {
        int days = 1;
        Date today = Date.today();
        int currentYear = today.getYear();

        if (currentYear == first.getYear()) {
            days = today.getDayOfYear() - first.getDayOfYear();
            return days;
        } else {
            days += first.getYear() % 4 == 0 ? 366 : 365 - first.getDayOfYear();
            days += today.getDayOfYear();
            currentYear--;
            while (currentYear > first.getYear()) {
                days += currentYear % 4 == 0 ? 366 : 365;
                currentYear--;
            }
            return days;
        }
    }

    private int getWeeksSince(Transaction first) {
        return getDaysSince(first) / 7 + getDaysSince(first) % 7 == 0 ? 0 : 1;
    }

    private int getTotalMoneySpent() {
        int dollars = 0;
        for (Transaction t : mTransactions) {
            dollars += t.getValue();
        }
        return dollars;
    }

}
