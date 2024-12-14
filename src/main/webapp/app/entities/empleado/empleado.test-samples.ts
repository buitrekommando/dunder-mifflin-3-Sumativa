import { IEmpleado, NewEmpleado } from './empleado.model';

export const sampleWithRequiredData: IEmpleado = {
  id: 18907,
  nombre: 'oh whoa whereas',
  apellido: 'expensive mixed repurpose',
  telefono: 'daintily boohoo entire',
  email: 'Sonia_AnguloAguilera@gmail.com',
};

export const sampleWithPartialData: IEmpleado = {
  id: 31543,
  nombre: 'pertain',
  apellido: 'yak finally',
  telefono: 'nightlife paintwork',
  email: 'German90@gmail.com',
};

export const sampleWithFullData: IEmpleado = {
  id: 26942,
  nombre: 'ligate once',
  apellido: 'defiantly',
  telefono: 'burly naughty',
  email: 'Ariadna.PadillaMoreno56@yahoo.com',
};

export const sampleWithNewData: NewEmpleado = {
  nombre: 'bitter pace',
  apellido: 'with since',
  telefono: 'mysteriously because hoot',
  email: 'Eduardo.AlvaradoBarela@yahoo.com',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
