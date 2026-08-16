import React, { useState, useEffect } from 'react';
import { API_BASE_URL } from '../services/api';
import Layout from '../../pages/Layout/Layout.jsx';
import '../manager.css';

const CategoriaManager = () => {
  const [categorias, setCategorias] = useState([]);
  const [form, setForm] = useState({ nome: '', descricao: '' });
  const [editingId, setEditingId] = useState(null);
  const [mostrarFormulario, setMostrarFormulario] = useState(false);

  const [searchTerm, setSearchTerm] = useState('');
  const [filtroStatus, setFiltroStatus] = useState('');
  const [ordenacao, setOrdenacao] = useState('');
  const [currentPage, setCurrentPage] = useState(1);
  const itemsPerPage = 5;

  const token = localStorage.getItem('tokenJWT');

  const headers = {
    'Content-Type': 'application/json',
    'Authorization': `Bearer ${token}`
  };

  useEffect(() => {
    fetchCategorias();
  }, []);

  const fetchCategorias = async () => {
    try {
      const response = await fetch(`${API_BASE_URL}/categoria`, { headers });
      if (response.ok) {
        const data = await response.json();
        setCategorias(data);
      } else {
        console.error('Erro ao buscar categorias');
      }
    } catch (error) {
      console.error('Erro de conexão:', error);
    }
  };

  let categoriasFiltradas = categorias.filter(c => {
    const matchBusca = c.nome.toLowerCase().includes(searchTerm.toLowerCase()) || c.descricao.toLowerCase().includes(searchTerm.toLowerCase());
    const matchStatus = filtroStatus === 'ativo' ? c.ativo === true : filtroStatus === 'inativo' ? c.ativo === false : true;
    return matchBusca && matchStatus;
  });

  if (ordenacao === 'az') categoriasFiltradas.sort((a, b) => a.nome.localeCompare(b.nome));
  if (ordenacao === 'za') categoriasFiltradas.sort((a, b) => b.nome.localeCompare(a.nome));

  const totalPages = Math.ceil(categoriasFiltradas.length / itemsPerPage);
  const indexUltimaCat = currentPage * itemsPerPage;
  const indexPrimeiraCat = indexUltimaCat - itemsPerPage;
  const categoriasPaginadas = categoriasFiltradas.slice(indexPrimeiraCat, indexUltimaCat);

  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setForm({ ...form, [name]: value });
  };

  const handleAbrirNovo = () => {
    setForm({ nome: '', descricao: '' });
    setEditingId(null);
    setMostrarFormulario(true);
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    
    // Corrigido para utilizar a API_BASE_URL padronizada
    const url = editingId ? `${API_BASE_URL}/categoria/${editingId}` : `${API_BASE_URL}/categoria`;
    const method = editingId ? 'PUT' : 'POST';

    try {
      const response = await fetch(url, {
        method: method,
        headers,
        body: JSON.stringify(form),
      });

      if (response.ok) {
        setForm({ nome: '', descricao: '' });
        setEditingId(null);
        setMostrarFormulario(false);
        fetchCategorias();
      } else {
        console.error('Erro ao salvar categoria');
      }
    } catch (error) {
      console.error('Erro de conexão:', error);
    }
  };

  const handleEdit = (categoria) => {
    setForm({ nome: categoria.nome, descricao: categoria.descricao });
    setEditingId(categoria.id);
    setMostrarFormulario(true);
  };

  const handleCancel = () => {
    setForm({ nome: '', descricao: '' });
    setEditingId(null);
    setMostrarFormulario(false);
  };

  const handleToggleAtividade = async (id) => {
    try {
      const response = await fetch(`${API_BASE_URL}/categoria/${id}/atividade`, {
        headers,
        method: 'PUT'
      });

      if (response.ok) {
        fetchCategorias();
      } else {
        console.error('Erro ao alterar atividade da categoria');
      }
    } catch (error) {
      console.error('Erro de conexão:', error);
    }
  };

  return (
    <Layout>
      <div style={{ marginBottom: '20px' }}>
        <h2 style={{ margin: '0 0 5px 0', color: '#2c3e50' }}>🏷 Categorias</h2>
        <p style={{ margin: 0, color: '#7f8c8d' }}>Gerencie os grupos e classificações de produtos.</p>
      </div>

      {/* BARRA DE AÇÕES E BUSCA */}
      <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: '20px', backgroundColor: '#fff', padding: '15px', borderRadius: '8px', boxShadow: '0 1px 3px rgba(0,0,0,0.1)' }}>
        <div style={{ display: 'flex', gap: '15px', alignItems: 'center' }}>
          <input 
            type="text" 
            placeholder="🔍 Pesquisar categoria..." 
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
            style={{ padding: '8px', width: '300px', borderRadius: '4px', border: '1px solid #ddd' }}
          />
          <select value={filtroStatus} onChange={(e) => setFiltroStatus(e.target.value)} style={{ padding: '8px', borderRadius: '4px', border: '1px solid #ddd' }}>
            <option value="">Status ▼</option>
            <option value="ativo">Ativas</option>
            <option value="inativo">Inativas</option>
          </select>
          <select value={ordenacao} onChange={(e) => setOrdenacao(e.target.value)} style={{ padding: '8px', borderRadius: '4px', border: '1px solid #ddd' }}>
            <option value="">Ordenar ▼</option>
            <option value="az">A - Z</option>
            <option value="za">Z - A</option>
          </select>
        </div>
        
        <button onClick={handleAbrirNovo} style={{ padding: '10px 15px', backgroundColor: '#27ae60', color: '#fff', border: 'none', borderRadius: '4px', cursor: 'pointer', fontWeight: 'bold' }}>
          + Nova Categoria
        </button>
      </div>

      {/* FORMULÁRIO */}
      {mostrarFormulario && (
        <div style={{ backgroundColor: '#fff', padding: '20px', borderRadius: '8px', marginBottom: '20px', boxShadow: '0 1px 3px rgba(0,0,0,0.1)' }}>
          <h3 style={{ marginTop: 0 }}>{editingId ? '✏️ Editar Categoria' : '✨ Nova Categoria'}</h3>
          <form onSubmit={handleSubmit} style={{ display: 'grid', gridTemplateColumns: '1fr', gap: '15px' }}>
            
            <div style={{ display: 'flex', flexDirection: 'column' }}>
              <label style={{ marginBottom: '5px', fontWeight: 'bold', fontSize: '14px' }}>Nome da Categoria:*</label>
              <input type="text" name="nome" value={form.nome} onChange={handleInputChange} required style={{ padding: '8px', border: '1px solid #ddd', borderRadius: '4px' }}/>
            </div>
            
            <div style={{ display: 'flex', flexDirection: 'column' }}>
              <label style={{ marginBottom: '5px', fontWeight: 'bold', fontSize: '14px' }}>Descrição:*</label>
              <input type="text" name="descricao" value={form.descricao} onChange={handleInputChange} required style={{ padding: '8px', border: '1px solid #ddd', borderRadius: '4px' }}/>
            </div>

            <div style={{ display: 'flex', gap: '10px', marginTop: '15px' }}>
              <button type="submit" style={{ padding: '10px 20px', backgroundColor: '#3498db', color: 'white', border: 'none', borderRadius: '4px', cursor: 'pointer', fontWeight: 'bold' }}>{editingId ? 'Salvar Alterações' : 'Criar Categoria'}</button>
              <button type="button" onClick={handleCancel} style={{ padding: '10px 20px', backgroundColor: '#95a5a6', color: 'white', border: 'none', borderRadius: '4px', cursor: 'pointer', fontWeight: 'bold' }}>Cancelar</button>
            </div>
          </form>
        </div>
      )}

      {/* TABELA */}
      <div style={{ backgroundColor: '#fff', borderRadius: '8px', boxShadow: '0 1px 3px rgba(0,0,0,0.1)', overflow: 'hidden' }}>
        <table style={{ width: '100%', borderCollapse: 'collapse', textAlign: 'left' }}>
          <thead style={{ backgroundColor: '#f8f9fa', borderBottom: '2px solid #eee' }}>
            <tr>
              <th style={{ padding: '15px', color: '#2c3e50' }}>ID</th>
              <th style={{ padding: '15px', color: '#2c3e50' }}>Nome</th>
              <th style={{ padding: '15px', color: '#2c3e50' }}>Descrição</th>
              <th style={{ padding: '15px', color: '#2c3e50' }}>Status</th>
              <th style={{ padding: '15px', color: '#2c3e50' }}>Ações</th>
            </tr>
          </thead>
          <tbody>
            {categoriasPaginadas.map(categoria => (
              <tr key={categoria.id} style={{ borderBottom: '1px solid #eee', color: categoria.ativo ? '#333' : '#aaa', backgroundColor: categoria.ativo ? 'transparent' : '#f9f9f9' }}>
                <td style={{ padding: '15px' }}>{categoria.id}</td>
                <td style={{ padding: '15px', fontWeight: 'bold' }}>{categoria.nome}</td>
                <td style={{ padding: '15px' }}>{categoria.descricao}</td>
                <td style={{ padding: '15px' }}>
                  <span style={{ padding: '5px 10px', borderRadius: '20px', fontSize: '12px', fontWeight: 'bold', backgroundColor: categoria.ativo ? '#e8f8f5' : '#fdedec', color: categoria.ativo ? '#27ae60' : '#e74c3c' }}>
                    {categoria.ativo ? 'Ativa' : 'Inativa'}
                  </span>
                </td>
                <td style={{ padding: '15px' }}>
                  <button onClick={() => handleEdit(categoria)} disabled={!categoria.ativo} style={{ background: 'none', border: 'none', cursor: categoria.ativo ? 'pointer' : 'not-allowed', fontSize: '18px', marginRight: '10px', opacity: categoria.ativo ? 1 : 0.5 }} title="Editar">✏️</button>
                  <button onClick={() => handleToggleAtividade(categoria.id)} style={{ background: 'none', border: 'none', cursor: 'pointer', fontSize: '18px' }} title={categoria.ativo ? 'Desativar' : 'Reativar'}>
                    {categoria.ativo ? '🗑' : '♻️'}
                  </button>
                </td>
              </tr>
            ))}
            {categoriasPaginadas.length === 0 && (
              <tr><td colSpan="5" style={{ padding: '20px', textAlign: 'center', color: '#7f8c8d' }}>Nenhuma categoria encontrada.</td></tr>
            )}
          </tbody>
        </table>
        
        {/* PAGINAÇÃO */}
        {totalPages > 1 && (
          <div style={{ padding: '15px', display: 'flex', justifyContent: 'center', gap: '5px', backgroundColor: '#fdfdfd' }}>
            <button disabled={currentPage === 1} onClick={() => setCurrentPage(p => p - 1)} style={{ cursor: currentPage === 1 ? 'not-allowed' : 'pointer', padding: '5px 10px', borderRadius: '4px', border: '1px solid #ddd', backgroundColor: '#fff' }}>«</button>
            {[...Array(totalPages)].map((_, i) => (
              <button 
                key={i+1} 
                onClick={() => setCurrentPage(i+1)}
                style={{ cursor: 'pointer', padding: '5px 10px', borderRadius: '4px', backgroundColor: currentPage === i+1 ? '#3498db' : '#fff', color: currentPage === i+1 ? '#fff' : '#333', border: '1px solid #ddd', fontWeight: currentPage === i+1 ? 'bold' : 'normal' }}
              >
                {i + 1}
              </button>
            ))}
            <button disabled={currentPage === totalPages} onClick={() => setCurrentPage(p => p + 1)} style={{ cursor: currentPage === totalPages ? 'not-allowed' : 'pointer', padding: '5px 10px', borderRadius: '4px', border: '1px solid #ddd', backgroundColor: '#fff' }}>»</button>
          </div>
        )}
      </div>
    </Layout>
  );
};

export default CategoriaManager;