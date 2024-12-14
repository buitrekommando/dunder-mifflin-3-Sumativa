import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse, provideHttpClient } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Subject, from, of } from 'rxjs';

import { JefeService } from '../service/jefe.service';
import { IJefe } from '../jefe.model';
import { JefeFormService } from './jefe-form.service';

import { JefeUpdateComponent } from './jefe-update.component';

describe('Jefe Management Update Component', () => {
  let comp: JefeUpdateComponent;
  let fixture: ComponentFixture<JefeUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let jefeFormService: JefeFormService;
  let jefeService: JefeService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [JefeUpdateComponent],
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
      .overrideTemplate(JefeUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(JefeUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    jefeFormService = TestBed.inject(JefeFormService);
    jefeService = TestBed.inject(JefeService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should update editForm', () => {
      const jefe: IJefe = { id: 456 };

      activatedRoute.data = of({ jefe });
      comp.ngOnInit();

      expect(comp.jefe).toEqual(jefe);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IJefe>>();
      const jefe = { id: 123 };
      jest.spyOn(jefeFormService, 'getJefe').mockReturnValue(jefe);
      jest.spyOn(jefeService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ jefe });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: jefe }));
      saveSubject.complete();

      // THEN
      expect(jefeFormService.getJefe).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(jefeService.update).toHaveBeenCalledWith(expect.objectContaining(jefe));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IJefe>>();
      const jefe = { id: 123 };
      jest.spyOn(jefeFormService, 'getJefe').mockReturnValue({ id: null });
      jest.spyOn(jefeService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ jefe: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: jefe }));
      saveSubject.complete();

      // THEN
      expect(jefeFormService.getJefe).toHaveBeenCalled();
      expect(jefeService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IJefe>>();
      const jefe = { id: 123 };
      jest.spyOn(jefeService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ jefe });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(jefeService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });
});
