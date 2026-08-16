import React, { useState, useEffect } from 'react';
import { API_BASE_URL } from '../services/api';
import Layout from '../../pages/Layout/Layout.jsx';''

const UsuarioManager = () => {
  const [usuarios, setUsuarios] = useState([]);
  const [buscaNome, setBuscaNome] = useState('');
  const [mostrarFormulario, setMostrarFormulario] = useState(false);
  
  const [formData, setFormData] = useState({
    nome: '',
    email: '',
    senha: '',
    confirmacaoSenha: '',
    role: 'ESTOQUISTA' 
  });
  
  const [editandoId, setEditandoId] = useState(null);
  const [mensagem, setMensagem] = useState({ texto: '', tipo: '' });

  const API_URL = `${API_BASE_URL}/usuarios`; // Utilizando a constante base
  const token = localStorage.getItem('tokenJWT'); 

  const authHeaders = {
    'Content-Type': 'application/json',
    'Authorization': `Bearer ${token}`
  };

  useEffect(() => {
    carregarUsuarios();
  }, []);

  const carregarUsuarios = async () => {
    try {
      const response = await fetch(API_URL, { headers: authHeaders });
      if (response.ok) {
        const data = await response.json();
        setUsuarios(data);
      } else {
        mostrarMensagem('Sem permissão para carregar usuários.', 'erro');
      }
    } catch (error) {
      mostrarMensagem('Erro ao carregar usuários.', 'erro');
    }
  };

  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setFormData({ ...formData, [name]: value });
  };

  const mostrarMensagem = (texto, tipo) => {
    setMensagem({ texto, tipo });
    setTimeout(() => setMensagem({ texto: '', tipo: '' }), 4000);
  };

  const handleAbrirNovo = () => {
    setFormData({ nome: '', email: '', senha: '', confirmacaoSenha: '', role: 'ESTOQUISTA' });
    setEditandoId(null);
    setMostrarFormulario(true);
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    
    if (!editandoId && formData.senha !== formData.confirmacaoSenha) {
      mostrarMensagem('As senhas não coincidem.', 'erro');
      return;
    }

    try {
      const url = editandoId ? `${API_URL}/${editandoId}` : API_URL;
      const metodo = editandoId ? 'PUT' : 'POST';
      
      const payload = editandoId 
        ? { nome: formData.nome, email: formData.email, role: formData.role }
        : { ...formData };

      const response = await fetch(url, {
        method: metodo,
        headers: authHeaders, 
        body: JSON.stringify(payload),
      });

      if (response.ok) {
        mostrarMensagem(`Usuário ${editandoId ? 'atualizado' : 'criado'} com sucesso!`, 'sucesso');
        setFormData({ nome: '', email: '', senha: '', confirmacaoSenha: '', role: 'ESTOQUISTA' });
        setEditandoId(null);
        setMostrarFormulario(false);
        carregarUsuarios();
      } else {
        mostrarMensagem('Erro ao salvar o usuário. Verifique os dados.', 'erro');
      }
    } catch (error) {
      mostrarMensagem('Erro de conexão com o servidor.', 'erro');
    }
  };

  const editarUsuario = (usuario) => {
    setEditandoId(usuario.id);
    setFormData({
      nome: usuario.nome,
      email: usuario.email,
      senha: '',
      confirmacaoSenha: '',
      role: usuario.role 
    });
    setMostrarFormulario(true);
  };

  const cancelarEdicao = () => {
    setEditandoId(null);
    setFormData({ nome: '', email: '', senha: '', confirmacaoSenha: '', role: 'ESTOQUISTA' });
    setMostrarFormulario(false);
  };

  const deletarUsuario = async (id) => {
    if (!window.confirm('Tem certeza que deseja deletar este usuário?')) return;

    try {
      const response = await fetch(`${API_URL}/${id}`, {
        method: 'DELETE',
        headers: authHeaders,
      });

      if (response.ok) {
        mostrarMensagem('Usuário deletado com sucesso!', 'sucesso');
        carregarUsuarios();
      } else {
        mostrarMensagem('Erro ao deletar o usuário.', 'erro');
      }
    } catch (error) {
      mostrarMensagem('Erro de conexão com o servidor.', 'erro');
    }
  };

  const usuariosFiltrados = usuarios.filter((usuario) =>
    usuario.nome.toLowerCase().includes(buscaNome.toLowerCase())
  );

  return (
    <Layout>
      <div style={{ marginBottom: '20px' }}>
        <h2 style={{ margin: '0 0 5px 0', color: '#2c3e50' }}>👥 Gerenciamento de Usuários</h2>
        <p style={{ margin: 0, color: '#7f8c8d' }}>Cadastre, edite e gerencie os níveis de acesso da equipe.</p>
      </div>

      {/* MENSAGEM DE ALERTA */}
      {mensagem.texto && (
        <div style={{ 
            padding: '15px', 
            marginBottom: '20px', 
            borderRadius: '4px', 
            color: '#fff', 
            fontWeight: 'bold',
            boxShadow: '0 1px 3px rgba(0,0,0,0.1)',
            backgroundColor: mensagem.tipo === 'erro' ? '#e74c3c' : '#27ae60' 
        }}>
          {mensagem.texto}
        </div>
      )}

      {/* BARRA DE PESQUISA E AÇÃO */}
      <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: '20px', backgroundColor: '#fff', padding: '15px', borderRadius: '8px', boxShadow: '0 1px 3px rgba(0,0,0,0.1)' }}>
        <div style={{ display: 'flex', gap: '15px', alignItems: 'center' }}>
          <input 
            type="text" 
            placeholder="🔍 Pesquisar usuário por nome..." 
            value={buscaNome} 
            onChange={(e) => setBuscaNome(e.target.value)}
            style={{ padding: '8px', width: '300px', borderRadius: '4px', border: '1px solid #ddd' }}
          />
        </div>
        
        <button onClick={handleAbrirNovo} style={{ padding: '10px 15px', backgroundColor: '#27ae60', color: '#fff', border: 'none', borderRadius: '4px', cursor: 'pointer', fontWeight: 'bold' }}>
          + Novo Usuário
        </button>
      </div>

      {/* FORMULÁRIO DE CRIAÇÃO/EDIÇÃO */}
      {mostrarFormulario && (
        <div style={{ backgroundColor: '#fff', padding: '20px', borderRadius: '8px', marginBottom: '20px', boxShadow: '0 1px 3px rgba(0,0,0,0.1)' }}>
          <h3 style={{ marginTop: 0, color: '#2c3e50', borderBottom: '1px solid #eee', paddingBottom: '10px' }}>
            {editandoId ? '✏️ Editar Usuário' : '✨ Novo Usuário'}
          </h3>
          
          <form onSubmit={handleSubmit} style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(250px, 1fr))', gap: '15px', marginTop: '15px' }}>
            
            <div style={{ display: 'flex', flexDirection: 'column' }}>
              <label style={{ marginBottom: '5px', fontWeight: 'bold', fontSize: '14px' }}>Nome:*</label>
              <input type="text" name="nome" value={formData.nome} onChange={handleInputChange} required style={{ padding: '8px', border: '1px solid #ddd', borderRadius: '4px' }}/>
            </div>

            <div style={{ display: 'flex', flexDirection: 'column' }}>
              <label style={{ marginBottom: '5px', fontWeight: 'bold', fontSize: '14px' }}>Email:*</label>
              <input type="email" name="email" value={formData.email} onChange={handleInputChange} required style={{ padding: '8px', border: '1px solid #ddd', borderRadius: '4px' }}/>
            </div>

            <div style={{ display: 'flex', flexDirection: 'column' }}>
              <label style={{ marginBottom: '5px', fontWeight: 'bold', fontSize: '14px' }}>Nível de Acesso:*</label>
              <select name="role" value={formData.role} onChange={handleInputChange} style={{ padding: '8px', border: '1px solid #ddd', borderRadius: '4px' }}>
                <option value="ESTOQUISTA">Estoquista</option>
                <option value="ADMIN">Administrador</option>
              </select>
            </div>

            {!editandoId && (
              <>
                <div style={{ display: 'flex', flexDirection: 'column' }}>
                  <label style={{ marginBottom: '5px', fontWeight: 'bold', fontSize: '14px' }}>Senha:*</label>
                  <input type="password" name="senha" value={formData.senha} onChange={handleInputChange} required minLength={8} style={{ padding: '8px', border: '1px solid #ddd', borderRadius: '4px' }}/>
                </div>

                <div style={{ display: 'flex', flexDirection: 'column' }}>
                  <label style={{ marginBottom: '5px', fontWeight: 'bold', fontSize: '14px' }}>Confirmação de Senha:*</label>
                  <input type="password" name="confirmacaoSenha" value={formData.confirmacaoSenha} onChange={handleInputChange} required minLength={8} style={{ padding: '8px', border: '1px solid #ddd', borderRadius: '4px' }}/>
                </div>
              </>
            )}

            <div style={{ gridColumn: '1 / -1', display: 'flex', gap: '10px', marginTop: '10px' }}>
              <button type="submit" style={{ padding: '10px 20px', backgroundColor: '#3498db', color: 'white', border: 'none', borderRadius: '4px', cursor: 'pointer', fontWeight: 'bold' }}>
                {editandoId ? 'Salvar Alterações' : 'Cadastrar'}
              </button>
              <button type="button" onClick={cancelarEdicao} style={{ padding: '10px 20px', backgroundColor: '#95a5a6', color: 'white', border: 'none', borderRadius: '4px', cursor: 'pointer', fontWeight: 'bold' }}>
                Cancelar
              </button>
            </div>
          </form>
        </div>
      )}

      {/* TABELA DE USUÁRIOS */}
      <div style={{ backgroundColor: '#fff', borderRadius: '8px', boxShadow: '0 1px 3px rgba(0,0,0,0.1)', overflow: 'hidden' }}>
        <table style={{ width: '100%', borderCollapse: 'collapse', textAlign: 'left' }}>
          <thead style={{ backgroundColor: '#f8f9fa', borderBottom: '2px solid #eee' }}>
            <tr>
              <th style={{ padding: '15px', color: '#2c3e50' }}>ID</th>
              <th style={{ padding: '15px', color: '#2c3e50' }}>Nome</th>
              <th style={{ padding: '15px', color: '#2c3e50' }}>Email</th>
              <th style={{ padding: '15px', color: '#2c3e50' }}>Role</th>
              <th style={{ padding: '15px', color: '#2c3e50' }}>Ações</th>
            </tr>
          </thead>
          <tbody>
            {usuariosFiltrados.length > 0 ? (
              usuariosFiltrados.map((usuario) => (
                <tr key={usuario.id} style={{ borderBottom: '1px solid #eee' }}>
                  <td style={{ padding: '15px' }}>{usuario.id}</td>
                  <td style={{ padding: '15px', fontWeight: 'bold' }}>{usuario.nome}</td>
                  <td style={{ padding: '15px' }}>{usuario.email}</td>
                  <td style={{ padding: '15px' }}>
                    <span style={{ 
                      padding: '5px 10px', 
                      borderRadius: '20px', 
                      fontSize: '12px', 
                      fontWeight: 'bold', 
                      backgroundColor: usuario.role === 'ADMIN' ? '#fdedec' : '#e8f8f5', 
                      color: usuario.role === 'ADMIN' ? '#e74c3c' : '#27ae60' 
                    }}>
                      {usuario.role}
                    </span>
                  </td>
                  <td style={{ padding: '15px' }}>
                    <button onClick={() => editarUsuario(usuario)} style={{ background: 'none', border: 'none', cursor: 'pointer', fontSize: '18px', marginRight: '10px' }} title="Editar">✏️</button>
                    <button onClick={() => deletarUsuario(usuario.id)} style={{ background: 'none', border: 'none', cursor: 'pointer', fontSize: '18px' }} title="Deletar">🗑</button>
                  </td>
                </tr>
              ))
            ) : (
              <tr>
                <td colSpan="5" style={{ padding: '20px', textAlign: 'center', color: '#7f8c8d' }}>
                  Nenhum usuário encontrado.
                </td>
              </tr>
            )}
          </tbody>
        </table>
      </div>
    </Layout>
  );
};

export default UsuarioManager;