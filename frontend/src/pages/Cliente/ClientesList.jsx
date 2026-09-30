import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import Layout from '../../pages/Layout/Layout.jsx';

const ClientesList = () => {
  const navigate = useNavigate();
  const [clientes, setClientes] = useState([]);
  const [loading, setLoading] = useState(true);
  
  // Filtros
  const [busca, setBusca] = useState('');
  const [statusFiltro, setStatusFiltro] = useState('');
  const [cidadeFiltro, setCidadeFiltro] = useState('');
  const [tipoFiltro, setTipoFiltro] = useState('');

  const token = localStorage.getItem('tokenJWT');

  useEffect(() => {
    carregarClientes();
  }, [cidadeFiltro, tipoFiltro]);

  const carregarClientes = async () => {
    setLoading(true);
    try {
      let url = '/api/cliente?';
      if (tipoFiltro) url += `tipoPessoa=${tipoFiltro}&`;
      if (cidadeFiltro) url += `cidade=${encodeURIComponent(cidadeFiltro)}&`;

      const response = await fetch(url, {
        method: 'GET',
        headers: {
          'Authorization': `Bearer ${token}`
        }
      });

      if (response.ok) {
        const data = await response.json();
        setClientes(data);
      }
    } catch (error) {
      console.error("Erro ao carregar clientes:", error);
    } finally {
      setLoading(false);
    }
  };

  const toggleStatus = async (id) => {
    try {
      const response = await fetch(`/api/cliente/${id}`, { 
        method: 'PATCH',
        headers: {
          'Authorization': `Bearer ${token}`
        }
      });
      if (response.ok) {
        carregarClientes();
      }
    } catch (error) {
      console.error("Erro ao alterar status do cliente:", error);
    }
  };

  // Filtragem local por termo de busca (Nome, Documento, Cidade)
  const clientesFiltrados = clientes.filter(c => {
    const termo = busca.toLowerCase();
    const coincideBusca = (c.nome && c.nome.toLowerCase().includes(termo)) ||
                          (c.documento && c.documento.toLowerCase().includes(termo)) ||
                          (c.endereco?.cidade && c.endereco.cidade.toLowerCase().includes(termo));
    
    const coincideStatus = statusFiltro === '' ? true : 
      (statusFiltro === 'ATIVO' ? c.ativo : !c.ativo);

    return coincideBusca && coincideStatus;
  });

  const styles = {
    headerCard: { backgroundColor: '#fff', padding: '20px', borderRadius: '8px', boxShadow: '0 2px 4px rgba(0,0,0,0.05)', marginBottom: '20px' },
    filterBar: { display: 'flex', gap: '15px', marginTop: '15px', flexWrap: 'wrap' },
    input: { padding: '10px 14px', borderRadius: '6px', border: '1px solid #ccc', fontSize: '14px', flex: 1, minWidth: '240px' },
    select: { padding: '10px 14px', borderRadius: '6px', border: '1px solid #ccc', fontSize: '14px', backgroundColor: '#fff' },
    btnPrimary: { backgroundColor: '#3498db', color: '#fff', border: 'none', padding: '10px 20px', borderRadius: '6px', fontWeight: 'bold', cursor: 'pointer' },
    tableCard: { backgroundColor: '#fff', borderRadius: '8px', boxShadow: '0 2px 4px rgba(0,0,0,0.05)', overflow: 'hidden' },
    table: { width: '100%', borderCollapse: 'collapse', textAlign: 'left', fontSize: '14px' },
    th: { backgroundColor: '#f8f9fa', padding: '14px 20px', borderBottom: '2px solid #eee', color: '#2c3e50' },
    td: { padding: '14px 20px', borderBottom: '1px solid #eee', color: '#333' },
    badgeAtivo: { color: '#27ae60', fontWeight: 'bold' },
    badgeInativo: { color: '#e74c3c', fontWeight: 'bold' },
    actionIcon: { cursor: 'pointer', marginRight: '10px', fontSize: '16px' },
    pagination: { display: 'flex', justifyContent: 'center', alignItems: 'center', gap: '10px', padding: '20px', color: '#7f8c8d' }
  };

  return (
    <Layout>
      <div style={styles.headerCard}>
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
          <div>
            <h2 style={{ margin: 0, color: '#2c3e50' }}>👥 Clientes</h2>
            <p style={{ margin: '5px 0 0 0', color: '#7f8c8d', fontSize: '14px' }}>
              Gerencie os clientes e acompanhe seu relacionamento com a empresa.
            </p>
          </div>
        </div>

        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginTop: '20px' }}>
          <input
            type="text"
            placeholder="🔍 Pesquisar por nome, CNPJ ou cidade..."
            style={styles.input}
            value={busca}
            onChange={(e) => setBusca(e.target.value)}
          />
          <button style={{ ...styles.btnPrimary, marginLeft: '15px' }} onClick={() => navigate('/clientes/novo')}>
            + Novo Cliente
          </button>
        </div>

        <div style={styles.filterBar}>
          <select style={styles.select} value={statusFiltro} onChange={(e) => setStatusFiltro(e.target.value)}>
            <option value="">Status ▼</option>
            <option value="ATIVO">🟢 Ativo</option>
            <option value="INATIVO">🔴 Inativo</option>
          </select>

          <select style={styles.select} value={cidadeFiltro} onChange={(e) => setCidadeFiltro(e.target.value)}>
            <option value="">Cidade ▼</option>
            <option value="Morrinhos">Morrinhos</option>
            <option value="Goiânia">Goiânia</option>
            <option value="Itumbiara">Itumbiara</option>
          </select>

          <select style={styles.select} value={tipoFiltro} onChange={(e) => setTipoFiltro(e.target.value)}>
            <option value="">Tipo ▼</option>
            <option value="JURIDICA">Pessoa Jurídica</option>
            <option value="FISICA">Pessoa Física</option>
          </select>
        </div>
      </div>

      <div style={styles.tableCard}>
        {loading ? (
          <div style={{ padding: '30px', textAlign: 'center', color: '#7f8c8d' }}>Carregando clientes...</div>
        ) : (
          <table style={styles.table}>
            <thead>
              <tr>
                <th style={styles.th}>Cliente / Empresa</th>
                <th style={styles.th}>CNPJ/CPF</th>
                <th style={styles.th}>Cidade</th>
                <th style={styles.th}>Telefone</th>
                <th style={styles.th}>Status</th>
                <th style={styles.th}>Ações</th>
              </tr>
            </thead>
            <tbody>
              {clientesFiltrados.length === 0 ? (
                <tr>
                  <td colSpan="6" style={{ ...styles.td, textAlign: 'center', color: '#95a5a6' }}>
                    Nenhum cliente encontrado.
                  </td>
                </tr>
              ) : (
                clientesFiltrados.map((cliente) => (
                  <tr key={cliente.id}>
                    <td style={styles.td}><strong>{cliente.nome}</strong></td>
                    <td style={styles.td}>{cliente.documento}</td>
                    <td style={styles.td}>{cliente.endereco?.cidade || '-'}</td>
                    <td style={styles.td}>{cliente.contato?.telefone || cliente.contato?.celular || '-'}</td>
                    <td style={styles.td}>
                      {cliente.ativo ? (
                        <span style={styles.badgeAtivo}>🟢 Ativo</span>
                      ) : (
                        <span style={styles.badgeInativo}>🔴 Inativo</span>
                      )}
                    </td>
                    <td style={styles.td}>
                      <span title="Visualizar" style={styles.actionIcon} onClick={() => navigate(`/clientes/${cliente.id}`)}>👁</span>
                      <span title="Editar" style={styles.actionIcon} onClick={() => navigate(`/clientes/editar/${cliente.id}`)}>✏️</span>
                      <span title="Alternar Status" style={styles.actionIcon} onClick={() => toggleStatus(cliente.id)}>⋮</span>
                    </td>
                  </tr>
                ))
              )}
            </tbody>
          </table>
        )}

        <div style={styles.pagination}>
          <span>←</span>
          <span style={{ fontWeight: 'bold', color: '#3498db' }}>1</span>
          <span>2</span>
          <span>3</span>
          <span>4</span>
          <span>5</span>
          <span>→</span>
        </div>
      </div>
    </Layout>
  );
};

export default ClientesList;