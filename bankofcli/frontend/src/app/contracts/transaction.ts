// export type TransactionType =
//   | 'deposit'
//   | 'withdraw'
//   | 'transfer';

export interface Transaction {
  transactionId: number;
  timestamp: string;
  type: string;
  amount: number;
  description?: string;
  accountId: number;
  relatedAccountId?: number;
}