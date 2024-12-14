import { IDepartamento, NewDepartamento } from './departamento.model';

export const sampleWithRequiredData: IDepartamento = {
  id: 3406,
  ubicacion: 'glorious insidious',
  nombre: 'ghost',
  presupuesto: 19249,
};

export const sampleWithPartialData: IDepartamento = {
  id: 8976,
  ubicacion: 'as whoa',
  nombre: 'perfumed puff',
  presupuesto: 459,
};

export const sampleWithFullData: IDepartamento = {
  id: 15718,
  ubicacion: 'solidly porter ouch',
  nombre: 'yet',
  presupuesto: 31183,
};

export const sampleWithNewData: NewDepartamento = {
  ubicacion: 'barring dissemble',
  nombre: 'phrase belabor essay',
  presupuesto: 25730,
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
