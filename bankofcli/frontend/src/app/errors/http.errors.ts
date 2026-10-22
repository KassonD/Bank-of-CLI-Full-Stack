import { HttpErrorResponse } from '@angular/common/http';
import { BankError } from './bank.errors';

export function httpErrorMessage(err: unknown, fallback: string): string {
  if (err instanceof BankError) {
    return err.message;
  }
  if (err instanceof HttpErrorResponse) {
    const body = err.error as { message?: string } | null;
    return body?.message ?? fallback;
  }
  return fallback;
}