import { Component, OnInit, inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { IJefe } from '../jefe.model';
import { JefeService } from '../service/jefe.service';
import { JefeFormGroup, JefeFormService } from './jefe-form.service';

@Component({
  standalone: true,
  selector: 'jhi-jefe-update',
  templateUrl: './jefe-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class JefeUpdateComponent implements OnInit {
  isSaving = false;
  jefe: IJefe | null = null;

  protected jefeService = inject(JefeService);
  protected jefeFormService = inject(JefeFormService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: JefeFormGroup = this.jefeFormService.createJefeFormGroup();

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ jefe }) => {
      this.jefe = jefe;
      if (jefe) {
        this.updateForm(jefe);
      }
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const jefe = this.jefeFormService.getJefe(this.editForm);
    if (jefe.id !== null) {
      this.subscribeToSaveResponse(this.jefeService.update(jefe));
    } else {
      this.subscribeToSaveResponse(this.jefeService.create(jefe));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IJefe>>): void {
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

  protected updateForm(jefe: IJefe): void {
    this.jefe = jefe;
    this.jefeFormService.resetForm(this.editForm, jefe);
  }
}
