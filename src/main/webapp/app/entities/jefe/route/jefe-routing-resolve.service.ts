import { inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { EMPTY, Observable, of } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IJefe } from '../jefe.model';
import { JefeService } from '../service/jefe.service';

const jefeResolve = (route: ActivatedRouteSnapshot): Observable<null | IJefe> => {
  const id = route.params.id;
  if (id) {
    return inject(JefeService)
      .find(id)
      .pipe(
        mergeMap((jefe: HttpResponse<IJefe>) => {
          if (jefe.body) {
            return of(jefe.body);
          }
          inject(Router).navigate(['404']);
          return EMPTY;
        }),
      );
  }
  return of(null);
};

export default jefeResolve;
