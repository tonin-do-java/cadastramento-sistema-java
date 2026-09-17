import React, { useState, useEffect } from 'react';
import { formatarNotificacao } from '../services/notificacaoUtils';

const Notificacao = () => {
  const [filtroAtivo, setFiltroAtivo] = useState('Todas');
  const [listaNotificacoes, setListaNotificacoes] = useState([]);

  useEffect(() => {
    const carregarTodasNotificacoes = async () => {
      try {
        const token = localStorage.getItem('tokenJWT');
        const response = await fetch('/api/notificacoes', {
          headers: { 'Authorization': `Bearer ${token}` }
        });
        if (response.ok) {
          const data = await response.json();
          const notificacoesFormatadas = data.map(formatarNotificacao);
          setListaNotificacoes(notificacoesFormatadas);
        }
      } catch (error) {
        console.error("Erro ao buscar histórico de notificações", error);
      }
    };
    
    carregarTodasNotificacoes();
  }, []);

  const marcarComoLida = async (id) => {
    try {
      const token = localStorage.getItem('tokenJWT');
      await fetch(`/api/notificacoes/${id}/ler`, {
        method: 'PATCH',
        headers: { 'Authorization': `Bearer ${token}` }
      });
      // Atualiza o estado visualmente sem precisar recarregar a página
      setListaNotificacoes(listaNotificacoes.map(n => 
        n.id === id ? { ...n, lida: true } : n
      ));
    } catch (error) {
      console.error("Erro ao marcar como lida", error);
    }
  };

  const notificacoesFiltradas = listaNotificacoes.filter(notif => {
    if (filtroAtivo === 'Todas') return true;
    if (filtroAtivo === 'Não lidas') return !notif.lida;
    return notif.categoria === filtroAtivo;
  });

  const abas = ['Todas', 'Não lidas', 'Estoque', 'Validade', 'Sistema'];

  const styles = {
    container: { padding: '20px', maxWidth: '900px', margin: '0 auto', fontFamily: 'sans-serif' },
    header: { marginBottom: '20px' },
    tituloPagina: { fontSize: '24px', fontWeight: 'bold', color: '#2c3e50', display: 'flex', alignItems: 'center', gap: '10px' },
    subtitulo: { color: '#7f8c8d', fontSize: '14px', marginTop: '5px' },
    tabsContainer: { display: 'flex', gap: '10px', marginBottom: '20px', borderBottom: '2px solid #ecf0f1', paddingBottom: '10px' },
    tabBotao: (ativo) => ({
      padding: '8px 16px', backgroundColor: ativo ? '#3498db' : 'transparent', color: ativo ? '#fff' : '#7f8c8d',
      border: 'none', borderRadius: '20px', cursor: 'pointer', fontWeight: 'bold', fontSize: '14px', transition: 'all 0.2s'
    }),
    lista: { display: 'flex', flexDirection: 'column', gap: '15px' },
    card: (lida) => ({
      display: 'flex', alignItems: 'flex-start', padding: '16px', backgroundColor: lida ? '#fff' : '#f4f9fd',
      border: `1px solid ${lida ? '#ecf0f1' : '#bbdefb'}`, borderRadius: '8px', position: 'relative',
      boxShadow: '0 1px 3px rgba(0,0,0,0.05)'
    }),
    cardIcone: { fontSize: '24px', marginRight: '15px' },
    cardConteudo: { flex: 1 },
    cardHeader: { display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '5px' },
    cardTitulo: { fontSize: '16px', fontWeight: 'bold', color: '#2c3e50', margin: 0 },
    indicadorNaoLida: { display: 'flex', alignItems: 'center', gap: '5px', color: '#3498db', fontSize: '12px', fontWeight: 'bold' },
    bolinhaAzul: { width: '8px', height: '8px', backgroundColor: '#3498db', borderRadius: '50%' },
    cardTexto: { color: '#555', margin: '0 0 10px 0', fontSize: '14px' },
    cardFooter: { display: 'flex', justifyContent: 'space-between', alignItems: 'center' },
    cardTempo: { color: '#95a5a6', fontSize: '12px' },
    linkAcao: { color: '#3498db', textDecoration: 'none', fontSize: '13px', fontWeight: 'bold', cursor: 'pointer', background: 'none', border: 'none' }
  };

  return (
    <div style={styles.container}>
      <div style={styles.header}>
        <div style={styles.tituloPagina}>🔔 Notificações</div>
        <div style={styles.subtitulo}>Acompanhe os avisos e alertas do sistema.</div>
      </div>

      <div style={styles.tabsContainer}>
        {abas.map(aba => (
          <button 
            key={aba} 
            style={styles.tabBotao(filtroAtivo === aba)}
            onClick={() => setFiltroAtivo(aba)}
          >
            {aba}
          </button>
        ))}
      </div>

      <div style={styles.lista}>
        {notificacoesFiltradas.length === 0 ? (
          <p style={{ textAlign: 'center', color: '#7f8c8d', padding: '20px' }}>Nenhuma notificação encontrada.</p>
        ) : (
          notificacoesFiltradas.map((notif) => (
            <div key={notif.id} style={styles.card(notif.lida)}>
              <div style={styles.cardIcone}>{notif.icone}</div>
              
              <div style={styles.cardConteudo}>
                <div style={styles.cardHeader}>
                  <p style={styles.cardTitulo}>{notif.titulo}</p>
                  {!notif.lida && (
                    <div style={styles.indicadorNaoLida}>
                      <span style={styles.bolinhaAzul}></span> NÃO LIDA
                    </div>
                  )}
                </div>
                
                <p style={styles.cardTexto}>{notif.texto}</p>
                
                <div style={styles.cardFooter}>
                  <span style={styles.cardTempo}>{notif.tempo}</span>
                  {!notif.lida && (
                    <button style={styles.linkAcao} onClick={() => marcarComoLida(notif.id)}>
                      ✓ Marcar como lida
                    </button>
                  )}
                </div>
              </div>
            </div>
          ))
        )}
      </div>
    </div>
  );
};

export default Notificacao;