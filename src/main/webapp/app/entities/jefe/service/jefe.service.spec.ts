import { TestBed } from '@angular/core/testing';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { provideHttpClient } from '@angular/common/http';

import { IJefe } from '../jefe.model';
import { sampleWithFullData, sampleWithNewData, sampleWithPartialData, sampleWithRequiredData } from '../jefe.test-samples';

import { JefeService } from './jefe.service';

const requireRestSample: IJefe = {
  ...sampleWithRequiredData,
};

describe('Jefe Service', () => {
  let service: JefeService;
  let httpMock: HttpTestingController;
  let expectedResult: IJefe | IJefe[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()],
    });
    expectedResult = null;
    service = TestBed.inject(JefeService);
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

    it('should create a Jefe', () => {
      const jefe = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(jefe).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Jefe', () => {
      const jefe = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(jefe).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Jefe', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Jefe', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a Jefe', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addJefeToCollectionIfMissing', () => {
      it('should add a Jefe to an empty array', () => {
        const jefe: IJefe = sampleWithRequiredData;
        expectedResult = service.addJefeToCollectionIfMissing([], jefe);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(jefe);
      });

      it('should not add a Jefe to an array that contains it', () => {
        const jefe: IJefe = sampleWithRequiredData;
        const jefeCollection: IJefe[] = [
          {
            ...jefe,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addJefeToCollectionIfMissing(jefeCollection, jefe);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Jefe to an array that doesn't contain it", () => {
        const jefe: IJefe = sampleWithRequiredData;
        const jefeCollection: IJefe[] = [sampleWithPartialData];
        expectedResult = service.addJefeToCollectionIfMissing(jefeCollection, jefe);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(jefe);
      });

      it('should add only unique Jefe to an array', () => {
        const jefeArray: IJefe[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const jefeCollection: IJefe[] = [sampleWithRequiredData];
        expectedResult = service.addJefeToCollectionIfMissing(jefeCollection, ...jefeArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const jefe: IJefe = sampleWithRequiredData;
        const jefe2: IJefe = sampleWithPartialData;
        expectedResult = service.addJefeToCollectionIfMissing([], jefe, jefe2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(jefe);
        expect(expectedResult).toContain(jefe2);
      });

      it('should accept null and undefined values', () => {
        const jefe: IJefe = sampleWithRequiredData;
        expectedResult = service.addJefeToCollectionIfMissing([], null, jefe, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(jefe);
      });

      it('should return initial array if no Jefe is added', () => {
        const jefeCollection: IJefe[] = [sampleWithRequiredData];
        expectedResult = service.addJefeToCollectionIfMissing(jefeCollection, undefined, null);
        expect(expectedResult).toEqual(jefeCollection);
      });
    });

    describe('compareJefe', () => {
      it('Should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareJefe(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('Should return false if one entity is null', () => {
        const entity1 = { id: 123 };
        const entity2 = null;

        const compareResult1 = service.compareJefe(entity1, entity2);
        const compareResult2 = service.compareJefe(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey differs', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 456 };

        const compareResult1 = service.compareJefe(entity1, entity2);
        const compareResult2 = service.compareJefe(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey matches', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 123 };

        const compareResult1 = service.compareJefe(entity1, entity2);
        const compareResult2 = service.compareJefe(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
