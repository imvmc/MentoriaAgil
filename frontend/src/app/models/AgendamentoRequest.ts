export interface AgendamentoRequest {
  mentorId: number;
  dataHoraInicio: string;
  dataHoraFim: string;
  formato: 'ONLINE' | 'PRESENCIAL';
  linkReuniao?: string;
  endereco?: string;
}