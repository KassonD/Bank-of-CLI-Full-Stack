export type UserErrorCode =
  | 'ACCOUNT_EXISTS'
  | 'ACTIVE_DOES_NOT_EXIST';

export class UserError extends Error {
  constructor(public readonly code: UserErrorCode, message: string) {
    super(message);
    this.name = 'UserError';
  }
}