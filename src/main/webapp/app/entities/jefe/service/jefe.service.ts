import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IJefe, NewJefe } from '../jefe.model';

export type PartialUpdateJefe = Partial<IJefe> & Pick<IJefe, 'id'>;

export type EntityResponseType = HttpResponse<IJefe>;
export type EntityArrayResponseType = HttpResponse<IJefe[]>;

@Injectable({ providedIn: 'root' })
export class JefeService {
  protected readonly http = inject(HttpClient);
  protected readonly applicationConfigService = inject(ApplicationConfigService);

  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/jefes');

  create(jefe: NewJefe): Observable<EntityResponseType> {
    return this.http.post<IJefe>(this.resourceUrl, jefe, { observe: 'response' });
  }

  update(jefe: IJefe): Observable<EntityResponseType> {
    return this.http.put<IJefe>(`${this.resourceUrl}/${this.getJefeIdentifier(jefe)}`, jefe, { observe: 'response' });
  }

  partialUpdate(jefe: PartialUpdateJefe): Observable<EntityResponseType> {
    return this.http.patch<IJefe>(`${this.resourceUrl}/${this.getJefeIdentifier(jefe)}`, jefe, { observe: 'response' });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IJefe>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IJefe[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getJefeIdentifier(jefe: Pick<IJefe, 'id'>): number {
    return jefe.id;
  }

  compareJefe(o1: Pick<IJefe, 'id'> | null, o2: Pick<IJefe, 'id'> | null): boolean {
    return o1 && o2 ? this.getJefeIdentifier(o1) === this.getJefeIdentifier(o2) : o1 === o2;
  }

  addJefeToCollectionIfMissing<Type extends Pick<IJefe, 'id'>>(
    jefeCollection: Type[],
    ...jefesToCheck: (Type | null | undefined)[]
  ): Type[] {
    const jefes: Type[] = jefesToCheck.filter(isPresent);
    if (jefes.length > 0) {
      const jefeCollectionIdentifiers = jefeCollection.map(jefeItem => this.getJefeIdentifier(jefeItem));
      const jefesToAdd = jefes.filter(jefeItem => {
        const jefeIdentifier = this.getJefeIdentifier(jefeItem);
        if (jefeCollectionIdentifiers.includes(jefeIdentifier)) {
          return false;
        }
        jefeCollectionIdentifiers.push(jefeIdentifier);
        return true;
      });
      return [...jefesToAdd, ...jefeCollection];
    }
    return jefeCollection;
  }
}
