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
import Notificacao from './pages/Notificacao/Notificacao';
import ClientesList from './pages/Cliente/ClientesList';
import ClienteForm from './pages/Cliente/ClienteForm';
import ClienteDetalhes from './pages/Cliente/ClienteDetalhes';
import VendaDetalhes from './pages/Vendas/VendaDetalhes';
import VendaForm from './pages/Vendas/VendaForm';
import VendaList from './pages/Vendas/VendaList';

import Cadastro from './pages/Home/Cadastro'

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
          <Route path="/notificacoes" element={<Layout><Notificacao /></Layout>} />
          
          <Route path="/clientes" element={<ClientesList />} />
          <Route path="/clientes/novo" element={<ClienteForm />} />
          <Route path="/clientes/editar/:id" element={<ClienteForm />} />
          <Route path="/clientes/:id" element={<ClienteDetalhes />} />
          
          <Route path="/vendas" element={<VendaList />} />
          <Route path="/vendas/nova" element={<VendaForm />} />
          <Route path="/vendas/:id" element={<VendaDetalhes />} />

          <Route path="/cadastro" element={<Cadastro />} />

        </Routes>
      </div>
    </BrowserRouter>
  );
}

export default App;