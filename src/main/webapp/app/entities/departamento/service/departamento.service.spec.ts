import { TestBed } from '@angular/core/testing';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { provideHttpClient } from '@angular/common/http';

import { IDepartamento } from '../departamento.model';
import { sampleWithFullData, sampleWithNewData, sampleWithPartialData, sampleWithRequiredData } from '../departamento.test-samples';

import { DepartamentoService } from './departamento.service';

const requireRestSample: IDepartamento = {
  ...sampleWithRequiredData,
};

describe('Departamento Service', () => {
  let service: DepartamentoService;
  let httpMock: HttpTestingController;
  let expectedResult: IDepartamento | IDepartamento[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()],
    });
    expectedResult = null;
    service = TestBed.inject(DepartamentoService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  describe('Service methods', () => {
    it('should find an element', () => {
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.find(123).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should create a Departamento', () => {
      const departamento = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(departamento).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Departamento', () => {
      const departamento = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(departamento).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Departamento', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Departamento', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a Departamento', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addDepartamentoToCollectionIfMissing', () => {
      it('should add a Departamento to an empty array', () => {
        const departamento: IDepartamento = sampleWithRequiredData;
        expectedResult = service.addDepartamentoToCollectionIfMissing([], departamento);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(departamento);
      });

      it('should not add a Departamento to an array that contains it', () => {
        const departamento: IDepartamento = sampleWithRequiredData;
        const departamentoCollection: IDepartamento[] = [
          {
            ...departamento,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addDepartamentoToCollectionIfMissing(departamentoCollection, departamento);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Departamento to an array that doesn't contain it", () => {
        const departamento: IDepartamento = sampleWithRequiredData;
        const departamentoCollection: IDepartamento[] = [sampleWithPartialData];
        expectedResult = service.addDepartamentoToCollectionIfMissing(departamentoCollection, departamento);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(departamento);
      });

      it('should add only unique Departamento to an array', () => {
        const departamentoArray: IDepartamento[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const departamentoCollection: IDepartamento[] = [sampleWithRequiredData];
        expectedResult = service.addDepartamentoToCollectionIfMissing(departamentoCollection, ...departamentoArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const departamento: IDepartamento = sampleWithRequiredData;
        const departamento2: IDepartamento = sampleWithPartialData;
        expectedResult = service.addDepartamentoToCollectionIfMissing([], departamento, departamento2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(departamento);
        expect(expectedResult).toContain(departamento2);
      });

      it('should accept null and undefined values', () => {
        const departamento: IDepartamento = sampleWithRequiredData;
        expectedResult = service.addDepartamentoToCollectionIfMissing([], null, departamento, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(departamento);
      });

      it('should return initial array if no Departamento is added', () => {
        const departamentoCollection: IDepartamento[] = [sampleWithRequiredData];
        expectedResult = service.addDepartamentoToCollectionIfMissing(departamentoCollection, undefined, null);
        expect(expectedResult).toEqual(departamentoCollection);
      });
    });

    describe('compareDepartamento', () => {
      it('Should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareDepartamento(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('Should return false if one entity is null', () => {
        const entity1 = { id: 123 };
        const entity2 = null;

        const compareResult1 = service.compareDepartamento(entity1, entity2);
        const compareResult2 = service.compareDepartamento(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey differs', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 456 };

        const compareResult1 = service.compareDepartamento(entity1, entity2);
        const compareResult2 = service.compareDepartamento(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey matches', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 123 };

        const compareResult1 = service.compareDepartamento(entity1, entity2);
        const compareResult2 = service.compareDepartamento(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
