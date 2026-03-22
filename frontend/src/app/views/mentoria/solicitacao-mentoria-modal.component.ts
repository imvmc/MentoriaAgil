import { Component, Inject, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { MAT_DIALOG_DATA, MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';

import { MentoriaService } from '../../services/mentoria/mentoria.service';
import { AgendamentoRequest } from '../../models/AgendamentoRequest';

@Component({
  selector: 'app-solicitacao-mentoria-modal',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatDialogModule,
    MatSnackBarModule,
    MatButtonModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule
  ],
  templateUrl: './solicitacao-mentoria-modal.component.html',
  styleUrls: ['./solicitacao-mentoria-modal.component.css']
})
export class SolicitacaoMentoriaModalComponent {
  private fb = inject(FormBuilder);
  private mentoriaService = inject(MentoriaService);
  private snackBar = inject(MatSnackBar);

  form: FormGroup;
  loading = false;

  constructor(
    public dialogRef: MatDialogRef<SolicitacaoMentoriaModalComponent>,
    @Inject(MAT_DIALOG_DATA) public data: { mentorId: number }
  ) {
    this.form = this.fb.group({
      dataHoraInicio: ['', Validators.required],
      dataHoraFim: ['', Validators.required],
      formato: ['ONLINE', Validators.required],
      linkReuniao: [''],
      endereco: ['']
    });

    this.form.get('formato')?.valueChanges.subscribe((formato) => {
      this.atualizarValidacoes(formato);
    });

    this.atualizarValidacoes(this.form.get('formato')?.value);
  }

  atualizarValidacoes(formato: 'ONLINE' | 'PRESENCIAL') {
    const linkControl = this.form.get('linkReuniao');
    const enderecoControl = this.form.get('endereco');

    linkControl?.clearValidators();
    enderecoControl?.clearValidators();

    if (formato === 'ONLINE') {
      linkControl?.setValidators([Validators.required]);
    } else {
      enderecoControl?.setValidators([Validators.required]);
    }

    linkControl?.updateValueAndValidity();
    enderecoControl?.updateValueAndValidity();
  }

  salvar() {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      this.snackBar.open('Preencha os campos obrigatórios.', 'Fechar', { duration: 3000 });
      return;
    }

    const payload: AgendamentoRequest = {
      mentorId: this.data.mentorId,
      dataHoraInicio: this.form.value.dataHoraInicio,
      dataHoraFim: this.form.value.dataHoraFim,
      formato: this.form.value.formato,
      linkReuniao: this.form.value.formato === 'ONLINE' ? this.form.value.linkReuniao : undefined,
      endereco: this.form.value.formato === 'PRESENCIAL' ? this.form.value.endereco : undefined
    };

    this.loading = true;

    this.mentoriaService.agendarSessao(payload).subscribe({
      next: () => {
        this.loading = false;
        this.snackBar.open(
          'Sessão agendada com sucesso! Notificação enviada.',
          'Fechar',
          { duration: 4000 }
        );
        this.dialogRef.close(true);
      },
      error: (err) => {
        this.loading = false;

        const mensagem =
          err?.error?.message ||
          err?.error?.erro ||
          'Erro ao agendar sessão.';

        this.snackBar.open(mensagem, 'Fechar', { duration: 4000 });
      }
    });
  }

  cancelar() {
    this.dialogRef.close(false);
  }
}