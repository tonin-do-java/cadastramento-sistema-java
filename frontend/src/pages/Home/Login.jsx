import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { jwtDecode } from "jwt-decode";
import { API_BASE_URL } from '../services/api';
import './style.css';

const Login = () => {
  const [credenciais, setCredenciais] = useState({
    email: '',
    senha: ''
  });
  
  const [erro, setErro] = useState('');

  const navigate = useNavigate();

  const handleChange = (e) => {
    setCredenciais({ ...credenciais, [e.target.name]: e.target.value });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setErro('');

    try {
      const response = await fetch(`${API_BASE_URL}/auth/login`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(credenciais),
      });

      if (response.ok) {
        const data = await response.json();

        localStorage.setItem('tokenJWT', data.token);

        try {
          const payloadBase64 = data.token.split('.')[1]; 
          const payloadDecoded = jwtDecode(data.token); 
          
          console.log("O que tem DENTRO do token:", payloadDecoded);

          const nomeDoUsuario = payloadDecoded.nome;
          const emailUsuario = payloadDecoded.sub;
          
          const idExtraido = payloadDecoded.id || payloadDecoded.usuarioId || payloadDecoded.userId;
          if (idExtraido) {
            localStorage.setItem('usuarioId', idExtraido);
          }
          
          localStorage.setItem('nomeUsuario', nomeDoUsuario);
          localStorage.setItem('emailUsuario', emailUsuario)

        } catch (err) {
          console.error("Não foi possível decodificar o token", err);
          localStorage.setItem('nomeUsuario', 'Usuário');
        }

        navigate('/dashboard');
        
      } else {
        setErro('Credenciais inválidas!');
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

      <h2 class='auth-title'>Entrar</h2>
      {erro && <p style={{ color: 'red' }}>{erro}</p>}
      
      <form class= 'auth-form' onSubmit={handleSubmit}>
        <div class='input-group'>
          <label>Email:</label>
          <input type="email" name="email" value={credenciais.email} onChange={handleChange} required />
        </div>
        <div class='input-group'>
          <label>Senha:</label>
          <input type="password" name="senha" value={credenciais.senha} onChange={handleChange} required />
        </div>
        
        <button class='btn-auth' type="submit">Entrar</button>

        <div class='auth-footer'>
            Não tem uma conta? <a href="/cadastro">Cadastre-se aqui</a>
        </div>

      </form>
    </div>
  );
};

export default Login;