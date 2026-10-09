import React, { useState } from 'react';
import { API_BASE_URL } from '../services/api';

const Cadastro = () => {
  const [formData, setFormData] = useState({
    nome: '',
    email: '',
    senha: '',
    confirmacaoSenha: ''
  });
  const [erro, setErro] = useState('');
  const [sucesso, setSucesso] = useState('');

  const handleChange = (e) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setErro('');
    setSucesso('');

    if (formData.senha !== formData.confirmacaoSenha) {
      setErro('As senhas não coincidem!');
      return;
    }

    try {
      const response = await fetch(`${API_BASE_URL}/usuarios`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(formData),
      });

      if (response.ok) {
        setSucesso('Usuário cadastrado com sucesso!');
        setFormData({ nome: '', email: '', senha: '', confirmacaoSenha: '' });
      } else {
        setErro('Erro ao cadastrar. Verifique os dados ou se o e-mail já existe.');
      }
    } catch (error) {
      setErro('Erro de conexão com o servidor.');
    }
  };

  return (
    <div className="auth-container">
      <div class="logo">
        <h1>ERP</h1>
        <p>Controle de Estoque e Gestão</p>
      </div>

      <h2 class='auth-title'>Criar Conta</h2>
      {erro && <p style={{ color: 'red' }}>{erro}</p>}
      {sucesso && <p style={{ color: 'green' }}>{sucesso}</p>}
      
      <form class='auth-form' onSubmit={handleSubmit}>
        <div class='input-group'>
          <label>Nome:</label>
          <input type="text" name="nome" value={formData.nome} onChange={handleChange} required />
        </div>
        <div class='input-group'>
          <label>Email:</label>
          <input type="email" name="email" value={formData.email} onChange={handleChange} required />
        </div>
        <div class='input-group'>
          <label>Senha:</label>
          <input type="password" name="senha" value={formData.senha} onChange={handleChange} required />
        </div>
        <div class='input-group'>
          <label>Confirmar Senha:</label>
          <input type="password" name="confirmacaoSenha" value={formData.confirmacaoSenha} onChange={handleChange} required />
        </div>
        <button class="btn-auth" type="submit">Cadastrar</button>
      </form>
    </div>
  );
};

export default Cadastro;