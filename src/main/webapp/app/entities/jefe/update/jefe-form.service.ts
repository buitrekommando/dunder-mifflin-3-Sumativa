import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import { IJefe, NewJefe } from '../jefe.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IJefe for edit and NewJefeFormGroupInput for create.
 */
type JefeFormGroupInput = IJefe | PartialWithRequiredKeyOf<NewJefe>;

type JefeFormDefaults = Pick<NewJefe, 'id'>;

type JefeFormGroupContent = {
  id: FormControl<IJefe['id'] | NewJefe['id']>;
  nombre: FormControl<IJefe['nombre']>;
  telefono: FormControl<IJefe['telefono']>;
};

export type JefeFormGroup = FormGroup<JefeFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class JefeFormService {
  createJefeFormGroup(jefe: JefeFormGroupInput = { id: null }): JefeFormGroup {
    const jefeRawValue = {
      ...this.getFormDefaults(),
      ...jefe,
    };
    return new FormGroup<JefeFormGroupContent>({
      id: new FormControl(
        { value: jefeRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      nombre: new FormControl(jefeRawValue.nombre, {
        validators: [Validators.required],
      }),
      telefono: new FormControl(jefeRawValue.telefono, {
        validators: [Validators.required],
      }),
    });
  }

  getJefe(form: JefeFormGroup): IJefe | NewJefe {
    return form.getRawValue() as IJefe | NewJefe;
  }

  resetForm(form: JefeFormGroup, jefe: JefeFormGroupInput): void {
    const jefeRawValue = { ...this.getFormDefaults(), ...jefe };
    form.reset(
      {
        ...jefeRawValue,
        id: { value: jefeRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): JefeFormDefaults {
    return {
      id: null,
    };
  }
}
