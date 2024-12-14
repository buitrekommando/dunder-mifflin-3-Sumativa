import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IDepartamento, NewDepartamento } from '../departamento.model';

export type PartialUpdateDepartamento = Partial<IDepartamento> & Pick<IDepartamento, 'id'>;

export type EntityResponseType = HttpResponse<IDepartamento>;
export type EntityArrayResponseType = HttpResponse<IDepartamento[]>;

@Injectable({ providedIn: 'root' })
export class DepartamentoService {
  protected readonly http = inject(HttpClient);
  protected readonly applicationConfigService = inject(ApplicationConfigService);

  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/departamentos');

  create(departamento: NewDepartamento): Observable<EntityResponseType> {
    return this.http.post<IDepartamento>(this.resourceUrl, departamento, { observe: 'response' });
  }

  update(departamento: IDepartamento): Observable<EntityResponseType> {
    return this.http.put<IDepartamento>(`${this.resourceUrl}/${this.getDepartamentoIdentifier(departamento)}`, departamento, {
      observe: 'response',
    });
  }

  partialUpdate(departamento: PartialUpdateDepartamento): Observable<EntityResponseType> {
    return this.http.patch<IDepartamento>(`${this.resourceUrl}/${this.getDepartamentoIdentifier(departamento)}`, departamento, {
      observe: 'response',
    });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IDepartamento>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IDepartamento[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getDepartamentoIdentifier(departamento: Pick<IDepartamento, 'id'>): number {
    return departamento.id;
  }

  compareDepartamento(o1: Pick<IDepartamento, 'id'> | null, o2: Pick<IDepartamento, 'id'> | null): boolean {
    return o1 && o2 ? this.getDepartamentoIdentifier(o1) === this.getDepartamentoIdentifier(o2) : o1 === o2;
  }

  addDepartamentoToCollectionIfMissing<Type extends Pick<IDepartamento, 'id'>>(
    departamentoCollection: Type[],
    ...departamentosToCheck: (Type | null | undefined)[]
  ): Type[] {
    const departamentos: Type[] = departamentosToCheck.filter(isPresent);
    if (departamentos.length > 0) {
      const departamentoCollectionIdentifiers = departamentoCollection.map(departamentoItem =>
        this.getDepartamentoIdentifier(departamentoItem),
      );
      const departamentosToAdd = departamentos.filter(departamentoItem => {
        const departamentoIdentifier = this.getDepartamentoIdentifier(departamentoItem);
        if (departamentoCollectionIdentifiers.includes(departamentoIdentifier)) {
          return false;
        }
        departamentoCollectionIdentifiers.push(departamentoIdentifier);
        return true;
      });
      return [...departamentosToAdd, ...departamentoCollection];
    }
    return departamentoCollection;
  }
}
