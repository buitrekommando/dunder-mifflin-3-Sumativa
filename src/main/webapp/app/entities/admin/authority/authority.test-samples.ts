import { IAuthority, NewAuthority } from './authority.model';

export const sampleWithRequiredData: IAuthority = {
  name: 'd68f45da-b3f3-488e-b765-75347f4545fc',
};

export const sampleWithPartialData: IAuthority = {
  name: '55035e40-ce7b-42e3-aa33-6da5509b3b98',
};

export const sampleWithFullData: IAuthority = {
  name: 'af2968f5-47a5-4de1-9c2f-4b3f72185ef5',
};

export const sampleWithNewData: NewAuthority = {
  name: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
