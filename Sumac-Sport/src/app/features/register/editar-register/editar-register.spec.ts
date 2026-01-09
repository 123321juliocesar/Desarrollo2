import { ComponentFixture, TestBed } from '@angular/core/testing';

import { EditarRegister } from './editar-register';

describe('EditarRegister', () => {
  let component: EditarRegister;
  let fixture: ComponentFixture<EditarRegister>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [EditarRegister]
    })
    .compileComponents();

    fixture = TestBed.createComponent(EditarRegister);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
