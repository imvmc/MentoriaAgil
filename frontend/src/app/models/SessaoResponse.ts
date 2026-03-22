export interface SessaoResponse {
  id?: number;
  mentorId?: number;
  mentoradoId?: number;
  dataHoraInicio?: string;
  dataHoraFim?: string;
  status?: string;
  formato?: string;
  linkReuniao?: string;
  endereco?: string;
}