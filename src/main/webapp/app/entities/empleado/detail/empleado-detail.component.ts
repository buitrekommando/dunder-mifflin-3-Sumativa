import { Component, input } from '@angular/core';
import { RouterModule } from '@angular/router';

import SharedModule from 'app/shared/shared.module';
import { DurationPipe, FormatMediumDatePipe, FormatMediumDatetimePipe } from 'app/shared/date';
import { IEmpleado } from '../empleado.model';

@Component({
  standalone: true,
  selector: 'jhi-empleado-detail',
  templateUrl: './empleado-detail.component.html',
  imports: [SharedModule, RouterModule, DurationPipe, FormatMediumDatetimePipe, FormatMediumDatePipe],
})
export class EmpleadoDetailComponent {
  empleado = input<IEmpleado | null>(null);

  previousState(): void {
    window.history.back();
  }
}
