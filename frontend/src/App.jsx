import React from 'react';
import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';


import Login from './pages/Home/Login';
import Dashboard from './pages/Dashboard/Dashboard';
import ProdutoManager from './pages/Produto/ProdutoManager'; 
import CategoriaManager from './pages/Categoria/CategoriaManager'; 
import MovimentacaoManager from './pages/Movimentacao/MovimentacaoManager';
import UsuarioManager from './pages/Usuario/UsuarioManager'
import Perfil from './pages/Perfil/Perfil'
import Layout from './pages/Layout/Layout'
import PreferenciaManager from './pages/Preferencias/PreferenciasManager';
import AlteraSenhaManager from './pages/AlteraSenha/AlteraSenhaManager';

function App() {
  return (
    <BrowserRouter>
      <div className="app-container">
        <Routes>
          <Route path="/" element={<Navigate to="/login" replace />} />
          
          <Route path="/login" element={<Login />} />
          <Route path="/dashboard" element={<Dashboard />} />
          <Route path="/produtos" element={<ProdutoManager />} />
          <Route path="/categorias" element={<CategoriaManager />} />
          <Route path="/movimentacoes" element={<MovimentacaoManager />} />
          <Route path="/usuario" element={<UsuarioManager />} />
          <Route path="/perfil" element={<Layout><Perfil /></Layout>} />
          <Route path="/preferencias" element={<Layout><PreferenciaManager /></Layout>} />
          <Route path="/alteraSenha" element={<Layout><AlteraSenhaManager /></Layout>} />

        </Routes>
      </div>
    </BrowserRouter>
  );
}

export default App;