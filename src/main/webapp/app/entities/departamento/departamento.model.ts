export interface IDepartamento {
  id: number;
  ubicacion?: string | null;
  nombre?: string | null;
  presupuesto?: number | null;
}

export type NewDepartamento = Omit<IDepartamento, 'id'> & { id: null };
