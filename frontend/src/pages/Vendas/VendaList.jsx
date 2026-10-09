import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import Layout from '../../pages/Layout/Layout.jsx';

const VendaList = () => {
  const navigate = useNavigate();

  const [vendas, setVendas] = useState([]);
  const [loading, setLoading] = useState(false);
  const [busca, setBusca] = useState('');
  
  // Filtros
  const [filtroStatus, setFiltroStatus] = useState('');
  const [filtroPagamento, setFiltroPagamento] = useState('');
  const token = localStorage.getItem('tokenJWT');

  const headers = {
    'Content-Type': 'application/json',
    'Authorization': `Bearer ${token}`
  };

  const carregarVendas = async () => {
    setLoading(true);
    try {
      const response = await fetch('/api/vendas', { headers });
      if (response.ok) {
        const data = await response.json();
        setVendas(data);
      }
    } catch (error) {
      console.error('Erro ao carregar vendas', error);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    carregarVendas();
  }, []);

  const limparFiltros = () => {
    setBusca('');
    setFiltroStatus('');
    setFiltroPagamento('');
  };

  // Filtragem dinâmica no frontend (busca + status + pagamento)
  const vendasFiltradas = vendas.filter((venda) => {
    const termo = busca.toLowerCase().trim();
    const idVendaFormatado = String(venda.id || '').padStart(5, '0');
    const idCliente = String(venda.clienteId || '');

    const bateuBusca = !termo || 
      idVendaFormatado.includes(termo) || 
      idCliente.includes(termo) || 
      `#${idVendaFormatado}`.includes(termo);

    const bateuStatus = !filtroStatus || venda.status === filtroStatus;
    const bateuPagamento = !filtroPagamento || venda.formaPagamento === filtroPagamento;

    return bateuBusca && bateuStatus && bateuPagamento;
  });

  const getStatusBadge = (status) => {
    const cores = {
      PENDENTE: { bg: '#fff3cd', color: '#856404' },
      CONFIRMADA: { bg: '#d1ecf1', color: '#0c5460' },
      EM_SEPARACAO: { bg: '#e2e3e5', color: '#383d41' },
      EM_ROTA: { bg: '#cce5ff', color: '#004085' },
      ENTREGUE: { bg: '#d4edda', color: '#155724' },
      CANCELADA: { bg: '#f8d7da', color: '#721c24' }
    };
    const estilo = cores[status] || { bg: '#eee', color: '#333' };
    return (
      <span style={{
        padding: '4px 8px',
        borderRadius: '12px',
        fontSize: '12px',
        fontWeight: 'bold',
        backgroundColor: estilo.bg,
        color: estilo.color
      }}>
        {status}
      </span>
    );
  };

  return (
    <Layout>
      <div style={{ marginBottom: '20px' }}>
        <h1 style={{ margin: 0, fontSize: '24px', color: '#2c3e50' }}>🛒 Vendas</h1>
        <p style={{ margin: '5px 0 0 0', color: '#7f8c8d', fontSize: '14px' }}>
          Gerencie vendas, pedidos e histórico de vendas.
        </p>
      </div>

      {/* Pesquisa e Botão Nova Venda */}
      <div style={{
        display: 'flex',
        justifyContent: 'space-between',
        alignItems: 'center',
        backgroundColor: '#fff',
        padding: '15px',
        borderRadius: '8px',
        boxShadow: '0 2px 4px rgba(0,0,0,0.05)',
        marginBottom: '20px'
      }}>
        <input
          type="text"
          placeholder="🔎 Buscar por cliente ID, nº da venda (#00125)..."
          value={busca}
          onChange={(e) => setBusca(e.target.value)}
          style={{
            flex: 1,
            padding: '10px 15px',
            borderRadius: '6px',
            border: '1px solid #ccc',
            fontSize: '14px',
            marginRight: '15px'
          }}
        />
        <button
          onClick={() => navigate('/vendas/nova')}
          style={{
            backgroundColor: '#27ae60',
            color: '#fff',
            border: 'none',
            padding: '10px 20px',
            borderRadius: '6px',
            fontWeight: 'bold',
            cursor: 'pointer',
            fontSize: '14px'
          }}
        >
          + Nova Venda
        </button>
      </div>

      {/* Controles de Filtros */}
      <div style={{ display: 'flex', gap: '10px', alignItems: 'center', marginBottom: '20px' }}>
        <span style={{ fontWeight: 'bold', fontSize: '14px', color: '#2c3e50' }}>Filtros:</span>
        <select
          value={filtroStatus}
          onChange={(e) => setFiltroStatus(e.target.value)}
          style={{ padding: '8px 12px', borderRadius: '6px', border: '1px solid #ccc', fontSize: '14px' }}
        >
          <option value="">Status (Todos)</option>
          <option value="PENDENTE">Pendente</option>
          <option value="CONFIRMADA">Confirmada</option>
          <option value="EM_SEPARACAO">Em Separação</option>
          <option value="EM_ROTA">Em Rota</option>
          <option value="ENTREGUE">Entregue</option>
          <option value="CANCELADA">Cancelada</option>
        </select>

        <select
          value={filtroPagamento}
          onChange={(e) => setFiltroPagamento(e.target.value)}
          style={{ padding: '8px 12px', borderRadius: '6px', border: '1px solid #ccc', fontSize: '14px' }}
        >
          <option value="">Pagamento (Todos)</option>
          <option value="DINHEIRO">Dinheiro</option>
          <option value="PIX">Pix</option>
          <option value="CREDITO">Cartão de Crédito</option>
          <option value="DEBITO">Cartão de Débito</option>
          <option value="BOLETO">Boleto</option>
        </select>

        <button
          onClick={limparFiltros}
          style={{
            backgroundColor: '#e74c3c',
            color: '#fff',
            border: 'none',
            padding: '8px 15px',
            borderRadius: '6px',
            cursor: 'pointer',
            fontSize: '13px'
          }}
        >
          🔄 Limpar filtros
        </button>
      </div>

      {/* Tabela de Vendas */}
      <div style={{ backgroundColor: '#fff', borderRadius: '8px', boxShadow: '0 2px 4px rgba(0,0,0,0.05)', overflow: 'hidden' }}>
        <table style={{ width: '100%', borderCollapse: 'collapse', textAlign: 'left', fontSize: '14px' }}>
          <thead>
            <tr style={{ backgroundColor: '#f8f9fa', borderBottom: '2px solid #dee2e6', color: '#2c3e50' }}>
              <th style={{ padding: '12px 15px' }}>Nº</th>
              <th style={{ padding: '12px 15px' }}>Cliente</th>
              <th style={{ padding: '12px 15px' }}>Data</th>
              <th style={{ padding: '12px 15px' }}>Valor</th>
              <th style={{ padding: '12px 15px' }}>Pagamento</th>
              <th style={{ padding: '12px 15px' }}>Status</th>
              <th style={{ padding: '12px 15px', textAlign: 'center' }}>Ações</th>
            </tr>
          </thead>
          <tbody>
            {loading ? (
              <tr>
                <td colSpan="7" style={{ textAlign: 'center', padding: '20px' }}>Carregando vendas...</td>
              </tr>
            ) : vendasFiltradas.length === 0 ? (
              <tr>
                <td colSpan="7" style={{ textAlign: 'center', padding: '20px', color: '#7f8c8d' }}>
                  Nenhuma venda encontrada.
                </td>
              </tr>
            ) : (
              vendasFiltradas.map((venda) => (
                <tr key={venda.id} style={{ borderBottom: '1px solid #eee' }}>
                  <td style={{ padding: '12px 15px', fontWeight: 'bold' }}>#{String(venda.id).padStart(5, '0')}</td>
                  <td style={{ padding: '12px 15px' }}>Cliente #{venda.clienteId}</td>
                  <td style={{ padding: '12px 15px' }}>{new Date(venda.dataHora).toLocaleDateString('pt-BR')}</td>
                  <td style={{ padding: '12px 15px', fontWeight: 'bold' }}>R$ {venda.valorTotal?.toFixed(2)}</td>
                  <td style={{ padding: '12px 15px' }}>{venda.formaPagamento}</td>
                  <td style={{ padding: '12px 15px' }}>{getStatusBadge(venda.status)}</td>
                  <td style={{ padding: '12px 15px', textAlign: 'center' }}>
                    <button
                      onClick={() => navigate(`/vendas/${venda.id}`)}
                      title="Ver Detalhes"
                      style={{ background: 'none', border: 'none', cursor: 'pointer', fontSize: '16px', marginRight: '8px' }}
                    >
                      👁
                    </button>
                  </td>
                </tr>
              ))
            )}
          </tbody>
        </table>
      </div>
    </Layout>
  );
};

export default VendaList;