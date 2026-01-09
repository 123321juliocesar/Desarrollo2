import { ComponentFixture, TestBed } from '@angular/core/testing';

import { FooterOuter } from './footer-outer';

describe('FooterOuter', () => {
  let component: FooterOuter;
  let fixture: ComponentFixture<FooterOuter>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [FooterOuter]
    })
    .compileComponents();

    fixture = TestBed.createComponent(FooterOuter);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
