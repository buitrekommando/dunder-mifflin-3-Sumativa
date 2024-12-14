import { Component, input } from '@angular/core';
import { RouterModule } from '@angular/router';

import SharedModule from 'app/shared/shared.module';
import { DurationPipe, FormatMediumDatePipe, FormatMediumDatetimePipe } from 'app/shared/date';
import { IDepartamento } from '../departamento.model';

@Component({
  standalone: true,
  selector: 'jhi-departamento-detail',
  templateUrl: './departamento-detail.component.html',
  imports: [SharedModule, RouterModule, DurationPipe, FormatMediumDatetimePipe, FormatMediumDatePipe],
})
export class DepartamentoDetailComponent {
  departamento = input<IDepartamento | null>(null);

  previousState(): void {
    window.history.back();
  }
}
