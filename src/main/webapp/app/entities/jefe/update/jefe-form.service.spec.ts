import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../jefe.test-samples';

import { JefeFormService } from './jefe-form.service';

describe('Jefe Form Service', () => {
  let service: JefeFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(JefeFormService);
  });

  describe('Service methods', () => {
    describe('createJefeFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createJefeFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            nombre: expect.any(Object),
            telefono: expect.any(Object),
          }),
        );
      });

      it('passing IJefe should create a new form with FormGroup', () => {
        const formGroup = service.createJefeFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            nombre: expect.any(Object),
            telefono: expect.any(Object),
          }),
        );
      });
    });

    describe('getJefe', () => {
      it('should return NewJefe for default Jefe initial value', () => {
        const formGroup = service.createJefeFormGroup(sampleWithNewData);

        const jefe = service.getJefe(formGroup) as any;

        expect(jefe).toMatchObject(sampleWithNewData);
      });

      it('should return NewJefe for empty Jefe initial value', () => {
        const formGroup = service.createJefeFormGroup();

        const jefe = service.getJefe(formGroup) as any;

        expect(jefe).toMatchObject({});
      });

      it('should return IJefe', () => {
        const formGroup = service.createJefeFormGroup(sampleWithRequiredData);

        const jefe = service.getJefe(formGroup) as any;

        expect(jefe).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IJefe should not enable id FormControl', () => {
        const formGroup = service.createJefeFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewJefe should disable id FormControl', () => {
        const formGroup = service.createJefeFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
