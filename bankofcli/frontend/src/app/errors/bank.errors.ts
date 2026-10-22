export type BankErrorCode =
  | 'INVALID_AMOUNT'
  | 'INSUFFICIENT_FUNDS'
  | 'ACCOUNT_NOT_FOUND'
  | 'SAME_ACCOUNT'
  | 'NO_ACTIVE_ACCOUNT';

export class BankError extends Error {
  constructor(public readonly code: BankErrorCode, message: string) {
    super(message);
    this.name = 'BankError';
  }
}

export function assertValidAmount(amount: number): void {
  if (typeof amount !== 'number' || !Number.isFinite(amount) || amount <= 0) {
    throw new BankError('INVALID_AMOUNT', 'Amount must be a number greater than 0.');
  }
  const cents = amount * 100;
  if (Math.abs(cents - Math.round(cents)) > 1e-9) {
    throw new BankError('INVALID_AMOUNT', 'Amount cannot have more than 2 decimal places.');
  }
}

export function assertSufficientFunds(balance: number, amount: number): void {
  if (amount > balance) {
    throw new BankError('INSUFFICIENT_FUNDS', 'Insufficient funds for this transaction.');
  }
}
