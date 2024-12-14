import { IJefe, NewJefe } from './jefe.model';

export const sampleWithRequiredData: IJefe = {
  id: 24834,
  nombre: 'dapper unto',
  telefono: 'versus',
};

export const sampleWithPartialData: IJefe = {
  id: 23143,
  nombre: 'trench ultimately',
  telefono: 'fluctuate',
};

export const sampleWithFullData: IJefe = {
  id: 2490,
  nombre: 'pause',
  telefono: 'oddly lucky',
};

export const sampleWithNewData: NewJefe = {
  nombre: 'from as attribute',
  telefono: 'worth absentmindedly',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
