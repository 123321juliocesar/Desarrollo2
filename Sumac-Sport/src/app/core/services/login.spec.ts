import { TestBed } from '@angular/core/testing';

import { loginService } from './loginService';

describe('Login', () => {
  let service: loginService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(loginService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
