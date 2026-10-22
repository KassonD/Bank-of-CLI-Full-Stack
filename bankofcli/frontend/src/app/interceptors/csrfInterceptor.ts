import { HttpInterceptorFn, HttpResponse } from '@angular/common/http';
import { inject } from '@angular/core';
import { tap } from 'rxjs';
import { CsrfToken } from '../service/csrf-token';

// On /api calls, sends cookies and the stored CSRF token. The token request response is saved for the next call.
export const csrfInterceptor: HttpInterceptorFn = (request, next) => {

  let apiUrl: string = '/api';

  if (
    request.url !== apiUrl && !request.url.startsWith(`${apiUrl}/`)
  ) {
    return next(request);
  }

  const csrfToken = inject(CsrfToken);
  const token = request.url === `${apiUrl}/auth/csrf` ? null : csrfToken.getCurrent();
  let apiRequest = request.clone({ withCredentials: true });

  if (token) {
    apiRequest = apiRequest.clone({
      setHeaders: { [token.headerName]: token.value },
    });
  }

  return next(apiRequest).pipe(
    tap((event) => {
      if (request.method === 'GET' && request.url === `${apiUrl}/auth/csrf` &&
        event instanceof HttpResponse && !csrfToken.store(event.body)
      ) {
        throw new Error('The CSRF endpoint did not return a token.');
      }
    })
  );
};