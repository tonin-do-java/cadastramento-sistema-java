import React, { useState, useEffect } from 'react';
import { API_BASE_URL } from '../services/api';
import Layout from '../../pages/Layout/Layout.jsx';

const Dashboard = () => {
  const [produtos, setProdutos] = useState([]);
  const [categorias, setCategorias] = useState([]);
  const [movimentacoes, setMovimentacoes] = useState([]);
  const [usuariosCount, setUsuariosCount] = useState(0);
  const [nomeUsuario, setNomeUsuario] = useState('');
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const nomeSalvo = localStorage.getItem('nomeUsuario'); 
    if (nomeSalvo) {
      setNomeUsuario(nomeSalvo);
    } else {
      setNomeUsuario('Usuário');
    }

    const fetchData = async () => {
      try {
        const token = localStorage.getItem('tokenJWT');

        const headers = {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${token}`
        };

        const resProdutos = await fetch(`${API_BASE_URL}/produto`, { headers });
        if (resProdutos.ok) setProdutos(await resProdutos.json());

        const resCategorias = await fetch(`${API_BASE_URL}/categoria`, { headers });
        if (resCategorias.ok) setCategorias(await resCategorias.json());

        const resMovimentacoes = await fetch(`${API_BASE_URL}/movimentacao`, { headers });
        if (resMovimentacoes.ok) setMovimentacoes(await resMovimentacoes.json());

        const resUsuarios = await fetch(`${API_BASE_URL}/usuarios`, { headers });
         if (resUsuarios.ok) {
            const users = await resUsuarios.json();
            setUsuariosCount(users.length);
         } else {
              setUsuariosCount(6);
         }

      } catch (error) {
        console.error('Erro ao carregar dados do Dashboard:', error);
      } finally {
        setLoading(false);
      }
    };

    fetchData();
  }, []);

  const formatarData = (dataString) => {
    if (!dataString) return '-';
    const dateObj = new Date(dataString);
    if(isNaN(dateObj)) return dataString; 
    return dateObj.toLocaleDateString('pt-BR');
  };

  const produtosEstoqueBaixo = produtos.filter(
    (p) => p.ativo && p.quantidadeAtual <= p.estoqueMinimo
  );

  const ultimasMovimentacoes = movimentacoes.slice(0, 5);

  const ultimosProdutos = [...produtos].reverse().slice(0, 5);

  if (loading) {
    return <div style={{ padding: '50px', textAlign: 'center' }}>Carregando dados do ERP...</div>;
  }

  return (
    <Layout>
      <div style={{ marginBottom: '30px' }}>
        <h1 style={{ margin: '0 0 5px 0', color: '#2c3e50' }}>Olá, {nomeUsuario}! 👋</h1>
        <p style={{ margin: 0, color: '#7f8c8d' }}>Bem-vindo ao Café Caseiro ERP</p>
      </div>

      <div style={{ display: 'flex', gap: '20px', marginBottom: '30px' }}>
        <Card titulo="Produtos Cadastrados" valor={produtos.length} corBarra="#3498db" />
        <Card titulo="Estoque Baixo" valor={produtosEstoqueBaixo.length} corBarra="#e74c3c" />
        <Card titulo="Categorias Ativas" valor={categorias.filter(c => c.ativo).length} corBarra="#f1c40f" />
        <Card titulo="Usuários" valor={usuariosCount} corBarra="#2ecc71" />
      </div>

      <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '20px', marginBottom: '30px' }}>
        <div style={styles.box}>
          <h3 style={styles.boxTitle}>⚠️ Produtos com estoque baixo</h3>
          {produtosEstoqueBaixo.length === 0 ? (
            <p style={{ color: '#7f8c8d', fontSize: '14px' }}>Nenhum produto com estoque baixo.</p>
          ) : (
            <ul style={{ listStyle: 'none', padding: 0, margin: 0 }}>
              {produtosEstoqueBaixo.map(item => (
                <li key={item.id} style={styles.listItem}>
                  <span>• {item.nome}</span>
                  <span style={{ fontWeight: 'bold', color: '#e74c3c' }}>
                    {item.quantidadeAtual} {item.unidadeMedida || 'un'}
                  </span>
                </li>
              ))}
            </ul>
          )}
        </div>

        <div style={styles.box}>
          <h3 style={styles.boxTitle}>🔄 Últimas movimentações</h3>
          {ultimasMovimentacoes.length === 0 ? (
            <p style={{ color: '#7f8c8d', fontSize: '14px' }}>Nenhuma movimentação recente.</p>
          ) : (
            <ul style={{ listStyle: 'none', padding: 0, margin: 0 }}>
              {ultimasMovimentacoes.map(mov => (
                <li key={mov.id} style={styles.listItem}>
                  <span style={{ color: mov.tipoMovimentacao === 'ENTRADA' ? '#2ecc71' : '#e74c3c', fontWeight: 'bold' }}>
                    {mov.tipoMovimentacao === 'ENTRADA' ? 'Entrada' : 'Saída'}
                  </span>
                  <span>- {mov.produto}</span>
                </li>
              ))}
            </ul>
          )}
        </div>
      </div>

      <div style={styles.box}>
        <h3 style={{ ...styles.boxTitle, textAlign: 'center', marginBottom: '20px' }}>Últimos Produtos Cadastrados</h3>
        <table style={{ width: '100%', borderCollapse: 'collapse', textAlign: 'left' }}>
          <thead>
            <tr style={{ borderBottom: '2px solid #ecf0f1', color: '#7f8c8d' }}>
              <th style={styles.th}>Produto</th>
              <th style={styles.th}>Marca</th>
              <th style={styles.th}>Estoque Atual</th>
              <th style={styles.th}>Preço Venda</th>
              <th style={styles.th}>Código</th>
            </tr>
          </thead>
          <tbody>
            {ultimosProdutos.length === 0 ? (
              <tr><td colSpan="5" style={{ textAlign: 'center', padding: '15px', color: '#7f8c8d' }}>Nenhum produto cadastrado.</td></tr>
            ) : (
              ultimosProdutos.map(prod => (
                <tr key={prod.id} style={{ borderBottom: '1px solid #ecf0f1' }}>
                  <td style={styles.td}>{prod.nome}</td>
                  <td style={styles.td}>{prod.marca}</td>
                  <td style={styles.td}>{prod.quantidadeAtual} {prod.unidadeMedida}</td>
                  <td style={styles.td}>R$ {parseFloat(prod.precoVenda).toFixed(2)}</td>
                  <td style={styles.td}>{prod.codigo}</td>
                </tr>
              ))
            )}
          </tbody>
        </table>
      </div>
    </Layout>
  );
};

const Card = ({ titulo, valor, corBarra }) => (
  <div style={{ flex: 1, backgroundColor: '#fff', padding: '20px', borderRadius: '8px', boxShadow: '0 2px 4px rgba(0,0,0,0.05)', borderTop: `4px solid ${corBarra}`, textAlign: 'center' }}>
    <h4 style={{ margin: '0 0 10px 0', color: '#7f8c8d', fontSize: '14px' }}>{titulo}</h4>
    <h2 style={{ margin: 0, color: '#2c3e50', fontSize: '28px' }}>{valor}</h2>
  </div>
);

const styles = {
  box: { backgroundColor: '#fff', padding: '20px', borderRadius: '8px', boxShadow: '0 2px 4px rgba(0,0,0,0.05)' },
  boxTitle: { margin: '0 0 15px 0', color: '#2c3e50', fontSize: '16px', borderBottom: '1px solid #ecf0f1', paddingBottom: '10px' },
  listItem: { display: 'flex', justifyContent: 'space-between', padding: '10px 0', borderBottom: '1px dashed #ecf0f1', fontSize: '14px', color: '#34495e' },
  th: { padding: '12px 10px', fontWeight: 'bold', fontSize: '14px' },
  td: { padding: '12px 10px', color: '#2c3e50', fontSize: '14px' }
};

export default Dashboard;