import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import SharedModule from 'app/shared/shared.module';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';
import { IJefe } from '../jefe.model';
import { JefeService } from '../service/jefe.service';

@Component({
  standalone: true,
  templateUrl: './jefe-delete-dialog.component.html',
  imports: [SharedModule, FormsModule],
})
export class JefeDeleteDialogComponent {
  jefe?: IJefe;

  protected jefeService = inject(JefeService);
  protected activeModal = inject(NgbActiveModal);

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.jefeService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
