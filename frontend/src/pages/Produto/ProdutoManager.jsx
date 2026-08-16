import React, { useState, useEffect } from 'react';
import { API_BASE_URL } from '../services/api';
import Layout from '../../pages/Layout/Layout.jsx';

const initialFormState = {
  categoriaId: '',
  codigo: '',
  nome: '',
  descricao: '',
  marca: '',
  unidadeMedida: '',
  precoCusto: '',
  precoVenda: '',
  estoqueMinimo: '',
  estoqueMaximo: '',
  quantidadeAtual: '',
  controlaLote: false,
  controlaValidade: false,
};

const ProdutoManager = () => {
  const [produtos, setProdutos] = useState([]);
  const [categorias, setCategorias] = useState([]);
  const [form, setForm] = useState(initialFormState);
  const [editingId, setEditingId] = useState(null);

  const [mostrarFormulario, setMostrarFormulario] = useState(false);

  const [searchTerm, setSearchTerm] = useState('');
  const [filtroCategoria, setFiltroCategoria] = useState('');
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
    fetchProdutos();
    fetchCategorias();
  }, []);

  const fetchProdutos = async () => {
    try {
      const response = await fetch(`${API_BASE_URL}/produto`, { headers });
      if (response.ok) {
        const data = await response.json();
        setProdutos(data);
      }
    } catch (error) {
      console.error('Erro ao buscar produtos:', error);
    }
  };

  const fetchCategorias = async () => {
    try {
      const response = await fetch(`${API_BASE_URL}/categoria`, { headers });
      if (response.ok) {
        const data = await response.json();
        setCategorias(data.filter(cat => cat.ativo));
      }
    } catch (error) {
      console.error('Erro ao buscar categorias:', error);
    }
  };

  let produtosFiltrados = produtos.filter(p => {
    const matchBusca = p.nome.toLowerCase().includes(searchTerm.toLowerCase()) || p.codigo.includes(searchTerm);
    const matchCategoria = filtroCategoria ? p.categoriaId === parseInt(filtroCategoria) : true;
    const matchStatus = filtroStatus === 'ativo' ? p.ativo === true : filtroStatus === 'inativo' ? p.ativo === false : true;
    return matchBusca && matchCategoria && matchStatus;
  });

  if (ordenacao === 'az') produtosFiltrados.sort((a, b) => a.nome.localeCompare(b.nome));
  if (ordenacao === 'estoqueDesc') produtosFiltrados.sort((a, b) => b.quantidadeAtual - a.quantidadeAtual);
  if (ordenacao === 'estoqueAsc') produtosFiltrados.sort((a, b) => a.quantidadeAtual - b.quantidadeAtual);

  const totalPages = Math.ceil(produtosFiltrados.length / itemsPerPage);
  const indexUltimoProduto = currentPage * itemsPerPage;
  const indexPrimeiroProduto = indexUltimoProduto - itemsPerPage;
  const produtosPaginados = produtosFiltrados.slice(indexPrimeiroProduto, indexUltimoProduto);

  const getCategoriaNome = (id) => {
    const cat = categorias.find(c => c.id === id);
    return cat ? cat.nome : 'Desconhecida';
  };

  const handleInputChange = (e) => {
    const { name, value, type, checked } = e.target;
    setForm({ ...form, [name]: type === 'checkbox' ? checked : value });
  };

  const handleAbrirNovo = () => {
    setForm(initialFormState);
    setEditingId(null);
    setMostrarFormulario(true);
  };

  const handleEdit = (produto) => {
    setForm({ ...produto });
    setEditingId(produto.id);
    setMostrarFormulario(true);
  };

  const handleCancel = () => {
    setForm(initialFormState);
    setEditingId(null);
    setMostrarFormulario(false);
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (parseFloat(form.precoVenda) <= parseFloat(form.precoCusto)) {
      alert("O preço de venda deve ser maior que o preço de custo!");
      return;
    }
    
    // Preparando o payload para a requisição de acordo com o RequestDto[cite: 20, 25]
    const payload = {
      ...form,
      categoriaId: parseInt(form.categoriaId),
      precoCusto: parseFloat(form.precoCusto),
      precoVenda: parseFloat(form.precoVenda),
      estoqueMinimo: parseInt(form.estoqueMinimo),
      estoqueMaximo: parseInt(form.estoqueMaximo),
      quantidadeAtual: parseInt(form.quantidadeAtual),
    };

    const url = editingId ? `${API_BASE_URL}/produto/${editingId}` : `${API_BASE_URL}/produto`;
    const method = editingId ? 'PUT' : 'POST';

    try {
      const response = await fetch(url, {
        method: method,
        headers,
        body: JSON.stringify(payload),
      });

      if (response.ok) {
        handleCancel();
        fetchProdutos();
      } else {
        alert("Erro ao salvar produto.");
      }
    } catch (error) {
      console.error('Erro de conexão:', error);
    }
  };

  const handleToggleAtividade = async (id) => {
    try {
      const response = await fetch(`${API_BASE_URL}/produto/${id}/atividade`, { headers, method: 'PUT' });
      if (response.ok) fetchProdutos();
    } catch (error) {
      console.error('Erro de conexão:', error);
    }
  };

  return (
    <Layout>
      <div style={{ marginBottom: '20px' }}>
        <h2 style={{ margin: '0 0 5px 0', color: '#2c3e50' }}>📦 Produtos</h2>
        <p style={{ margin: 0, color: '#7f8c8d' }}>Gerencie todos os produtos cadastrados.</p>
      </div>

      <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: '20px', backgroundColor: '#fff', padding: '15px', borderRadius: '8px', boxShadow: '0 1px 3px rgba(0,0,0,0.1)' }}>
        <div style={{ display: 'flex', gap: '15px', alignItems: 'center', flexWrap: 'wrap' }}>
          <input 
            type="text" 
            placeholder="🔍 Pesquisar produto (Nome ou Código)..." 
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
            style={{ padding: '8px', width: '300px', borderRadius: '4px', border: '1px solid #ddd' }}
          />
          <select value={filtroCategoria} onChange={(e) => setFiltroCategoria(e.target.value)} style={{ padding: '8px', borderRadius: '4px', border: '1px solid #ddd' }}>
            <option value="">Filtro: Categoria ▼</option>
            {categorias.map(cat => <option key={cat.id} value={cat.id}>{cat.nome}</option>)}
          </select>
          <select value={filtroStatus} onChange={(e) => setFiltroStatus(e.target.value)} style={{ padding: '8px', borderRadius: '4px', border: '1px solid #ddd' }}>
            <option value="">Status ▼</option>
            <option value="ativo">Ativos</option>
            <option value="inativo">Inativos</option>
          </select>
          <select value={ordenacao} onChange={(e) => setOrdenacao(e.target.value)} style={{ padding: '8px', borderRadius: '4px', border: '1px solid #ddd' }}>
            <option value="">Ordenar ▼</option>
            <option value="az">A - Z</option>
            <option value="estoqueDesc">Maior Estoque</option>
            <option value="estoqueAsc">Menor Estoque</option>
          </select>
        </div>
        
        <button onClick={handleAbrirNovo} style={{ padding: '10px 15px', backgroundColor: '#27ae60', color: '#fff', border: 'none', borderRadius: '4px', cursor: 'pointer', fontWeight: 'bold', whiteSpace: 'nowrap' }}>
          + Novo Produto
        </button>
      </div>

      {mostrarFormulario && (
        <div style={{ backgroundColor: '#fff', padding: '20px', borderRadius: '8px', marginBottom: '20px', boxShadow: '0 1px 3px rgba(0,0,0,0.1)' }}>
          <h3 style={{ marginTop: 0, color: '#2c3e50' }}>{editingId ? '✏️ Editar Produto' : '✨ Novo Produto'}</h3>
          <form onSubmit={handleSubmit} style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(200px, 1fr))', gap: '15px' }}>
            
            <div style={{ display: 'flex', flexDirection: 'column' }}>
              <label style={{ marginBottom: '5px', fontWeight: 'bold', fontSize: '14px' }}>Categoria:*</label>
              <select name="categoriaId" value={form.categoriaId} onChange={handleInputChange} required style={{ padding: '8px', border: '1px solid #ddd', borderRadius: '4px' }}>
                <option value="">Selecione...</option>
                {categorias.map(cat => <option key={cat.id} value={cat.id}>{cat.nome}</option>)}
              </select>
            </div>
            
            <div style={{ display: 'flex', flexDirection: 'column' }}><label style={{ marginBottom: '5px', fontWeight: 'bold', fontSize: '14px' }}>Código:*</label><input type="text" name="codigo" value={form.codigo} onChange={handleInputChange} required style={{ padding: '8px', border: '1px solid #ddd', borderRadius: '4px' }}/></div>
            <div style={{ display: 'flex', flexDirection: 'column' }}><label style={{ marginBottom: '5px', fontWeight: 'bold', fontSize: '14px' }}>Nome:*</label><input type="text" name="nome" value={form.nome} onChange={handleInputChange} required style={{ padding: '8px', border: '1px solid #ddd', borderRadius: '4px' }}/></div>
            <div style={{ display: 'flex', flexDirection: 'column' }}><label style={{ marginBottom: '5px', fontWeight: 'bold', fontSize: '14px' }}>Marca:*</label><input type="text" name="marca" value={form.marca} onChange={handleInputChange} required style={{ padding: '8px', border: '1px solid #ddd', borderRadius: '4px' }}/></div>
            <div style={{ display: 'flex', flexDirection: 'column' }}><label style={{ marginBottom: '5px', fontWeight: 'bold', fontSize: '14px' }}>Descrição:*</label><input type="text" name="descricao" value={form.descricao} onChange={handleInputChange} required style={{ padding: '8px', border: '1px solid #ddd', borderRadius: '4px' }}/></div>
            <div style={{ display: 'flex', flexDirection: 'column' }}><label style={{ marginBottom: '5px', fontWeight: 'bold', fontSize: '14px' }}>Und. Medida:*</label><input type="text" name="unidadeMedida" value={form.unidadeMedida} onChange={handleInputChange} required style={{ padding: '8px', border: '1px solid #ddd', borderRadius: '4px' }}/></div>
            <div style={{ display: 'flex', flexDirection: 'column' }}><label style={{ marginBottom: '5px', fontWeight: 'bold', fontSize: '14px' }}>Custo (R$):*</label><input type="number" step="0.01" name="precoCusto" value={form.precoCusto} onChange={handleInputChange} required style={{ padding: '8px', border: '1px solid #ddd', borderRadius: '4px' }}/></div>
            <div style={{ display: 'flex', flexDirection: 'column' }}><label style={{ marginBottom: '5px', fontWeight: 'bold', fontSize: '14px' }}>Venda (R$):*</label><input type="number" step="0.01" name="precoVenda" value={form.precoVenda} onChange={handleInputChange} required style={{ padding: '8px', border: '1px solid #ddd', borderRadius: '4px' }}/></div>
            <div style={{ display: 'flex', flexDirection: 'column' }}><label style={{ marginBottom: '5px', fontWeight: 'bold', fontSize: '14px' }}>Estoque Mín:*</label><input type="number" name="estoqueMinimo" value={form.estoqueMinimo} onChange={handleInputChange} required style={{ padding: '8px', border: '1px solid #ddd', borderRadius: '4px' }}/></div>
            <div style={{ display: 'flex', flexDirection: 'column' }}><label style={{ marginBottom: '5px', fontWeight: 'bold', fontSize: '14px' }}>Estoque Máx:*</label><input type="number" name="estoqueMaximo" value={form.estoqueMaximo} onChange={handleInputChange} required style={{ padding: '8px', border: '1px solid #ddd', borderRadius: '4px' }}/></div>
            <div style={{ display: 'flex', flexDirection: 'column' }}><label style={{ marginBottom: '5px', fontWeight: 'bold', fontSize: '14px' }}>Qtd. Atual:*</label><input type="number" name="quantidadeAtual" value={form.quantidadeAtual} onChange={handleInputChange} required style={{ padding: '8px', border: '1px solid #ddd', borderRadius: '4px' }}/></div>

            <div style={{ gridColumn: '1 / -1', display: 'flex', gap: '20px', marginTop: '10px' }}>
              <label style={{ display: 'flex', alignItems: 'center', gap: '5px', cursor: 'pointer' }}><input type="checkbox" name="controlaLote" checked={form.controlaLote} onChange={handleInputChange} /> Controla Lote</label>
              <label style={{ display: 'flex', alignItems: 'center', gap: '5px', cursor: 'pointer' }}><input type="checkbox" name="controlaValidade" checked={form.controlaValidade} onChange={handleInputChange} /> Controla Validade</label>
            </div>

            <div style={{ gridColumn: '1 / -1', display: 'flex', gap: '10px', marginTop: '15px' }}>
              <button type="submit" style={{ padding: '10px 20px', backgroundColor: '#3498db', color: 'white', border: 'none', borderRadius: '4px', cursor: 'pointer', fontWeight: 'bold' }}>{editingId ? 'Salvar Alterações' : 'Criar Produto'}</button>
              <button type="button" onClick={handleCancel} style={{ padding: '10px 20px', backgroundColor: '#95a5a6', color: 'white', border: 'none', borderRadius: '4px', cursor: 'pointer', fontWeight: 'bold' }}>Cancelar</button>
            </div>
          </form>
        </div>
      )}

      <div style={{ backgroundColor: '#fff', borderRadius: '8px', boxShadow: '0 1px 3px rgba(0,0,0,0.1)', overflow: 'hidden' }}>
        <table style={{ width: '100%', borderCollapse: 'collapse', textAlign: 'left' }}>
          <thead style={{ backgroundColor: '#f8f9fa', borderBottom: '2px solid #eee' }}>
            <tr>
              <th style={{ padding: '15px', color: '#2c3e50' }}>Produto</th>
              <th style={{ padding: '15px', color: '#2c3e50' }}>Categoria</th>
              <th style={{ padding: '15px', color: '#2c3e50' }}>Estoque</th>
              <th style={{ padding: '15px', color: '#2c3e50' }}>Mínimo</th>
              <th style={{ padding: '15px', color: '#2c3e50' }}>Código</th>
              <th style={{ padding: '15px', color: '#2c3e50' }}>Ações</th>
            </tr>
          </thead>
          <tbody>
            {produtosPaginados.map(produto => (
              <tr key={produto.id} style={{ borderBottom: '1px solid #eee', color: produto.ativo ? '#333' : '#aaa', backgroundColor: produto.ativo ? 'transparent' : '#f9f9f9' }}>
                <td style={{ padding: '15px', fontWeight: 'bold' }}>{produto.nome}</td>
                <td style={{ padding: '15px' }}>{getCategoriaNome(produto.categoriaId)}</td>
                <td style={{ padding: '15px' }}>
                  <span style={{ color: produto.quantidadeAtual <= produto.estoqueMinimo ? '#e74c3c' : 'inherit', fontWeight: produto.quantidadeAtual <= produto.estoqueMinimo ? 'bold' : 'normal' }}>
                    {produto.quantidadeAtual} {produto.unidadeMedida}
                  </span>
                </td>
                <td style={{ padding: '15px' }}>{produto.estoqueMinimo}</td>
                <td style={{ padding: '15px' }}>{produto.codigo}</td>
                <td style={{ padding: '15px' }}>
                  <button onClick={() => handleEdit(produto)} style={{ background: 'none', border: 'none', cursor: 'pointer', fontSize: '18px', marginRight: '10px' }} title="Editar">✏️</button>
                  <button onClick={() => handleToggleAtividade(produto.id)} style={{ background: 'none', border: 'none', cursor: 'pointer', fontSize: '18px' }} title={produto.ativo ? 'Desativar' : 'Reativar'}>
                    {produto.ativo ? '🗑' : '♻️'}
                  </button>
                </td>
              </tr>
            ))}
            {produtosPaginados.length === 0 && (
              <tr><td colSpan="6" style={{ padding: '20px', textAlign: 'center', color: '#7f8c8d' }}>Nenhum produto encontrado.</td></tr>
            )}
          </tbody>
        </table>
        
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

export default ProdutoManager;