import { ComponentFixture, TestBed } from '@angular/core/testing';

import { FooterInner } from './footer-inner';

describe('FooterInner', () => {
  let component: FooterInner;
  let fixture: ComponentFixture<FooterInner>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [FooterInner]
    })
    .compileComponents();

    fixture = TestBed.createComponent(FooterInner);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
