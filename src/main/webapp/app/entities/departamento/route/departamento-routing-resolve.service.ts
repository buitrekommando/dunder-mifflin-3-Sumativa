import { inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { EMPTY, Observable, of } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IDepartamento } from '../departamento.model';
import { DepartamentoService } from '../service/departamento.service';

const departamentoResolve = (route: ActivatedRouteSnapshot): Observable<null | IDepartamento> => {
  const id = route.params.id;
  if (id) {
    return inject(DepartamentoService)
      .find(id)
      .pipe(
        mergeMap((departamento: HttpResponse<IDepartamento>) => {
          if (departamento.body) {
            return of(departamento.body);
          }
          inject(Router).navigate(['404']);
          return EMPTY;
        }),
      );
  }
  return of(null);
};

export default departamentoResolve;
