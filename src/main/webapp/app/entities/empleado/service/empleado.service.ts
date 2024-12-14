import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IEmpleado, NewEmpleado } from '../empleado.model';

export type PartialUpdateEmpleado = Partial<IEmpleado> & Pick<IEmpleado, 'id'>;

export type EntityResponseType = HttpResponse<IEmpleado>;
export type EntityArrayResponseType = HttpResponse<IEmpleado[]>;

@Injectable({ providedIn: 'root' })
export class EmpleadoService {
  protected readonly http = inject(HttpClient);
  protected readonly applicationConfigService = inject(ApplicationConfigService);

  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/empleados');

  create(empleado: NewEmpleado): Observable<EntityResponseType> {
    return this.http.post<IEmpleado>(this.resourceUrl, empleado, { observe: 'response' });
  }

  update(empleado: IEmpleado): Observable<EntityResponseType> {
    return this.http.put<IEmpleado>(`${this.resourceUrl}/${this.getEmpleadoIdentifier(empleado)}`, empleado, { observe: 'response' });
  }

  partialUpdate(empleado: PartialUpdateEmpleado): Observable<EntityResponseType> {
    return this.http.patch<IEmpleado>(`${this.resourceUrl}/${this.getEmpleadoIdentifier(empleado)}`, empleado, { observe: 'response' });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IEmpleado>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IEmpleado[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getEmpleadoIdentifier(empleado: Pick<IEmpleado, 'id'>): number {
    return empleado.id;
  }

  compareEmpleado(o1: Pick<IEmpleado, 'id'> | null, o2: Pick<IEmpleado, 'id'> | null): boolean {
    return o1 && o2 ? this.getEmpleadoIdentifier(o1) === this.getEmpleadoIdentifier(o2) : o1 === o2;
  }

  addEmpleadoToCollectionIfMissing<Type extends Pick<IEmpleado, 'id'>>(
    empleadoCollection: Type[],
    ...empleadosToCheck: (Type | null | undefined)[]
  ): Type[] {
    const empleados: Type[] = empleadosToCheck.filter(isPresent);
    if (empleados.length > 0) {
      const empleadoCollectionIdentifiers = empleadoCollection.map(empleadoItem => this.getEmpleadoIdentifier(empleadoItem));
      const empleadosToAdd = empleados.filter(empleadoItem => {
        const empleadoIdentifier = this.getEmpleadoIdentifier(empleadoItem);
        if (empleadoCollectionIdentifiers.includes(empleadoIdentifier)) {
          return false;
        }
        empleadoCollectionIdentifiers.push(empleadoIdentifier);
        return true;
      });
      return [...empleadosToAdd, ...empleadoCollection];
    }
    return empleadoCollection;
  }
}
