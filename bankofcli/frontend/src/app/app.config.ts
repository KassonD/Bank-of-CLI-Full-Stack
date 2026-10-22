import { ApplicationConfig, provideBrowserGlobalErrorListeners } from '@angular/core';
import { provideRouter } from '@angular/router';
import { routes } from './app.routes';
import { provideHttpClient, withInterceptors } from '@angular/common/http';
import { HttpBankDataSource } from './data_source/http-bank-data-source';
import { csrfInterceptor } from './interceptors/csrfInterceptor';

export const appConfig: ApplicationConfig = {
  providers: [
    provideBrowserGlobalErrorListeners(),
    provideRouter(routes),
    provideHttpClient(withInterceptors([csrfInterceptor])),
    { provide: HttpBankDataSource, useClass: HttpBankDataSource }
  ]
};
