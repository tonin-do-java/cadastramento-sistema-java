import React, { useState, useRef, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { formatarNotificacao } from '../services/notificacaoUtils';

const SinoNotificacao = () => {
  const [aberto, setAberto] = useState(false);
  const [notificacoes, setNotificacoes] = useState([]);
  const dropdownRef = useRef(null);
  const navigate = useNavigate();

  // Busca as notificações não lidas da API
  const carregarNotificacoes = async () => {
    try {
      const token = localStorage.getItem('tokenJWT'); // Ajuste conforme seu Auth
      const response = await fetch('/api/notificacoes/nao-lida', {
        headers: { 'Authorization': `Bearer ${token}` }
      });
      if (response.ok) {
        const data = await response.json();

        const notificacoesFormatadas = data.map(formatarNotificacao);
        setNotificacoes(notificacoesFormatadas);
      }
    } catch (error) {
      console.error("Erro ao buscar notificações", error);
    }
  };

  useEffect(() => {
    carregarNotificacoes();
    // Atualiza o sino a cada 1 minuto (opcional)
    const interval = setInterval(carregarNotificacoes, 60000); 
    return () => clearInterval(interval);
  }, []);

  useEffect(() => {
    const handleClickFora = (event) => {
      if (dropdownRef.current && !dropdownRef.current.contains(event.target)) {
        setAberto(false);
      }
    };
    document.addEventListener('mousedown', handleClickFora);
    return () => document.removeEventListener('mousedown', handleClickFora);
  }, []);

  const marcarComoLida = async (id) => {
    try {
      const token = localStorage.getItem('tokenJWT');
      await fetch(`/api/notificacoes/${id}/ler`, {
        method: 'PATCH',
        headers: { 'Authorization': `Bearer ${token}` }
      });
      // Remove da lista do dropdown após clicar
      setNotificacoes(notificacoes.filter(n => n.id !== id));
    } catch (error) {
      console.error("Erro ao marcar como lida", error);
    }
  };

  const styles = {
    container: { position: 'relative', display: 'inline-block' },
    sinoContainer: { cursor: 'pointer', display: 'flex', alignItems: 'center', fontSize: '18px', padding: '5px 10px', userSelect: 'none' },
    badge: { position: 'absolute', top: '0px', right: '0px', backgroundColor: '#e74c3c', color: 'white', borderRadius: '50%', padding: '2px 6px', fontSize: '11px', fontWeight: 'bold' },
    dropdown: { position: 'absolute', top: '40px', right: '0', width: '300px', backgroundColor: '#fff', border: '1px solid #ecf0f1', borderRadius: '8px', boxShadow: '0 4px 12px rgba(0,0,0,0.15)', zIndex: 1000, overflow: 'hidden' },
    dropdownHeader: { backgroundColor: '#f8f9fa', padding: '12px 15px', fontWeight: 'bold', borderBottom: '1px solid #ecf0f1', color: '#2c3e50', fontSize: '15px' },
    lista: { listStyle: 'none', margin: 0, padding: 0, maxHeight: '350px', overflowY: 'auto' },
    item: { padding: '12px 15px', borderBottom: '1px solid #ecf0f1', display: 'flex', gap: '12px', cursor: 'pointer', transition: 'bg 0.2s' },
    icone: { fontSize: '20px' },
    conteudo: { display: 'flex', flexDirection: 'column', gap: '4px' },
    titulo: { fontSize: '14px', fontWeight: 'bold', color: '#2c3e50', margin: 0 },
    texto: { fontSize: '13px', color: '#555', margin: 0, whiteSpace: 'pre-line' },
    tempo: { fontSize: '11px', color: '#95a5a6', marginTop: '4px' },
    footer: { padding: '10px', textAlign: 'center', backgroundColor: '#f8f9fa', cursor: 'pointer', fontWeight: 'bold', color: '#3498db', fontSize: '14px' }
  };

  return (
    <div style={styles.container} ref={dropdownRef}>
      <div style={styles.sinoContainer} onClick={() => setAberto(!aberto)}>
        🔔 Notificações
        {notificacoes.length > 0 && (
          <span style={styles.badge}>{notificacoes.length}</span>
        )}
      </div>

      {aberto && (
        <div style={styles.dropdown}>
          <div style={styles.dropdownHeader}>🔔 Notificações</div>
          <ul style={styles.lista}>
            {notificacoes.length === 0 ? (
              <li style={{ padding: '15px', textAlign: 'center', color: '#7f8c8d', fontSize: '13px' }}>Sem novas notificações</li>
            ) : (
              notificacoes.map((notificacao) => (
                <li 
                  key={notificacao.id} 
                  style={styles.item}
                  onClick={() => marcarComoLida(notificacao.id)}
                  onMouseEnter={(e) => e.currentTarget.style.backgroundColor = '#f1f2f6'}
                  onMouseLeave={(e) => e.currentTarget.style.backgroundColor = 'transparent'}
                >
                  <span style={styles.icone}>{notificacao.icone}</span>
                  <div style={styles.conteudo}>
                    <p style={styles.titulo}>{notificacao.titulo}</p>
                    <p style={styles.texto}>{notificacao.texto}</p>
                    <span style={styles.tempo}>{notificacao.tempo}</span>
                  </div>
                </li>
              ))
            )}
          </ul>
          <div 
            style={styles.footer} 
            onClick={() => {
              setAberto(false);
              navigate('/notificacoes');
            }}
          >
            Ver todas
          </div>
        </div>
      )}
    </div>
  );
};

export default SinoNotificacao;