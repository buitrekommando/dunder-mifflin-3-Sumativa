import { Component, OnInit, inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { IDepartamento } from 'app/entities/departamento/departamento.model';
import { DepartamentoService } from 'app/entities/departamento/service/departamento.service';
import { IJefe } from 'app/entities/jefe/jefe.model';
import { JefeService } from 'app/entities/jefe/service/jefe.service';
import { EmpleadoService } from '../service/empleado.service';
import { IEmpleado } from '../empleado.model';
import { EmpleadoFormGroup, EmpleadoFormService } from './empleado-form.service';

@Component({
  standalone: true,
  selector: 'jhi-empleado-update',
  templateUrl: './empleado-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class EmpleadoUpdateComponent implements OnInit {
  isSaving = false;
  empleado: IEmpleado | null = null;

  departamentosSharedCollection: IDepartamento[] = [];
  jefesSharedCollection: IJefe[] = [];

  protected empleadoService = inject(EmpleadoService);
  protected empleadoFormService = inject(EmpleadoFormService);
  protected departamentoService = inject(DepartamentoService);
  protected jefeService = inject(JefeService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: EmpleadoFormGroup = this.empleadoFormService.createEmpleadoFormGroup();

  compareDepartamento = (o1: IDepartamento | null, o2: IDepartamento | null): boolean =>
    this.departamentoService.compareDepartamento(o1, o2);

  compareJefe = (o1: IJefe | null, o2: IJefe | null): boolean => this.jefeService.compareJefe(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ empleado }) => {
      this.empleado = empleado;
      if (empleado) {
        this.updateForm(empleado);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const empleado = this.empleadoFormService.getEmpleado(this.editForm);
    if (empleado.id !== null) {
      this.subscribeToSaveResponse(this.empleadoService.update(empleado));
    } else {
      this.subscribeToSaveResponse(this.empleadoService.create(empleado));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IEmpleado>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe({
      next: () => this.onSaveSuccess(),
      error: () => this.onSaveError(),
    });
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving = false;
  }

  protected updateForm(empleado: IEmpleado): void {
    this.empleado = empleado;
    this.empleadoFormService.resetForm(this.editForm, empleado);

    this.departamentosSharedCollection = this.departamentoService.addDepartamentoToCollectionIfMissing<IDepartamento>(
      this.departamentosSharedCollection,
      empleado.departamento,
    );
    this.jefesSharedCollection = this.jefeService.addJefeToCollectionIfMissing<IJefe>(this.jefesSharedCollection, empleado.jefe);
  }

  protected loadRelationshipsOptions(): void {
    this.departamentoService
      .query()
      .pipe(map((res: HttpResponse<IDepartamento[]>) => res.body ?? []))
      .pipe(
        map((departamentos: IDepartamento[]) =>
          this.departamentoService.addDepartamentoToCollectionIfMissing<IDepartamento>(departamentos, this.empleado?.departamento),
        ),
      )
      .subscribe((departamentos: IDepartamento[]) => (this.departamentosSharedCollection = departamentos));

    this.jefeService
      .query()
      .pipe(map((res: HttpResponse<IJefe[]>) => res.body ?? []))
      .pipe(map((jefes: IJefe[]) => this.jefeService.addJefeToCollectionIfMissing<IJefe>(jefes, this.empleado?.jefe)))
      .subscribe((jefes: IJefe[]) => (this.jefesSharedCollection = jefes));
  }
}
