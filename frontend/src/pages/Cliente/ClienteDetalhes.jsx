import React, { useState, useEffect } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import Layout from '../../pages/Layout/Layout.jsx';

const ClienteDetalhes = () => {
  const navigate = useNavigate();
  const { id } = useParams();
  const [cliente, setCliente] = useState(null);
  const [abaAtiva, setAbaAtiva] = useState('Historico');

  useEffect(() => {
    carregarCliente();
  }, [id]);

  const carregarCliente = async () => {
    try {
      const token = localStorage.getItem('tokenJWT');
      const response = await fetch(`/api/cliente/${id}`, {
        headers: {
          'Authorization': `Bearer ${token}`
        }
      });
      if (response.ok) {
        const data = await response.json();
        setCliente(data);
      }
    } catch (error) {
      console.error("Erro ao carregar detalhes do cliente:", error);
    }
  };

  const historicoComprasMock = [
    { data: '15/09/2026', pedido: '#000245', valor: 'R$ 850,00', pagamento: 'Boleto' },
    { data: '02/09/2026', pedido: '#000231', valor: 'R$ 420,00', pagamento: 'Pix' },
    { data: '20/08/2026', pedido: '#000198', valor: 'R$ 1.120,00', pagamento: 'Boleto' }
  ];

  const styles = {
    card: { backgroundColor: '#fff', borderRadius: '8px', padding: '20px', boxShadow: '0 2px 4px rgba(0,0,0,0.05)', marginBottom: '20px' },
    headerTop: { display: 'flex', justifyContent: 'space-between', alignItems: 'center' },
    badge: { color: '#27ae60', fontWeight: 'bold', display: 'flex', alignItems: 'center', gap: '5px', marginTop: '10px' },
    profileBox: { display: 'flex', gap: '20px', marginTop: '15px' },
    avatar: { width: '120px', height: '120px', backgroundColor: '#f8f9fa', border: '1px solid #eee', borderRadius: '8px', display: 'flex', flexDirection: 'column', alignItems: 'center', justifyContent: 'center', fontSize: '30px' },
    dataBox: { flex: 1, backgroundColor: '#f8f9fa', border: '1px solid #eee', borderRadius: '8px', padding: '15px' },
    summaryGrid: { display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(180px, 1fr))', gap: '15px', marginTop: '15px' },
    summaryCard: { border: '1px solid #eee', borderRadius: '8px', padding: '15px', textAlign: 'center', backgroundColor: '#fff' },
    tabsBar: { display: 'flex', gap: '10px', marginBottom: '15px' },
    tabButton: (active) => ({
      padding: '8px 16px', borderRadius: '6px', border: active ? '1px solid #3498db' : '1px solid #eee',
      backgroundColor: active ? '#3498db' : '#fff', color: active ? '#fff' : '#2c3e50',
      cursor: 'pointer', fontWeight: 'bold', fontSize: '14px'
    }),
    table: { width: '100%', borderCollapse: 'collapse', textAlign: 'left', fontSize: '14px' },
    th: { backgroundColor: '#f8f9fa', padding: '12px 15px', borderBottom: '2px solid #eee', color: '#2c3e50' },
    td: { padding: '12px 15px', borderBottom: '1px solid #eee', color: '#333' },
    btnEdit: { backgroundColor: '#fff', border: '1px solid #ccc', padding: '6px 14px', borderRadius: '6px', cursor: 'pointer', fontSize: '14px', marginRight: '5px' }
  };

  if (!cliente) {
    return (
      <Layout>
        <div style={styles.card}>Carregando dados do cliente...</div>
      </Layout>
    );
  }

  return (
    <Layout>
      {/* CABEÇALHO */}
      <div style={styles.card}>
        <div style={styles.headerTop}>
          <h2 style={{ margin: 0, color: '#2c3e50' }}>👥 Cliente: {cliente.nome}</h2>
          <div>
            <button style={styles.btnEdit} onClick={() => navigate(`/clientes/editar/${cliente.id}`)}>✏️ Editar</button>
            <button style={styles.btnEdit}>⋮ Mais</button>
          </div>
        </div>

        <div style={styles.badge}>
          🟢 {cliente.ativo ? 'ATIVO' : 'INATIVO'}
        </div>

        <div style={styles.profileBox}>
          <div style={styles.avatar}>
            🏢
            <span style={{ fontSize: '12px', marginTop: '10px', textAlign: 'center', color: '#555' }}>
              {cliente.nomeFantasia || cliente.nome}
            </span>
          </div>

          <div style={styles.dataBox}>
            <div style={{ fontWeight: 'bold', color: '#2c3e50', marginBottom: '10px' }}>DADOS CADASTRAIS</div>
            <p style={{ margin: '4px 0', fontSize: '14px' }}><strong>Razão Social:</strong> {cliente.nome}</p>
            <p style={{ margin: '4px 0', fontSize: '14px' }}><strong>Nome Fantasia:</strong> {cliente.nomeFantasia || '-'}</p>
            <p style={{ margin: '4px 0', fontSize: '14px' }}><strong>CNPJ/CPF:</strong> {cliente.documento}</p>
            <p style={{ margin: '4px 0', fontSize: '14px' }}>
              <strong>Cidade:</strong> {cliente.endereco?.cidade || '-'} - {cliente.endereco?.estado || ''}
            </p>
          </div>
        </div>
      </div>

      {/* RESUMO FINANCIAL / COMPRAS */}
      <div style={styles.card}>
        <div style={{ fontWeight: 'bold', color: '#2c3e50', marginBottom: '10px' }}>📊 RESUMO</div>
        <div style={styles.summaryGrid}>
          <div style={styles.summaryCard}>
            <div style={{ color: '#7f8c8d', fontSize: '12px' }}>Total Compras</div>
            <div style={{ fontSize: '20px', fontWeight: 'bold', color: '#2c3e50', marginTop: '5px' }}>47</div>
          </div>
          <div style={styles.summaryCard}>
            <div style={{ color: '#7f8c8d', fontSize: '12px' }}>Total Gasto</div>
            <div style={{ fontSize: '20px', fontWeight: 'bold', color: '#2c3e50', marginTop: '5px' }}>R$ 18.450</div>
          </div>
          <div style={styles.summaryCard}>
            <div style={{ color: '#7f8c8d', fontSize: '12px' }}>Última Compra</div>
            <div style={{ fontSize: '18px', fontWeight: 'bold', color: '#2c3e50', marginTop: '5px' }}>15/09/2026</div>
          </div>
          <div style={styles.summaryCard}>
            <div style={{ color: '#7f8c8d', fontSize: '12px' }}>Em Aberto</div>
            <div style={{ fontSize: '20px', fontWeight: 'bold', color: '#e74c3c', marginTop: '5px' }}>R$ 1.250</div>
          </div>
        </div>
      </div>

      {/* NAVEGAÇÃO DE ABAS */}
      <div style={styles.tabsBar}>
        <button style={styles.tabButton(abaAtiva === 'Dados')} onClick={() => setAbaAtiva('Dados')}>Dados</button>
        <button style={styles.tabButton(abaAtiva === 'Compras')} onClick={() => setAbaAtiva('Compras')}>Compras</button>
        <button style={styles.tabButton(abaAtiva === 'Financeiro')} onClick={() => setAbaAtiva('Financeiro')}>Financeiro</button>
        <button style={styles.tabButton(abaAtiva === 'Contatos')} onClick={() => setAbaAtiva('Contatos')}>Contatos</button>
        <button style={styles.tabButton(abaAtiva === 'Historico')} onClick={() => setAbaAtiva('Historico')}>Histórico</button>
      </div>

      {/* CONTEÚDO DA ABA ATIVA */}
      <div style={styles.card}>
        {abaAtiva === 'Historico' && (
          <div>
            <div style={{ fontWeight: 'bold', color: '#2c3e50', marginBottom: '15px' }}>HISTÓRICO DE COMPRAS</div>
            <table style={styles.table}>
              <thead>
                <tr>
                  <th style={styles.th}>Data</th>
                  <th style={styles.th}>Pedido</th>
                  <th style={styles.th}>Valor</th>
                  <th style={styles.th}>Forma de Pagamento</th>
                </tr>
              </thead>
              <tbody>
                {historicoComprasMock.map((item, index) => (
                  <tr key={index}>
                    <td style={styles.td}>{item.data}</td>
                    <td style={styles.td}><strong>{item.pedido}</strong></td>
                    <td style={styles.td}>{item.valor}</td>
                    <td style={styles.td}>{item.pagamento}</td>
                  </tr>
                ))}
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
          </div>
        )}

        {abaAtiva !== 'Historico' && abaAtiva !== 'Dados' && (
          <div style={{ padding: '20px', color: '#7f8c8d', textAlign: 'center' }}>
            Nenhum registro recente nesta seção.
          </div>
        )}
      </div>
    </Layout>
  );
};

export default ClienteDetalhes;