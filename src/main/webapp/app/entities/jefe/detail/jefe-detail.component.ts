import { Component, input } from '@angular/core';
import { RouterModule } from '@angular/router';

import SharedModule from 'app/shared/shared.module';
import { DurationPipe, FormatMediumDatePipe, FormatMediumDatetimePipe } from 'app/shared/date';
import { IJefe } from '../jefe.model';

@Component({
  standalone: true,
  selector: 'jhi-jefe-detail',
  templateUrl: './jefe-detail.component.html',
  imports: [SharedModule, RouterModule, DurationPipe, FormatMediumDatetimePipe, FormatMediumDatePipe],
})
export class JefeDetailComponent {
  jefe = input<IJefe | null>(null);

  previousState(): void {
    window.history.back();
  }
}
