import { IDepartamento } from 'app/entities/departamento/departamento.model';
import { IJefe } from 'app/entities/jefe/jefe.model';

export interface IEmpleado {
  id: number;
  nombre?: string | null;
  apellido?: string | null;
  telefono?: string | null;
  email?: string | null;
  departamento?: IDepartamento | null;
  jefe?: IJefe | null;
}

export type NewEmpleado = Omit<IEmpleado, 'id'> & { id: null };
