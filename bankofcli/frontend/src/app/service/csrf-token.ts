import { Injectable } from '@angular/core';

export interface CsrfTokenValue {
  value: string;
  headerName: string;
}

//Service responsible for storage and retrieval of the CSRF on the front-end
@Injectable({ providedIn: 'root' })
export class CsrfToken {
  //Union type holds CSRF Token or NULL (Defaults NULL)
  private token: CsrfTokenValue | null = null;

  //Retrieve current CSRF Token
  getCurrent(): CsrfTokenValue | null {
    return this.token;
  }

  //Store to CSRFTokenValue (Returns true is successfully stored new CSRF)
  store(response: unknown): boolean {
    //Ignore invalid response type
    if (typeof response !== 'object' || response === null) {
      return false;
    }
    //Ignore blank token value
    const body = response as { token: string; headerName: string };
    if (body?.token?.length === 0) {
      return false;
    }
    //Store token
    this.token = {
      value: body.token,
      headerName: body.headerName,
    };
    return true;
  }

  clear(): void {
    this.token = null;
  }
}