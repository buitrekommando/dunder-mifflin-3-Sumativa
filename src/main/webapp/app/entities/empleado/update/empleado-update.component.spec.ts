import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse, provideHttpClient } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Subject, from, of } from 'rxjs';

import { IDepartamento } from 'app/entities/departamento/departamento.model';
import { DepartamentoService } from 'app/entities/departamento/service/departamento.service';
import { IJefe } from 'app/entities/jefe/jefe.model';
import { JefeService } from 'app/entities/jefe/service/jefe.service';
import { IEmpleado } from '../empleado.model';
import { EmpleadoService } from '../service/empleado.service';
import { EmpleadoFormService } from './empleado-form.service';

import { EmpleadoUpdateComponent } from './empleado-update.component';

describe('Empleado Management Update Component', () => {
  let comp: EmpleadoUpdateComponent;
  let fixture: ComponentFixture<EmpleadoUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let empleadoFormService: EmpleadoFormService;
  let empleadoService: EmpleadoService;
  let departamentoService: DepartamentoService;
  let jefeService: JefeService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [EmpleadoUpdateComponent],
      providers: [
        provideHttpClient(),
        FormBuilder,
        {
          provide: ActivatedRoute,
          useValue: {
            params: from([{}]),
          },
        },
      ],
    })
      .overrideTemplate(EmpleadoUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(EmpleadoUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    empleadoFormService = TestBed.inject(EmpleadoFormService);
    empleadoService = TestBed.inject(EmpleadoService);
    departamentoService = TestBed.inject(DepartamentoService);
    jefeService = TestBed.inject(JefeService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call Departamento query and add missing value', () => {
      const empleado: IEmpleado = { id: 456 };
      const departamento: IDepartamento = { id: 15363 };
      empleado.departamento = departamento;

      const departamentoCollection: IDepartamento[] = [{ id: 21553 }];
      jest.spyOn(departamentoService, 'query').mockReturnValue(of(new HttpResponse({ body: departamentoCollection })));
      const additionalDepartamentos = [departamento];
      const expectedCollection: IDepartamento[] = [...additionalDepartamentos, ...departamentoCollection];
      jest.spyOn(departamentoService, 'addDepartamentoToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ empleado });
      comp.ngOnInit();

      expect(departamentoService.query).toHaveBeenCalled();
      expect(departamentoService.addDepartamentoToCollectionIfMissing).toHaveBeenCalledWith(
        departamentoCollection,
        ...additionalDepartamentos.map(expect.objectContaining),
      );
      expect(comp.departamentosSharedCollection).toEqual(expectedCollection);
    });

    it('Should call Jefe query and add missing value', () => {
      const empleado: IEmpleado = { id: 456 };
      const jefe: IJefe = { id: 18266 };
      empleado.jefe = jefe;

      const jefeCollection: IJefe[] = [{ id: 32669 }];
      jest.spyOn(jefeService, 'query').mockReturnValue(of(new HttpResponse({ body: jefeCollection })));
      const additionalJefes = [jefe];
      const expectedCollection: IJefe[] = [...additionalJefes, ...jefeCollection];
      jest.spyOn(jefeService, 'addJefeToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ empleado });
      comp.ngOnInit();

      expect(jefeService.query).toHaveBeenCalled();
      expect(jefeService.addJefeToCollectionIfMissing).toHaveBeenCalledWith(
        jefeCollection,
        ...additionalJefes.map(expect.objectContaining),
      );
      expect(comp.jefesSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const empleado: IEmpleado = { id: 456 };
      const departamento: IDepartamento = { id: 664 };
      empleado.departamento = departamento;
      const jefe: IJefe = { id: 23588 };
      empleado.jefe = jefe;

      activatedRoute.data = of({ empleado });
      comp.ngOnInit();

      expect(comp.departamentosSharedCollection).toContain(departamento);
      expect(comp.jefesSharedCollection).toContain(jefe);
      expect(comp.empleado).toEqual(empleado);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IEmpleado>>();
      const empleado = { id: 123 };
      jest.spyOn(empleadoFormService, 'getEmpleado').mockReturnValue(empleado);
      jest.spyOn(empleadoService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ empleado });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: empleado }));
      saveSubject.complete();

      // THEN
      expect(empleadoFormService.getEmpleado).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(empleadoService.update).toHaveBeenCalledWith(expect.objectContaining(empleado));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IEmpleado>>();
      const empleado = { id: 123 };
      jest.spyOn(empleadoFormService, 'getEmpleado').mockReturnValue({ id: null });
      jest.spyOn(empleadoService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ empleado: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: empleado }));
      saveSubject.complete();

      // THEN
      expect(empleadoFormService.getEmpleado).toHaveBeenCalled();
      expect(empleadoService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IEmpleado>>();
      const empleado = { id: 123 };
      jest.spyOn(empleadoService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ empleado });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(empleadoService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareDepartamento', () => {
      it('Should forward to departamentoService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(departamentoService, 'compareDepartamento');
        comp.compareDepartamento(entity, entity2);
        expect(departamentoService.compareDepartamento).toHaveBeenCalledWith(entity, entity2);
      });
    });

    describe('compareJefe', () => {
      it('Should forward to jefeService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(jefeService, 'compareJefe');
        comp.compareJefe(entity, entity2);
        expect(jefeService.compareJefe).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
