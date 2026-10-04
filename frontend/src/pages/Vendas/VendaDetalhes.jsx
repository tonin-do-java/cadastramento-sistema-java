import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import Layout from '../../components/Layout/Layout';

const VendaDetalhes = () => {
  const { id } = useParams();
  const navigate = useNavigate();

  const [venda, setVenda] = useState(null);
  const [loading, setLoading] = useState(true);
  const token = localStorage.getItem('tokenJWT');
  const headers = {
    'Content-Type': 'application/json',
    'Authorization': `Bearer ${token}`
  };

  const carregarVenda = async () => {
    try {
      const res = await fetch(`/api/vendas/${id}`, { headers });
      if (res.ok) {
        setVenda(await res.json());
      }
    } catch (err) {
      console.error('Erro ao buscar venda:', err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    carregarVenda();
  }, [id]);

  const handleAlterarStatus = async (novoStatus) => {
    try {
      const res = await fetch(`/api/vendas/${id}/status?status=${novoStatus}`, {
        method: 'PATCH', 
        headers
      });
      if (res.ok) {
        alert(`Status alterado para ${novoStatus}!`);
        carregarVenda();
      }
    } catch (err) {
      console.error('Erro ao alterar status:', err);
    }
  };

  if (loading) return <Layout><p>Carregando venda...</p></Layout>;
  if (!venda) return <Layout><p>Venda não encontrada.</p></Layout>;

  return (
    <Layout>
      <div style={{ marginBottom: '20px' }}>
        <button onClick={() => navigate('/vendas')} style={{ background: 'none', border: 'none', color: '#3498db', cursor: 'pointer', fontWeight: 'bold' }}>
          ← Voltar
        </button>
        <h1 style={{ margin: '10px 0 0 0', fontSize: '22px', color: '#2c3e50' }}>🛒 VENDA #{String(venda.id).padStart(5, '0')}</h1>
      </div>

      <div style={{ backgroundColor: '#fff', padding: '15px 20px', borderRadius: '8px', boxShadow: '0 2px 4px rgba(0,0,0,0.05)', marginBottom: '20px', display: 'flex', gap: '30px' }}>
        <div><strong>Status:</strong> {venda.status}</div>
        <div><strong>Data:</strong> {new Date(venda.dataHora).toLocaleDateString('pt-BR')}</div>
        <div><strong>Vendedor ID:</strong> #{venda.vendedorId}</div>
        <div><strong>Cliente ID:</strong> #{venda.clienteId}</div>
      </div>

      {/* ITENS DA VENDA */}
      <div style={{ backgroundColor: '#fff', padding: '20px', borderRadius: '8px', boxShadow: '0 2px 4px rgba(0,0,0,0.05)', marginBottom: '20px' }}>
        <h3 style={{ margin: '0 0 15px 0', borderBottom: '1px solid #eee', paddingBottom: '8px' }}>ITENS DA VENDA</h3>
        <table style={{ width: '100%', borderCollapse: 'collapse', textAlign: 'left' }}>
          <thead>
            <tr style={{ backgroundColor: '#f8f9fa', borderBottom: '1px solid #ddd' }}>
              <th style={{ padding: '10px' }}>Produto ID</th>
              <th style={{ padding: '10px' }}>Quantidade</th>
              <th style={{ padding: '10px' }}>Preço Unit.</th>
              <th style={{ padding: '10px' }}>Desconto</th>
              <th style={{ padding: '10px' }}>Total</th>
            </tr>
          </thead>
          <tbody>
            {venda.itens?.map((item) => (
              <tr key={item.id} style={{ borderBottom: '1px solid #eee' }}>
                <td style={{ padding: '10px' }}>Produto #{item.produto?.id || item.produtoId}</td>
                <td style={{ padding: '10px' }}>{item.quantidade}</td>
                <td style={{ padding: '10px' }}>R$ {item.precoUnitario?.toFixed(2)}</td>
                <td style={{ padding: '10px' }}>R$ {item.desconto?.toFixed(2)}</td>
                <td style={{ padding: '10px', fontWeight: 'bold' }}>R$ {item.subtotal?.toFixed(2)}</td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>

      {/* PAGAMENTO E RESUMO */}
      <div style={{ backgroundColor: '#fff', padding: '20px', borderRadius: '8px', boxShadow: '0 2px 4px rgba(0,0,0,0.05)', marginBottom: '20px' }}>
        <h3 style={{ margin: '0 0 15px 0', borderBottom: '1px solid #eee', paddingBottom: '8px' }}>PAGAMENTO</h3>
        <div style={{ display: 'flex', justifyContent: 'space-between' }}>
          <div>
            <p><strong>Forma:</strong> {venda.formaPagamento}</p>
            <p><strong>Observações:</strong> {venda.observacao || 'Nenhuma'}</p>
          </div>
          <div style={{ textAlign: 'right' }}>
            <p>Subtotal: R$ {venda.valorSubtotal?.toFixed(2)}</p>
            <p>Desconto: R$ {venda.desconto?.toFixed(2)}</p>
            <h2 style={{ color: '#27ae60', margin: 0 }}>TOTAL: R$ {venda.valorTotal?.toFixed(2)}</h2>
          </div>
        </div>
      </div>

      {/* AÇÕES */}
      <div style={{ display: 'flex', gap: '10px', justifyContent: 'flex-end' }}>
        <button onClick={() => alert('Integração com módulo Fiscal disponível na V2!')} style={{ padding: '10px 15px', borderRadius: '6px', border: '1px solid #ccc', backgroundColor: '#fff', cursor: 'pointer' }}>
          🧾 Emitir Nota Fiscal
        </button>
        <button onClick={() => window.print()} style={{ padding: '10px 15px', borderRadius: '6px', border: '1px solid #ccc', backgroundColor: '#fff', cursor: 'pointer' }}>
          🖨 Imprimir
        </button>
        <button onClick={() => handleAlterarStatus('CANCELADA')} style={{ padding: '10px 15px', borderRadius: '6px', border: 'none', backgroundColor: '#e74c3c', color: '#fff', cursor: 'pointer' }}>
          Cancelar Venda
        </button>
      </div>
    </Layout>
  );
};

export default VendaDetalhes;