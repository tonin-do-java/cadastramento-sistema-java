import React, { useState, useEffect } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import Layout from '../../pages/Layout/Layout.jsx';

const ClienteDetalhes = () => {
  const navigate = useNavigate();
  const { id } = useParams();
  
  const [cliente, setCliente] = useState(null);
  const [vendas, setVendas] = useState([]); // Histórico real de vendas
  const [abaAtiva, setAbaAtiva] = useState('Historico');
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    carregarDados();
  }, [id]);

  const carregarDados = async () => {
    setLoading(true);
    try {
      const token = localStorage.getItem('tokenJWT');
      const headers = { 'Authorization': `Bearer ${token}` };

      // Busca dados do Cliente e suas Vendas em paralelo
      const [resCliente, resVendas] = await Promise.all([
        fetch(`/api/cliente/${id}`, { headers }),
        fetch(`/api/vendas?clienteId=${id}`, { headers })
      ]);

      if (resCliente.ok) {
        setCliente(await resCliente.json());
      }
      
      if (resVendas.ok) {
        setVendas(await resVendas.json());
      }
    } catch (error) {
      console.error("Erro ao carregar detalhes do cliente:", error);
    } finally {
      setLoading(false);
    }
  };

  // Cálculos dinâmicos com base no histórico real
  const totalCompras = vendas.length;
  const totalGasto = vendas.reduce((acc, v) => acc + (v.valorTotal || 0), 0);
  
  // Considera "em aberto" pedidos que não foram cancelados nem entregues
  const emAberto = vendas
    .filter(v => ['PENDENTE', 'EM_SEPARACAO', 'EM_ROTA', 'CONFIRMADA'].includes(v.status))
    .reduce((acc, v) => acc + (v.valorTotal || 0), 0);
    
  const datasDeCompra = vendas.map(v => new Date(v.dataHora).getTime());
  const ultimaCompra = datasDeCompra.length > 0 
    ? new Date(Math.max(...datasDeCompra)).toLocaleDateString('pt-BR') 
    : '-';

  const styles = {
    card: { backgroundColor: '#fff', borderRadius: '8px', padding: '20px', boxShadow: '0 2px 4px rgba(0,0,0,0.05)', marginBottom: '20px' },
    headerTop: { display: 'flex', justifyContent: 'space-between', alignItems: 'center' },
    badge: { color: '#27ae60', fontWeight: 'bold', display: 'flex', alignItems: 'center', gap: '5px', marginTop: '10px' },
    profileBox: { display: 'flex', gap: '20px', marginTop: '15px', flexWrap: 'wrap' },
    avatar: { width: '120px', height: '120px', backgroundColor: '#f8f9fa', border: '1px solid #eee', borderRadius: '8px', display: 'flex', flexDirection: 'column', alignItems: 'center', justifyContent: 'center', fontSize: '30px' },
    dataBox: { flex: 1, backgroundColor: '#f8f9fa', border: '1px solid #eee', borderRadius: '8px', padding: '15px', minWidth: '250px' },
    summaryGrid: { display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(180px, 1fr))', gap: '15px', marginTop: '15px' },
    summaryCard: { border: '1px solid #eee', borderRadius: '8px', padding: '15px', textAlign: 'center', backgroundColor: '#fff' },
    tabsBar: { display: 'flex', gap: '10px', marginBottom: '15px', overflowX: 'auto', paddingBottom: '5px' },
    tabButton: (active) => ({
      padding: '8px 16px', borderRadius: '6px', border: active ? '1px solid #3498db' : '1px solid #eee',
      backgroundColor: active ? '#3498db' : '#fff', color: active ? '#fff' : '#2c3e50',
      cursor: 'pointer', fontWeight: 'bold', fontSize: '14px', whiteSpace: 'nowrap'
    }),
    table: { width: '100%', borderCollapse: 'collapse', textAlign: 'left', fontSize: '14px' },
    th: { backgroundColor: '#f8f9fa', padding: '12px 15px', borderBottom: '2px solid #eee', color: '#2c3e50' },
    td: { padding: '12px 15px', borderBottom: '1px solid #eee', color: '#333' },
    btnEdit: { backgroundColor: '#fff', border: '1px solid #ccc', padding: '6px 14px', borderRadius: '6px', cursor: 'pointer', fontSize: '14px', marginRight: '5px' },
    statusBadge: { padding: '4px 8px', borderRadius: '12px', fontSize: '11px', fontWeight: 'bold', backgroundColor: '#eee' }
  };

  if (loading) {
    return <Layout><div style={styles.card}>Carregando dados do cliente...</div></Layout>;
  }

  if (!cliente) {
    return <Layout><div style={styles.card}>Cliente não encontrado.</div></Layout>;
  }

  return (
    <Layout>
      <div style={styles.card}>
        <div style={styles.headerTop}>
          <h2 style={{ margin: 0, color: '#2c3e50' }}>👥 Cliente: {cliente.nome}</h2>
          <div>
            <button style={styles.btnEdit} onClick={() => navigate(`/clientes/editar/${cliente.id}`)}>✏️ Editar</button>
            <button style={styles.btnEdit} onClick={() => navigate('/clientes')}>Voltar</button>
          </div>
        </div>

        <div style={{...styles.badge, color: cliente.ativo ? '#27ae60' : '#e74c3c'}}>
          {cliente.ativo ? '🟢 ATIVO' : '🔴 INATIVO'}
        </div>

        <div style={styles.profileBox}>
          <div style={styles.avatar}>
            {cliente.tipoPessoa === 'JURIDICA' ? '🏢' : '👤'}
            <span style={{ fontSize: '12px', marginTop: '10px', textAlign: 'center', color: '#555', padding: '0 5px' }}>
              {cliente.nomeFantasia || cliente.nome.split(' ')[0]}
            </span>
          </div>

          <div style={styles.dataBox}>
            <div style={{ fontWeight: 'bold', color: '#2c3e50', marginBottom: '10px' }}>DADOS CADASTRAIS</div>
            <p style={{ margin: '4px 0', fontSize: '14px' }}><strong>Nome/Razão Social:</strong> {cliente.nome}</p>
            {cliente.tipoPessoa === 'JURIDICA' && (
              <p style={{ margin: '4px 0', fontSize: '14px' }}><strong>Nome Fantasia:</strong> {cliente.nomeFantasia || '-'}</p>
            )}
            <p style={{ margin: '4px 0', fontSize: '14px' }}><strong>{cliente.tipoPessoa === 'JURIDICA' ? 'CNPJ' : 'CPF'}:</strong> {cliente.documento}</p>
            <p style={{ margin: '4px 0', fontSize: '14px' }}>
              <strong>Cidade:</strong> {cliente.endereco?.cidade || '-'} - {cliente.endereco?.estado || ''}
            </p>
          </div>
        </div>
      </div>

      <div style={styles.card}>
        <div style={{ fontWeight: 'bold', color: '#2c3e50', marginBottom: '10px' }}>📊 RESUMO</div>
        <div style={styles.summaryGrid}>
          <div style={styles.summaryCard}>
            <div style={{ color: '#7f8c8d', fontSize: '12px' }}>Total Compras</div>
            <div style={{ fontSize: '20px', fontWeight: 'bold', color: '#2c3e50', marginTop: '5px' }}>{totalCompras}</div>
          </div>
          <div style={styles.summaryCard}>
            <div style={{ color: '#7f8c8d', fontSize: '12px' }}>Total Gasto</div>
            <div style={{ fontSize: '20px', fontWeight: 'bold', color: '#2c3e50', marginTop: '5px' }}>R$ {totalGasto.toFixed(2)}</div>
          </div>
          <div style={styles.summaryCard}>
            <div style={{ color: '#7f8c8d', fontSize: '12px' }}>Última Compra</div>
            <div style={{ fontSize: '18px', fontWeight: 'bold', color: '#2c3e50', marginTop: '5px' }}>{ultimaCompra}</div>
          </div>
          <div style={styles.summaryCard}>
            <div style={{ color: '#7f8c8d', fontSize: '12px' }}>Em Aberto</div>
            <div style={{ fontSize: '20px', fontWeight: 'bold', color: emAberto > 0 ? '#e74c3c' : '#27ae60', marginTop: '5px' }}>
              R$ {emAberto.toFixed(2)}
            </div>
          </div>
        </div>
      </div>

      <div style={styles.tabsBar}>
        <button style={styles.tabButton(abaAtiva === 'Historico')} onClick={() => setAbaAtiva('Historico')}>Histórico</button>
        <button style={styles.tabButton(abaAtiva === 'Dados')} onClick={() => setAbaAtiva('Dados')}>Dados</button>
      </div>

      <div style={styles.card}>
        {abaAtiva === 'Historico' && (
          <div style={{ overflowX: 'auto' }}>
            <div style={{ fontWeight: 'bold', color: '#2c3e50', marginBottom: '15px' }}>HISTÓRICO DE COMPRAS (SISTEMA)</div>
            <table style={styles.table}>
              <thead>
                <tr>
                  <th style={styles.th}>Data</th>
                  <th style={styles.th}>Pedido</th>
                  <th style={styles.th}>Valor</th>
                  <th style={styles.th}>Pagamento</th>
                  <th style={styles.th}>Status</th>
                </tr>
              </thead>
              <tbody>
                {vendas.length === 0 ? (
                  <tr>
                    <td colSpan="5" style={{ padding: '20px', textAlign: 'center', color: '#7f8c8d' }}>Nenhuma compra registrada.</td>
                  </tr>
                ) : (
                  vendas.map((venda) => (
                    <tr key={venda.id}>
                      <td style={styles.td}>{new Date(venda.dataHora).toLocaleDateString('pt-BR')}</td>
                      <td style={styles.td}>
                        <button 
                          onClick={() => navigate(`/vendas/${venda.id}`)}
                          style={{ background: 'none', border: 'none', color: '#3498db', fontWeight: 'bold', cursor: 'pointer', padding: 0 }}
                        >
                          #{String(venda.id).padStart(5, '0')}
                        </button>
                      </td>
                      <td style={styles.td}>R$ {venda.valorTotal?.toFixed(2)}</td>
                      <td style={styles.td}>{venda.formaPagamento}</td>
                      <td style={styles.td}>
                        <span style={styles.statusBadge}>{venda.status}</span>
                      </td>
                    </tr>
                  ))
                )}
              </tbody>
            </table>
          </div>
        )}

        {abaAtiva === 'Dados' && (
          <div>
            <h3>Outras Informações Cadastrais</h3>
            <p><strong>Telefone:</strong> {cliente.contato?.telefone || '-'}</p>
            <p><strong>Celular:</strong> {cliente.contato?.celular || '-'}</p>
            <p><strong>E-mail:</strong> {cliente.contato?.email || '-'}</p>
            <p><strong>Logradouro:</strong> {cliente.endereco?.logradouro || '-'}, {cliente.endereco?.numero || 'S/N'}</p>
            <p><strong>Bairro:</strong> {cliente.endereco?.bairro || '-'}</p>
            <p><strong>CEP:</strong> {cliente.endereco?.cep || '-'}</p>
            {cliente.inscricaoEstadual && <p><strong>Insc. Estadual:</strong> {cliente.inscricaoEstadual}</p>}
          </div>
        )}
      </div>
    </Layout>
  );
};

export default ClienteDetalhes;