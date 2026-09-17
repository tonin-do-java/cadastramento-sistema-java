const calcularTempoRelativo = (dataHoraString) => {
  const dataNotificacao = new Date(dataHoraString);
  const agora = new Date();
  const diffEmMinutos = Math.floor((agora - dataNotificacao) / 60000);

  if (diffEmMinutos < 1) return 'Agora mesmo';
  if (diffEmMinutos < 60) return `há ${diffEmMinutos} minutos`;
  if (diffEmMinutos < 1440) return `há ${Math.floor(diffEmMinutos / 60)} horas`;
  return `há ${Math.floor(diffEmMinutos / 1440)} dias`;
};

export const formatarNotificacao = (notifBruta) => {
  const mapaIcones = {
    ESTOQUE: '📦',
    VALIDADE: '⏳',
    SISTEMA: '⚙️'
  };

  const mapaCategorias = {
    ESTOQUE: 'Estoque',
    VALIDADE: 'Validade',
    SISTEMA: 'Sistema'
  };

  return {
    ...notifBruta,
    icone: mapaIcones[notifBruta.tipoNotificacao] || '🔔',
    categoria: mapaCategorias[notifBruta.tipoNotificacao] || 'Outros',
    tempo: calcularTempoRelativo(notifBruta.dataHora)
  };
};