import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../departamento.test-samples';

import { DepartamentoFormService } from './departamento-form.service';

describe('Departamento Form Service', () => {
  let service: DepartamentoFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(DepartamentoFormService);
  });

  describe('Service methods', () => {
    describe('createDepartamentoFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createDepartamentoFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            ubicacion: expect.any(Object),
            nombre: expect.any(Object),
            presupuesto: expect.any(Object),
          }),
        );
      });

      it('passing IDepartamento should create a new form with FormGroup', () => {
        const formGroup = service.createDepartamentoFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            ubicacion: expect.any(Object),
            nombre: expect.any(Object),
            presupuesto: expect.any(Object),
          }),
        );
      });
    });

    describe('getDepartamento', () => {
      it('should return NewDepartamento for default Departamento initial value', () => {
        const formGroup = service.createDepartamentoFormGroup(sampleWithNewData);

        const departamento = service.getDepartamento(formGroup) as any;

        expect(departamento).toMatchObject(sampleWithNewData);
      });

      it('should return NewDepartamento for empty Departamento initial value', () => {
        const formGroup = service.createDepartamentoFormGroup();

        const departamento = service.getDepartamento(formGroup) as any;

        expect(departamento).toMatchObject({});
      });

      it('should return IDepartamento', () => {
        const formGroup = service.createDepartamentoFormGroup(sampleWithRequiredData);

        const departamento = service.getDepartamento(formGroup) as any;

        expect(departamento).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IDepartamento should not enable id FormControl', () => {
        const formGroup = service.createDepartamentoFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewDepartamento should disable id FormControl', () => {
        const formGroup = service.createDepartamentoFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
