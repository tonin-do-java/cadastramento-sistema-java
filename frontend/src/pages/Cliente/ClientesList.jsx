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
  
  // NOVO: Estado para armazenar as cidades dinâmicas do banco
  const [cidadesDisponiveis, setCidadesDisponiveis] = useState([]);

  // Paginação
  const [paginaAtual, setPaginaAtual] = useState(1);
  const itensPorPagina = 8; // Define quantos clientes aparecem por tela

  const token = localStorage.getItem('tokenJWT');

  // NOVO: Busca a lista de cidades únicas assim que a tela abre
  useEffect(() => {
    const carregarCidades = async () => {
      try {
        const response = await fetch('/api/cliente', {
          headers: { 'Authorization': `Bearer ${token}` }
        });
        
        if (response.ok) {
          const data = await response.json();
          // Mapeia as cidades, ignora nulos/vazios e remove duplicatas
          const cidadesUnicas = [...new Set(data.map(c => c.endereco?.cidade).filter(Boolean))];
          setCidadesDisponiveis(cidadesUnicas.sort()); // Ordem alfabética
        }
      } catch (error) {
        console.error("Erro ao carregar cidades:", error);
      }
    };

    carregarCidades();
  }, [token]);

  // Atualizado: Adicionado 'busca' nas dependências para refazer a consulta à API quando o usuário digitar
  useEffect(() => {
    carregarClientes();
  }, [cidadeFiltro, tipoFiltro, busca]);

  // Reseta para a página 1 sempre que o usuário digitar ou mudar filtros locais
  useEffect(() => {
    setPaginaAtual(1);
  }, [busca, statusFiltro]);

  const carregarClientes = async () => {
    setLoading(true);
    try {
      let url = '/api/cliente?';
      
      // Montagem dinâmica da URL com os parâmetros de filtro
      if (tipoFiltro) url += `tipoPessoa=${tipoFiltro}&`;
      if (cidadeFiltro) url += `cidade=${encodeURIComponent(cidadeFiltro)}&`;
      if (busca) url += `busca=${encodeURIComponent(busca)}&`;

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
        headers: { 'Authorization': `Bearer ${token}` }
      });
      if (response.ok) {
        carregarClientes();
      }
    } catch (error) {
      console.error("Erro ao alterar status do cliente:", error);
    }
  };

  // 1. Aplica filtros locais (garante a filtragem caso a API não suporte o parâmetro ?busca=)
  const clientesFiltrados = clientes.filter(c => {
    const termo = busca.toLowerCase();
    const coincideBusca = (c.nome && c.nome.toLowerCase().includes(termo)) ||
                          (c.documento && c.documento.toLowerCase().includes(termo)) ||
                          (c.endereco?.cidade && c.endereco.cidade.toLowerCase().includes(termo));
    
    const coincideStatus = statusFiltro === '' ? true : 
      (statusFiltro === 'ATIVO' ? c.ativo : !c.ativo);

    return coincideBusca && coincideStatus;
  });

  // 2. Aplica paginação sobre os dados já filtrados
  const indexUltimo = paginaAtual * itensPorPagina;
  const indexPrimeiro = indexUltimo - itensPorPagina;
  const clientesExibidos = clientesFiltrados.slice(indexPrimeiro, indexUltimo);
  const totalPaginas = Math.ceil(clientesFiltrados.length / itensPorPagina);

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
    actionIcon: { cursor: 'pointer', marginRight: '10px', fontSize: '16px', background: 'none', border: 'none' },
    pagination: { display: 'flex', justifyContent: 'center', gap: '8px', padding: '20px' },
    pageBtn: (isActive) => ({
      padding: '5px 12px', border: '1px solid #ddd', borderRadius: '4px', cursor: 'pointer',
      backgroundColor: isActive ? '#3498db' : '#fff', color: isActive ? '#fff' : '#333', fontWeight: isActive ? 'bold' : 'normal'
    })
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
            placeholder="🔍 Pesquisar por nome, documento ou cidade..."
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

          {/* NOVO: Select dinâmico gerado através do map da requisição */}
          <select style={styles.select} value={cidadeFiltro} onChange={(e) => setCidadeFiltro(e.target.value)}>
            <option value="">Cidade ▼</option>
            {cidadesDisponiveis.map(cidade => (
              <option key={cidade} value={cidade}>{cidade}</option>
            ))}
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
          <div style={{ overflowX: 'auto' }}>
            <table style={styles.table}>
              <thead>
                <tr>
                  <th style={styles.th}>Cliente / Empresa</th>
                  <th style={styles.th}>CPF/CNPJ</th>
                  <th style={styles.th}>Cidade</th>
                  <th style={styles.th}>Telefone</th>
                  <th style={styles.th}>Status</th>
                  <th style={styles.th}>Ações</th>
                </tr>
              </thead>
              <tbody>
                {clientesExibidos.length === 0 ? (
                  <tr>
                    <td colSpan="6" style={{ ...styles.td, textAlign: 'center', color: '#95a5a6' }}>
                      Nenhum cliente encontrado.
                    </td>
                  </tr>
                ) : (
                  clientesExibidos.map((cliente) => (
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
                        <button title="Visualizar" style={styles.actionIcon} onClick={() => navigate(`/clientes/${cliente.id}`)}>👁</button>
                        <button title="Editar" style={styles.actionIcon} onClick={() => navigate(`/clientes/editar/${cliente.id}`)}>✏️</button>
                        <button title="Alternar Status" style={styles.actionIcon} onClick={() => toggleStatus(cliente.id)}>⋮</button>
                      </td>
                    </tr>
                  ))
                )}
              </tbody>
            </table>
          </div>
        )}

        {/* Paginação Dinâmica Renderizada Apenas se houver mais de uma página */}
        {totalPaginas > 1 && (
          <div style={styles.pagination}>
            <button 
              style={styles.pageBtn(false)} 
              disabled={paginaAtual === 1} 
              onClick={() => setPaginaAtual(p => p - 1)}
            >
              ←
            </button>
            
            {Array.from({ length: totalPaginas }, (_, i) => i + 1).map(numero => (
              <button 
                key={numero} 
                style={styles.pageBtn(paginaAtual === numero)}
                onClick={() => setPaginaAtual(numero)}
              >
                {numero}
              </button>
            ))}

            <button 
              style={styles.pageBtn(false)} 
              disabled={paginaAtual === totalPaginas} 
              onClick={() => setPaginaAtual(p => p + 1)}
            >
              →
            </button>
          </div>
        )}
      </div>
    </Layout>
  );
};

export default ClientesList;