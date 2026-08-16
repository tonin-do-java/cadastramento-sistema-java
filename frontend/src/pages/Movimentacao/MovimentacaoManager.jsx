import React, { useState, useEffect } from 'react';
import { API_BASE_URL } from '../services/api';
import Layout from '../../pages/Layout/Layout.jsx';

const initialFormState = {
  produtoId: '',
  tipo: '',
  origem: '',
  quantidade: '',
  lote: '',
  dataValidade: '',
};

const MovimentacaoManager = () => {
  const [movimentacoes, setMovimentacoes] = useState([]);
  const [produtos, setProdutos] = useState([]);
  const [form, setForm] = useState(initialFormState);
  const token = localStorage.getItem('tokenJWT');

  const headers = {
    'Content-Type': 'application/json',
    'Authorization': `Bearer ${token}`
  };

  useEffect(() => {
    fetchMovimentacoes();
    fetchProdutos();
  }, []);

  const fetchMovimentacoes = async () => {
    try {
      const response = await fetch(`${API_BASE_URL}/movimentacao`, { headers });
      if (response.ok) {
        const data = await response.json();
        setMovimentacoes(data);
      }
    } catch (error) {
      console.error('Erro ao buscar movimentações:', error);
    }
  };

  const fetchProdutos = async () => {
    try {
      const response = await fetch(`${API_BASE_URL}/produto`, { headers });
      if (response.ok) {
        const data = await response.json();
        setProdutos(data.filter(p => p.ativo));
      }
    } catch (error) {
      console.error('Erro ao buscar produtos:', error);
    }
  };

  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setForm({ ...form, [name]: value });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    const produtoSelecionado = produtos.find(p => p.id === parseInt(form.produtoId));

    if (!produtoSelecionado) {
      alert("Selecione um produto válido.");
      return;
    }

    if (form.tipo === 'SAIDA' && parseInt(form.quantidade) > produtoSelecionado.quantidadeAtual) {
      alert(`Saldo insuficiente! Quantidade atual: ${produtoSelecionado.quantidadeAtual}`);
      return;
    }

    if (produtoSelecionado.controlaLote && (!form.lote || form.lote.trim() === '')) {
      alert("O lote é obrigatório para este produto.");
      return;
    }

    if (produtoSelecionado.controlaValidade && !form.dataValidade) {
      alert("A data de validade é obrigatória para este produto.");
      return;
    }

    if (form.dataValidade) {
      const hoje = new Date().setHours(0,0,0,0);
      const dataValidadeForm = new Date(form.dataValidade).getTime();
      
      if (dataValidadeForm < hoje) {
        alert("Não é possível registrar com uma data de validade já vencida.");
        return;
      }
    }

    const payload = {
      produtoId: parseInt(form.produtoId),
      quantidade: parseInt(form.quantidade),
      tipo: form.tipo,
      origem: form.origem,
      lote: form.lote || null,
      dataValidade: form.dataValidade || null
    };

    try {
      const response = await fetch(`${API_BASE_URL}/movimentacao`, {
        method: 'POST',
        headers,
        body: JSON.stringify(payload),
      });

      if (response.ok) {
        setForm(initialFormState);
        fetchMovimentacoes();
        fetchProdutos();
        alert("Movimentação registrada com sucesso!");
      } else {
        const errorData = await response.json().catch(() => null);
        alert(`Erro ao registrar movimentação. ${errorData ? JSON.stringify(errorData) : ''}`);
      }
    } catch (error) {
      console.error('Erro de conexão:', error);
    }
  };

  const formatarDataHora = (dataString) => {
    if (!dataString) return '-';
    const data = new Date(dataString);
    return data.toLocaleString('pt-BR');
  };

  const formatarData = (dataString) => {
    if (!dataString) return '-';
    const [year, month, day] = dataString.split('-');
    return `${day}/${month}/${year}`;
  };

  const produtoSelecionado = produtos.find(p => p.id === parseInt(form.produtoId));
  const exigeLote = produtoSelecionado?.controlaLote || false;
  const exigeValidade = produtoSelecionado?.controlaValidade || false;

  return (
    <Layout>
      <div style={{ marginBottom: '20px' }}>
        <h2 style={{ margin: '0 0 5px 0', color: '#2c3e50' }}>📊 Movimentações de Estoque</h2>
        <p style={{ margin: 0, color: '#7f8c8d' }}>Registre entradas, saídas e consulte o histórico do inventário.</p>
      </div>

      {/* FORMULÁRIO DE NOVA MOVIMENTAÇÃO */}
      <div style={{ backgroundColor: '#fff', padding: '20px', borderRadius: '8px', marginBottom: '20px', boxShadow: '0 1px 3px rgba(0,0,0,0.1)' }}>
        <h3 style={{ marginTop: 0, color: '#2c3e50', borderBottom: '1px solid #eee', paddingBottom: '10px' }}>➕ Registrar Nova Movimentação</h3>
        
        <form onSubmit={handleSubmit} style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(250px, 1fr))', gap: '15px', marginTop: '15px' }}>
          
          <div style={{ display: 'flex', flexDirection: 'column' }}>
            <label style={{ marginBottom: '5px', fontWeight: 'bold', fontSize: '14px' }}>Produto:* </label>
            <select name="produtoId" value={form.produtoId} onChange={handleInputChange} required style={{ padding: '8px', border: '1px solid #ddd', borderRadius: '4px' }}>
              <option value="">Selecione o Produto</option>
              {produtos.map(p => (
                <option key={p.id} value={p.id}>
                  {p.nome} (Estoque Atual: {p.quantidadeAtual})
                </option>
              ))}
            </select>
          </div>

          <div style={{ display: 'flex', flexDirection: 'column' }}>
            <label style={{ marginBottom: '5px', fontWeight: 'bold', fontSize: '14px' }}>Quantidade:* </label>
            <input type="number" name="quantidade" value={form.quantidade} onChange={handleInputChange} required min="1" style={{ padding: '8px', border: '1px solid #ddd', borderRadius: '4px' }}/>
          </div>

          <div style={{ display: 'flex', flexDirection: 'column' }}>
            <label style={{ marginBottom: '5px', fontWeight: 'bold', fontSize: '14px' }}>Tipo de Movimentação:* </label>
            <select name="tipo" value={form.tipo} onChange={handleInputChange} required style={{ padding: '8px', border: '1px solid #ddd', borderRadius: '4px' }}>
              <option value="">Selecione o Tipo</option>
              <option value="ENTRADA">ENTRADA (+)</option>
              <option value="SAIDA">SAÍDA (-)</option>
            </select>
          </div>

          <div style={{ display: 'flex', flexDirection: 'column' }}>
            <label style={{ marginBottom: '5px', fontWeight: 'bold', fontSize: '14px' }}>Origem:* </label>
            <select name="origem" value={form.origem} onChange={handleInputChange} required style={{ padding: '8px', border: '1px solid #ddd', borderRadius: '4px' }}>
              <option value="">Selecione a Origem</option>
              <option value="COMPRA">COMPRA</option>
              <option value="VENDA">VENDA</option>
              <option value="BALANCO_AJUSTE">BALANÇO/AJUSTE</option>
              <option value="DEVOLUCAO_CLIENTE">DEVOLUÇÃO DE CLIENTE</option>
              <option value="USO_INTERNO">USO INTERNO</option>
            </select>
          </div>

          <div style={{ display: 'flex', flexDirection: 'column' }}>
            <label style={{ marginBottom: '5px', fontWeight: 'bold', fontSize: '14px' }}>Lote {exigeLote ? '*' : '(Opcional)'}: </label>
            <input 
              type="text" 
              name="lote" 
              value={form.lote} 
              onChange={handleInputChange} 
              required={exigeLote} 
              disabled={!form.produtoId && !exigeLote}
              style={{ padding: '8px', border: '1px solid #ddd', borderRadius: '4px', backgroundColor: (!form.produtoId && !exigeLote) ? '#f4f6f8' : '#fff' }}
            />
            {exigeLote && <small style={{ color: '#3498db', marginTop: '3px' }}>Obrigatório para este produto.</small>}
          </div>

          <div style={{ display: 'flex', flexDirection: 'column' }}>
            <label style={{ marginBottom: '5px', fontWeight: 'bold', fontSize: '14px' }}>Data de Validade {exigeValidade ? '*' : '(Opcional)'}: </label>
            <input 
              type="date" 
              name="dataValidade" 
              value={form.dataValidade} 
              onChange={handleInputChange} 
              required={exigeValidade}
              disabled={!form.produtoId && !exigeValidade} 
              style={{ padding: '8px', border: '1px solid #ddd', borderRadius: '4px', backgroundColor: (!form.produtoId && !exigeValidade) ? '#f4f6f8' : '#fff' }}
            />
            {exigeValidade && <small style={{ color: '#3498db', marginTop: '3px' }}>Obrigatório para este produto.</small>}
          </div>

          <div style={{ gridColumn: '1 / -1', marginTop: '10px' }}>
            <button type="submit" style={{ padding: '10px 20px', backgroundColor: '#3498db', color: 'white', border: 'none', borderRadius: '4px', cursor: 'pointer', fontWeight: 'bold' }}>
              Registrar Movimentação
            </button>
          </div>
        </form>
      </div>

      {/* TABELA DE HISTÓRICO */}
      <div style={{ backgroundColor: '#fff', borderRadius: '8px', boxShadow: '0 1px 3px rgba(0,0,0,0.1)', overflow: 'hidden' }}>
        <div style={{ padding: '20px', borderBottom: '2px solid #eee', backgroundColor: '#f8f9fa' }}>
          <h3 style={{ margin: 0, color: '#2c3e50' }}>📜 Histórico de Movimentações</h3>
        </div>
        
        <table style={{ width: '100%', borderCollapse: 'collapse', textAlign: 'left' }}>
          <thead style={{ backgroundColor: '#f8f9fa', borderBottom: '2px solid #eee' }}>
            <tr>
              <th style={{ padding: '15px', color: '#2c3e50' }}>ID</th>
              <th style={{ padding: '15px', color: '#2c3e50' }}>Data e Hora</th>
              <th style={{ padding: '15px', color: '#2c3e50' }}>Produto</th>
              <th style={{ padding: '15px', color: '#2c3e50' }}>Usuário</th>
              <th style={{ padding: '15px', color: '#2c3e50' }}>Tipo</th>
              <th style={{ padding: '15px', color: '#2c3e50' }}>Origem</th>
              <th style={{ padding: '15px', color: '#2c3e50' }}>Quantidade</th>
              <th style={{ padding: '15px', color: '#2c3e50' }}>Lote</th>
              <th style={{ padding: '15px', color: '#2c3e50' }}>Validade</th>
            </tr>
          </thead>
          <tbody>
            {movimentacoes.map((mov) => (
              <tr key={mov.id} style={{ borderBottom: '1px solid #eee' }}>
                <td style={{ padding: '15px' }}>{mov.id}</td>
                <td style={{ padding: '15px' }}>{formatarDataHora(mov.dataHora)}</td>
                <td style={{ padding: '15px', fontWeight: 'bold' }}>{mov.produto}</td>
                <td style={{ padding: '15px' }}>{mov.usuario}</td>
                <td style={{ padding: '15px' }}>
                  <span style={{ 
                    padding: '5px 10px', 
                    borderRadius: '20px', 
                    fontSize: '12px', 
                    fontWeight: 'bold', 
                    backgroundColor: mov.tipoMovimentacao === 'ENTRADA' ? '#e8f8f5' : '#fdedec', 
                    color: mov.tipoMovimentacao === 'ENTRADA' ? '#27ae60' : '#e74c3c' 
                  }}>
                    {mov.tipoMovimentacao}
                  </span>
                </td>
                <td style={{ padding: '15px' }}>{mov.origemMovimentacao.replace('_', ' ')}</td>
                <td style={{ padding: '15px', fontWeight: 'bold', color: mov.tipoMovimentacao === 'ENTRADA' ? '#27ae60' : '#e74c3c' }}>
                  {mov.tipoMovimentacao === 'ENTRADA' ? '+' : '-'}{mov.quantidade}
                </td>
                <td style={{ padding: '15px' }}>{mov.lote || '-'}</td>
                <td style={{ padding: '15px' }}>{formatarData(mov.validade)}</td>
              </tr>
            ))}
            {movimentacoes.length === 0 && (
              <tr>
                <td colSpan="9" style={{ padding: '20px', textAlign: 'center', color: '#7f8c8d' }}>Nenhuma movimentação registrada no histórico.</td>
              </tr>
            )}
          </tbody>
        </table>
      </div>
    </Layout>
  );
};

export default MovimentacaoManager;