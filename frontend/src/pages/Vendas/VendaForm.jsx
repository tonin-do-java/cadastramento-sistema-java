import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import Layout from '../../components/Layout/Layout';

const VendaForm = () => {
  const navigate = useNavigate();

  // Dados do formulário
  const [clientes, setClientes] = useState([]);
  const [clienteId, setClienteId] = useState('');
  const [formaPagamento, setFormaPagamento] = useState('BOLETO');
  const [descontoGeral, setDescontoGeral] = useState(0);
  const [observacao, setObservacao] = useState('');

  // Itens selecionados no carrinho
  const [itens, setItens] = useState([]);

  // Modal de busca de produtos
  const [modalAberto, setModalAberto] = useState(false);
  const [produtos, setProdutos] = useState([]);
  const [produtoBusca, setProdutoBusca] = useState('');
  const [produtoSelecionado, setProdutoSelecionado] = useState(null);
  const [qtdItem, setQtdItem] = useState(1);
  const [descontoItem, setDescontoItem] = useState(0);
  const token = localStorage.getItem('tokenJWT');
  
  const headers = {
    'Content-Type': 'application/json',
    'Authorization': `Bearer ${token}`
};

  useEffect(() => {
    const carregarDados = async () => {
      try {
        const resClientes = await fetch('/api/clientes', { headers });
        if (resClientes.ok) setClientes(await resClientes.json());

        const resProdutos = await fetch('/api/produtos', { headers });
        if (resProdutos.ok) setProdutos(await resProdutos.json());
      } catch (err) {
        console.error('Erro ao carregar dados auxiliares:', err);
      }
    };
    carregarDados();
  }, []);

  // Adicionar item à lista local
  const handleAdicionarItem = () => {
    if (!produtoSelecionado) return;

    const novoItem = {
      produtoId: produtoSelecionado.id,
      nomeProduto: produtoSelecionado.nome,
      quantidade: parseInt(qtdItem, 10),
      precoUnitario: produtoSelecionado.precoVenda,
      desconto: parseFloat(descontoItem) || 0,
      subtotal: (produtoSelecionado.precoVenda * qtdItem) - (parseFloat(descontoItem) || 0)
    };

    setItens([...itens, novoItem]);
    setModalAberto(false);
    setProdutoSelecionado(null);
    setQtdItem(1);
    setDescontoItem(0);
  };

  const handleRemoverItem = (index) => {
    const novaLista = [...itens];
    novaLista.splice(index, 1);
    setItens(novaLista);
  };

  // Cálculos dinâmicos
  const subtotalVenda = itens.reduce((acc, curr) => acc + curr.subtotal, 0);
  const totalVenda = subtotalVenda - (parseFloat(descontoGeral) || 0);

  // Salvar venda na API
  const handleRegistrarVenda = async () => {
    if (!clienteId) {
      alert('Selecione um cliente!');
      return;
    }
    if (itens.length === 0) {
      alert('Adicione pelo menos um produto na venda!');
      return;
    }

    const vendedorIdSalvo = localStorage.getItem('usuarioId') || 1;

    const payload = {
      clienteId: parseInt(clienteId, 10),
      vendedorId: parseInt(vendedorIdSalvo, 10),
      status: 'PENDENTE',
      formaPagamento: formaPagamento,
      desconto: parseFloat(descontoGeral) || 0,
      observacao: observacao,
      itens: itens.map(i => ({
        produtoId: i.produtoId,
        quantidade: i.quantidade,
        desconto: i.desconto
      }))
    };

    try {
      const res = await fetch('/api/vendas', {
        method: 'POST',
        headers,
        body: JSON.stringify(payload)
      });

      if (res.ok) {
        alert('Venda registrada com sucesso!');
        navigate('/vendas');
      } else {
        alert('Erro ao registrar venda.');
      }
    } catch (err) {
      console.error('Erro de conexão ao salvar venda:', err);
    }
  };

  return (
    <Layout>
      <div style={{ marginBottom: '20px' }}>
        <button
          onClick={() => navigate('/vendas')}
          style={{ background: 'none', border: 'none', color: '#3498db', cursor: 'pointer', fontWeight: 'bold' }}
        >
          ← Voltar para Vendas
        </button>
        <h1 style={{ margin: '10px 0 0 0', fontSize: '22px', color: '#2c3e50' }}>🛒 NOVA VENDA</h1>
      </div>

      {/* DADOS DA VENDA */}
      <div style={{ backgroundColor: '#fff', padding: '20px', borderRadius: '8px', boxShadow: '0 2px 4px rgba(0,0,0,0.05)', marginBottom: '20px' }}>
        <h3 style={{ margin: '0 0 15px 0', fontSize: '16px', color: '#2c3e50', borderBottom: '1px solid #eee', paddingBottom: '8px' }}>DADOS DA VENDA</h3>
        <div style={{ display: 'grid', gridTemplateColumns: '2fr 1fr 1fr', gap: '20px' }}>
          <div>
            <label style={{ display: 'block', fontSize: '13px', fontWeight: 'bold', marginBottom: '5px' }}>Cliente</label>
            <select
              value={clienteId}
              onChange={(e) => setClienteId(e.target.value)}
              style={{ width: '100%', padding: '10px', borderRadius: '6px', border: '1px solid #ccc' }}
            >
              <option value="">Selecione um cliente...</option>
              {clientes.map(c => (
                <option key={c.id} value={c.id}>{c.nome || c.razacaSocial}</option>
              ))}
            </select>
          </div>
          <div>
            <label style={{ display: 'block', fontSize: '13px', fontWeight: 'bold', marginBottom: '5px' }}>Data</label>
            <input
              type="text"
              disabled
              value={new Date().toLocaleDateString('pt-BR')}
              style={{ width: '100%', padding: '10px', borderRadius: '6px', border: '1px solid #ccc', backgroundColor: '#f8f9fa' }}
            />
          </div>
        </div>
      </div>

      {/* PRODUTOS DA VENDA */}
      <div style={{ backgroundColor: '#fff', padding: '20px', borderRadius: '8px', boxShadow: '0 2px 4px rgba(0,0,0,0.05)', marginBottom: '20px' }}>
        <h3 style={{ margin: '0 0 15px 0', fontSize: '16px', color: '#2c3e50', borderBottom: '1px solid #eee', paddingBottom: '8px' }}>PRODUTOS DA VENDA</h3>
        
        <table style={{ width: '100%', borderCollapse: 'collapse', textAlign: 'left', marginBottom: '15px' }}>
          <thead>
            <tr style={{ backgroundColor: '#f8f9fa', borderBottom: '1px solid #ddd' }}>
              <th style={{ padding: '10px' }}>Produto</th>
              <th style={{ padding: '10px' }}>Qtd.</th>
              <th style={{ padding: '10px' }}>Preço Unit.</th>
              <th style={{ padding: '10px' }}>Desconto</th>
              <th style={{ padding: '10px' }}>Subtotal</th>
              <th style={{ padding: '10px', textAlign: 'center' }}>Ações</th>
            </tr>
          </thead>
          <tbody>
            {itens.length === 0 ? (
              <tr>
                <td colSpan="6" style={{ textAlign: 'center', padding: '15px', color: '#7f8c8d' }}>Nenhum produto adicionado.</td>
              </tr>
            ) : (
              itens.map((item, idx) => (
                <tr key={idx} style={{ borderBottom: '1px solid #eee' }}>
                  <td style={{ padding: '10px' }}>{item.nomeProduto}</td>
                  <td style={{ padding: '10px' }}>{item.quantidade}</td>
                  <td style={{ padding: '10px' }}>R$ {item.precoUnitario?.toFixed(2)}</td>
                  <td style={{ padding: '10px' }}>R$ {item.desconto?.toFixed(2)}</td>
                  <td style={{ padding: '10px', fontWeight: 'bold' }}>R$ {item.subtotal?.toFixed(2)}</td>
                  <td style={{ padding: '10px', textAlign: 'center' }}>
                    <button onClick={() => handleRemoverItem(idx)} style={{ background: 'none', border: 'none', cursor: 'pointer', fontSize: '16px' }}>🗑</button>
                  </td>
                </tr>
              ))
            )}
          </tbody>
        </table>

        <button
          onClick={() => setModalAberto(true)}
          style={{ backgroundColor: '#3498db', color: '#fff', border: 'none', padding: '10px 15px', borderRadius: '6px', cursor: 'pointer', fontWeight: 'bold' }}
        >
          + Adicionar Produto
        </button>
      </div>

      {/* PAGAMENTO E VALORES */}
      <div style={{ backgroundColor: '#fff', padding: '20px', borderRadius: '8px', boxShadow: '0 2px 4px rgba(0,0,0,0.05)', marginBottom: '20px' }}>
        <h3 style={{ margin: '0 0 15px 0', fontSize: '16px', color: '#2c3e50', borderBottom: '1px solid #eee', paddingBottom: '8px' }}>PAGAMENTO</h3>
        <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '30px' }}>
          <div>
            <label style={{ display: 'block', fontSize: '13px', fontWeight: 'bold', marginBottom: '5px' }}>Forma de Pagamento</label>
            <select
              value={formaPagamento}
              onChange={(e) => setFormaPagamento(e.target.value)}
              style={{ width: '100%', padding: '10px', borderRadius: '6px', border: '1px solid #ccc', marginBottom: '15px' }}
            >
              <option value="DINHEIRO">Dinheiro</option>
              <option value="PIX">Pix</option>
              <option value="CREDITO">Cartão de Crédito</option>
              <option value="DEBITO">Cartão de Débito</option>
              <option value="BOLETO">Boleto</option>
            </select>

            <label style={{ display: 'block', fontSize: '13px', fontWeight: 'bold', marginBottom: '5px' }}>Observações</label>
            <textarea
              rows="3"
              value={observacao}
              onChange={(e) => setObservacao(e.target.value)}
              style={{ width: '100%', padding: '10px', borderRadius: '6px', border: '1px solid #ccc' }}
            />
          </div>

          <div style={{ backgroundColor: '#f8f9fa', padding: '15px', borderRadius: '6px', display: 'flex', flexDirection: 'column', justifyContent: 'center', gap: '10px' }}>
            <div style={{ display: 'flex', justifyContent: 'space-between' }}>
              <span>Subtotal:</span>
              <strong>R$ {subtotalVenda.toFixed(2)}</strong>
            </div>
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
              <span>Desconto Geral (R$):</span>
              <input
                type="number"
                value={descontoGeral}
                onChange={(e) => setDescontoGeral(e.target.value)}
                style={{ width: '100px', padding: '5px', borderRadius: '4px', border: '1px solid #ccc', textAlign: 'right' }}
              />
            </div>
            <hr style={{ border: 'none', borderTop: '1px solid #ddd', margin: '5px 0' }} />
            <div style={{ display: 'flex', justifyContent: 'space-between', fontSize: '18px', color: '#27ae60' }}>
              <strong>Total:</strong>
              <strong>R$ {totalVenda.toFixed(2)}</strong>
            </div>
          </div>
        </div>
      </div>

      {/* AÇÕES DE SALVAR / CANCELAR */}
      <div style={{ display: 'flex', justifyContent: 'flex-end', gap: '15px' }}>
        <button
          onClick={() => navigate('/vendas')}
          style={{ backgroundColor: '#95a5a6', color: '#fff', border: 'none', padding: '12px 25px', borderRadius: '6px', cursor: 'pointer', fontWeight: 'bold' }}
        >
          Cancelar
        </button>
        <button
          onClick={handleRegistrarVenda}
          style={{ backgroundColor: '#27ae60', color: '#fff', border: 'none', padding: '12px 25px', borderRadius: '6px', cursor: 'pointer', fontWeight: 'bold' }}
        >
          💾 Registrar Venda
        </button>
      </div>

      {/* MODAL ADICIONAR PRODUTO */}
      {modalAberto && (
        <div style={{ position: 'fixed', top: 0, left: 0, right: 0, bottom: 0, backgroundColor: 'rgba(0,0,0,0.5)', display: 'flex', justifyContent: 'center', alignItems: 'center', zIndex: 2000 }}>
          <div style={{ backgroundColor: '#fff', width: '500px', padding: '25px', borderRadius: '8px', boxShadow: '0 4px 10px rgba(0,0,0,0.2)' }}>
            <h3 style={{ marginTop: 0 }}>ADICIONAR PRODUTO</h3>

            <label style={{ fontSize: '12px', fontWeight: 'bold' }}>Buscar Produto</label>
            <input
              type="text"
              placeholder="Digite nome do produto..."
              value={produtoBusca}
              onChange={(e) => setProdutoBusca(e.target.value)}
              style={{ width: '100%', padding: '10px', borderRadius: '6px', border: '1px solid #ccc', marginBottom: '10px' }}
            />

            {/* Lista de Seleção */}
            <div style={{ maxHeight: '150px', overflowY: 'auto', border: '1px solid #eee', borderRadius: '6px', marginBottom: '15px' }}>
              {produtos
                .filter(p => p.nome.toLowerCase().includes(produtoBusca.toLowerCase()))
                .map(p => (
                  <div
                    key={p.id}
                    onClick={() => setProdutoSelecionado(p)}
                    style={{
                      padding: '10px',
                      cursor: 'pointer',
                      borderBottom: '1px solid #eee',
                      backgroundColor: produtoSelecionado?.id === p.id ? '#e3f2fd' : '#fff'
                    }}
                  >
                    <strong>{p.nome}</strong>
                    <div style={{ fontSize: '12px', color: '#666' }}>Estoque: {p.quantidadeEstoque || 0} | Preço: R$ {p.precoVenda?.toFixed(2)}</div>
                  </div>
                ))}
            </div>

            <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '15px', marginBottom: '20px' }}>
              <div>
                <label style={{ fontSize: '12px', fontWeight: 'bold' }}>Quantidade</label>
                <input
                  type="number"
                  min="1"
                  value={qtdItem}
                  onChange={(e) => setQtdItem(e.target.value)}
                  style={{ width: '100%', padding: '8px', borderRadius: '6px', border: '1px solid #ccc' }}
                />
              </div>
              <div>
                <label style={{ fontSize: '12px', fontWeight: 'bold' }}>Desconto Item (R$)</label>
                <input
                  type="number"
                  value={descontoItem}
                  onChange={(e) => setDescontoItem(e.target.value)}
                  style={{ width: '100%', padding: '8px', borderRadius: '6px', border: '1px solid #ccc' }}
                />
              </div>
            </div>

            <div style={{ display: 'flex', justifyContent: 'flex-end', gap: '10px' }}>
              <button onClick={() => setModalAberto(false)} style={{ padding: '10px 15px', borderRadius: '6px', border: 'none', backgroundColor: '#e74c3c', color: '#fff', cursor: 'pointer' }}>Cancelar</button>
              <button onClick={handleAdicionarItem} style={{ padding: '10px 15px', borderRadius: '6px', border: 'none', backgroundColor: '#27ae60', color: '#fff', cursor: 'pointer' }}>Adicionar</button>
            </div>
          </div>
        </div>
      )}
    </Layout>
  );
};

export default VendaForm;